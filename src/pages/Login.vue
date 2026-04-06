<template>
  <div class="login-page">
    <div class="login-card">
      <h3 class="login-title">Đăng Nhập Tài Khoản</h3>

      <div v-if="errors.length" class="error-box">
        <ul>
          <li v-for="error in errors" :key="error">{{ error }}</li>
        </ul>
      </div>

      <form id="loginForm" @submit="handleSubmit" novalidate autocomplete="off">
        <div class="form-group">
          <input
            type="text"
            id="uEmail"
            name="uEmail"
            class="form-control lowercase-input"
            placeholder="Nhập email hoặc tên đăng nhập"
            v-model.trim="loginObj.email"
            autocapitalize="none"
            autocorrect="off"
            spellcheck="false"
            @input="loginObj.email = loginObj.email.toLowerCase()"
          />
        </div>

        <div class="form-group">
          <input
            type="password"
            id="uPass"
            name="uPass"
            class="form-control"
            placeholder="Nhập mật khẩu"
            v-model.trim="loginObj.pass"
            autocapitalize="off"
            autocorrect="off"
            spellcheck="false"
          />
        </div>

        <div class="form-group">
          <input type="submit" value="Đăng Nhập" class="btn" />
        </div>

        <p class="register-link">
          Chưa có tài khoản?
          <router-link @click="scrollToTop()" to="/register">
            Tạo tài khoản ngay
          </router-link>
        </p>
      </form>
    </div>
  </div>
</template>

<script>
import api from "@/axios";
import { mapMutations } from "vuex";
import storage, { STORAGE_KEYS } from "@/utils/storage";
import Swal from "sweetalert2";

export default {
  name: "Login",

  data() {
    return {
      loginObj: { email: "", pass: "" },
      errors: [],
    };
  },

  methods: {
    ...mapMutations(["setUser", "setAdmin"]),

    scrollToTop() {
      window.scrollTo(0, 0);
    },

    async handleSubmit(e) {
      e.preventDefault();
      this.errors = [];

      let identifier = (this.loginObj.email || "").trim().toLowerCase();
      const password = this.loginObj.pass;

      // Validate input
      if (!identifier) {
        this.errors.push("Vui lòng nhập email hoặc tên đăng nhập.");
      }

      if (identifier.includes("@")) {
        const emailRegex = /[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$/;
        if (!emailRegex.test(identifier)) {
          this.errors.push("Email không hợp lệ.");
        }
      }

      if (!password) {
        this.errors.push("Vui lòng nhập mật khẩu.");
      }

      if (this.errors.length > 0) return;

      try {
        const res = await api.post("/auth/login", {
          username: identifier,
          password: password,
        });

        const payload = res.data;

        if (!payload.success || !payload.data) {
          return this.errors.push(payload.message || "Đăng nhập thất bại!");
        }

        const auth = payload.data;
        const user = auth.user;
        const token = auth.token;
        const refreshToken = auth.refreshToken;

        if (!user || !token) {
          return this.errors.push("Không lấy được thông tin người dùng.");
        }

        // Lưu auth vào Vuex + localStorage
        this.$store.commit("setToken", token);
        this.setUser(user);

        const role = user.role?.toUpperCase() || "";
        const isAdmin = role.includes("ADMIN");
        this.setAdmin(isAdmin);

        if (refreshToken) {
          storage.set(STORAGE_KEYS.refreshToken, refreshToken);
        }

        // Popup thành công rồi redirect
        await Swal.fire({
          icon: "success",
          title: "Đăng nhập thành công!",
          text: `Chào mừng ${
            user.fullName || user.user_name || user.username || "bạn"
          }`,
          timer: 1200,
          showConfirmButton: false,
        });

        // Redirect theo role
        if (isAdmin) {
          this.$router.push("/admin");
        } else {
          this.$router.push("/");
        }
      } catch (err) {
        this.errors.push(
          err.response?.data?.message || "Email hoặc mật khẩu không chính xác!"
        );
      }
    },
  },
};
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap");

.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f0fff4, #ffffff);
  font-family: "Inter", sans-serif;
  padding: 2rem;
}

.login-card {
  background: #fff;
  padding: 3rem;
  border-radius: 1.5rem;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08);
  max-width: 420px;
  width: 100%;
  text-align: center;
  transition: all 0.3s ease;
}

.login-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.12);
}

.login-title {
  font-size: 2rem;
  font-weight: 700;
  color: #130f40;
  margin-bottom: 2rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.error-box {
  background-color: #fff3f3;
  border-left: 4px solid #ff4d4f;
  text-align: left;
  padding: 1.2rem 1.5rem;
  border-radius: 8px;
  margin-bottom: 2rem;
}

.error-box ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.error-box li {
  color: #d32f2f;
  font-size: 1.4rem;
  font-weight: 500;
  line-height: 1.5;
}

.form-group {
  margin-bottom: 2rem;
}

.form-control {
  width: 100%;
  padding: 1.3rem;
  font-size: 1.25rem;
  border: 2px solid #edf2f7;
  border-radius: 1rem;
  background: #f8f9fa;
  color: #333;
  transition: all 0.3s ease;
}

.lowercase-input {
  text-transform: lowercase !important;
}

.form-control:focus {
  background: #fff;
  border-color: #27ae60;
  box-shadow: 0 0 0 4px rgba(39, 174, 96, 0.1);
  outline: none;
}

.btn {
  background-color: #27ae60;
  color: #fff;
  font-size: 1.25rem;
  font-weight: 700;
  padding: 1.3rem;
  border-radius: 1rem;
  cursor: pointer;
  border: none;
  width: 100%;
  margin-top: 1rem;
  transition: all 0.3s ease;
  letter-spacing: 0.5px;
}

.btn:hover {
  background-color: #219150;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(39, 174, 96, 0.2);
}

.register-link {
  padding-top: 2rem;
  font-size: 1.1rem;
  color: #666;
  margin: 0;
  text-align: center;
}

.register-link a {
  color: #27ae60;
  font-weight: 600;
  transition: all 0.3s ease;
  text-decoration: none;
}

.register-link a:hover {
  color: #219150;
  text-decoration: underline;
}

@media (max-width: 480px) {
  .login-card {
    padding: 2.5rem;
  }
  .login-title {
    font-size: 1.7rem;
  }
  .form-control {
    padding: 1.1rem;
  }
  .btn {
    padding: 1.1rem;
  }
}
</style>
