<template>
  <div class="register-container">
    <div class="register-card">
      <h2 class="title">Tạo tài khoản của bạn</h2>

      <form
        id="registerForm"
        @submit="handleSubmit"
        novalidate
        autocomplete="off"
      >
        <!-- Họ tên -->
        <div class="form-group">
          <label for="uName">Họ và tên</label>
          <input
            type="text"
            id="uName"
            class="form-control"
            placeholder="Nhập họ và tên đầy đủ"
            v-model="registerObj.fullName"
          />
          <p class="error" v-if="errorObj.fullName.length">
            {{ errorObj.fullName[0] }}
          </p>
        </div>

        <!-- Email -->
        <div class="form-group">
          <label for="uEmail">Email</label>
          <input
            type="email"
            id="uEmail"
            class="form-control"
            placeholder="example@gmail.com"
            v-model="registerObj.email"
          />
          <p class="error" v-if="errorObj.email.length">
            {{ errorObj.email[0] }}
          </p>
        </div>

        <!-- Mật khẩu -->
        <div class="form-group">
          <label for="uPass">Mật khẩu</label>
          <input
            type="password"
            id="uPass"
            class="form-control"
            placeholder="Nhập mật khẩu"
            v-model="registerObj.password"
          />
          <p class="error" v-if="errorObj.password.length">
            {{ errorObj.password[0] }}
          </p>
        </div>

        <!-- Xác nhận mật khẩu -->
        <div class="form-group">
          <label for="uPassConfirm">Xác nhận mật khẩu</label>
          <input
            type="password"
            id="uPassConfirm"
            class="form-control"
            placeholder="Nhập lại mật khẩu"
            v-model="registerObj.confirm"
          />
          <p class="error" v-if="errorObj.confirm.length">
            {{ errorObj.confirm[0] }}
          </p>
        </div>

        <!-- Số điện thoại -->
        <div class="form-group">
          <label for="uPhone">Số điện thoại</label>
          <input
            type="tel"
            id="uPhone"
            class="form-control"
            placeholder="Nhập số điện thoại"
            v-model="registerObj.phone"
          />
          <p class="error" v-if="errorObj.phone.length">
            {{ errorObj.phone[0] }}
          </p>
        </div>

        <!-- Nút submit -->
        <button type="submit" class="btn-submit">Tham gia ngay</button>

        <p class="redirect">
          Đã có tài khoản?
          <router-link to="/login" @click="scrollToTop">Đăng nhập</router-link>
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
  name: "Register",
  data() {
    return {
      registerObj: {
        fullName: "",
        email: "",
        password: "",
        confirm: "",
        phone: "",
      },
      errorObj: {
        fullName: [],
        email: [],
        password: [],
        confirm: [],
        phone: [],
      },
    };
  },
  methods: {
    ...mapMutations(["setUser", "setAdmin"]),

    scrollToTop() {
      window.scrollTo(0, 0);
    },
    resetErrors() {
      Object.keys(this.errorObj).forEach((key) => (this.errorObj[key] = []));
    },
    validateForm() {
      this.resetErrors();
      // Họ tên
      if (!this.registerObj.fullName)
        this.errorObj.fullName.push("Vui lòng nhập họ tên");
      // Email
      if (!this.registerObj.email)
        this.errorObj.email.push("Vui lòng nhập email");
      else if (
        !/[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$/.test(this.registerObj.email)
      )
        this.errorObj.email.push("Email không hợp lệ");
      // Password
      if (!this.registerObj.password)
        this.errorObj.password.push("Vui lòng nhập mật khẩu");
      else if (this.registerObj.password.length < 6)
        this.errorObj.password.push("Mật khẩu phải có ít nhất 6 ký tự");
      // Confirm
      if (!this.registerObj.confirm)
        this.errorObj.confirm.push("Vui lòng xác nhận mật khẩu");
      else if (this.registerObj.confirm !== this.registerObj.password)
        this.errorObj.confirm.push("Mật khẩu xác nhận không khớp");
      // Phone
      if (!this.registerObj.phone)
        this.errorObj.phone.push("Vui lòng nhập số điện thoại");
      else if (!/^[0-9]{9,11}$/.test(this.registerObj.phone))
        this.errorObj.phone.push("Số điện thoại không hợp lệ");
    },
    checkValid() {
      return Object.values(this.errorObj).every((arr) => arr.length === 0);
    },
    async handleSubmit(e) {
      e.preventDefault();
      this.validateForm();
      if (!this.checkValid()) return;

      // Gọi API đăng ký backend
      try {
        const res = await api.post("/auth/register", {
          username: this.registerObj.email,
          fullName: this.registerObj.fullName,
          email: this.registerObj.email,
          password: this.registerObj.password,
          phone: this.registerObj.phone,
        });
        if (res.data.success) {
          const auth = res.data.data || {};
          const token = auth.token || null;
          const user = auth.user || null;
          const refreshToken = auth.refreshToken || null;

          if (token && user) {
            this.$store.commit("setToken", token);
            this.setUser(user);

            const role = user.role?.toUpperCase() || "";
            const isAdmin = role.includes("ADMIN");
            this.setAdmin(isAdmin);

            if (refreshToken) {
              storage.set(STORAGE_KEYS.refreshToken, refreshToken);
            }
          }

          this.registerObj = {
            fullName: "",
            email: "",
            password: "",
            confirm: "",
            phone: "",
          };
          this.resetErrors();

          await Swal.fire({
            icon: "success",
            title: "Đăng ký thành công!",
            text: "Tài khoản đã được kích hoạt và có thể sử dụng ngay.",
            timer: 1400,
            showConfirmButton: false,
          });

          if (token && user) {
            const role = (user.role || "").toUpperCase();
            this.$router.push(role.includes("ADMIN") ? "/admin" : "/");
          } else {
            this.$router.push("/login");
          }
        } else {
          // Hiển thị lỗi từ BE trả về
          this.errorObj.email.push(res.data.message || "Đăng ký thất bại!");
        }
      } catch (err) {
        this.errorObj.email.push(
          (err.response && err.response.data && err.response.data.message) ||
            "Đăng ký thất bại!"
        );
      }
    },
  },
};
</script>

<style scoped>
/* ... giữ nguyên toàn bộ phần CSS cũ của bạn ... */
input,
input::placeholder {
  text-transform: none !important;
}
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 4rem 2rem;
  background: linear-gradient(135deg, #f0fff4, #ffffff);
  min-height: 100vh;
}
.register-card {
  width: 100%;
  max-width: 800px;
  background: #fff;
  padding: 3rem;
  border-radius: 2rem;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.1);
  animation: fadeIn 0.4s ease;
  transition: all 0.3s ease;
}
.title {
  text-align: center;
  font-size: 2.6rem;
  color: #27ae60;
  font-weight: 800;
  margin-bottom: 2.2rem;
  letter-spacing: -0.5px;
}
form {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.8rem;
}
.form-group:nth-last-child(-n + 3) {
  grid-column: 1 / -1;
}
.form-group {
  margin-bottom: 0;
}
label {
  display: block;
  font-weight: 600;
  font-size: 1.3rem;
  color: #2c3e50;
  margin-bottom: 0.8rem;
  transition: all 0.2s;
}
.form-control {
  width: 100%;
  padding: 1.2rem 1.3rem;
  border-radius: 1.2rem;
  border: 2px solid #edf2f7;
  font-size: 1.2rem;
  outline: none;
  transition: all 0.3s ease;
  background: #f8fafc;
  height: auto;
}
.form-control:focus {
  border-color: #27ae60;
  background: #fff;
  box-shadow: 0 0 0 4px rgba(39, 174, 96, 0.1);
}
.gender-group {
  display: flex;
  align-items: center;
  gap: 4rem;
  font-size: 1.5rem;
  margin-top: 0.8rem;
  padding: 1rem 0;
}
.gender-group label {
  display: flex;
  align-items: center;
  margin: 0;
  cursor: pointer;
}
.gender-group input {
  transform: scale(1.3);
  margin-right: 1rem;
  cursor: pointer;
}
.error {
  color: #e74c3c;
  font-size: 1.1rem;
  margin-top: 0.6rem;
  font-weight: 500;
}
.btn-submit {
  width: 100%;
  padding: 1.3rem;
  background: linear-gradient(to right, #27ae60, #2ecc71);
  border: none;
  color: #fff;
  font-size: 1.2rem;
  font-weight: 700;
  border-radius: 1.2rem;
  margin-top: 0.5rem;
  cursor: pointer;
  transition: all 0.3s ease;
}
.btn-submit:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(39, 174, 96, 0.2);
}
.redirect {
  text-align: center;
  margin-top: 1.2rem;
  font-size: 1.1rem;
  color: #64748b;
}
.redirect a {
  color: #27ae60;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
  margin-left: 0.5rem;
}
.redirect a:hover {
  color: #219150;
  text-decoration: underline;
}
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
@media (max-width: 768px) {
  .register-card {
    padding: 2.5rem;
  }
  form {
    grid-template-columns: 1fr;
    gap: 1.6rem;
  }
  .title {
    font-size: 2rem;
    margin-bottom: 2rem;
  }
  .form-control {
    padding: 1.1rem;
    font-size: 1.2rem;
  }
  .btn-submit {
    padding: 1.2rem;
    font-size: 1.2rem;
  }
}
@media (max-width: 480px) {
  .register-container {
    padding: 2rem 1.5rem;
  }
  .register-card {
    padding: 1.8rem;
  }
  .title {
    font-size: 1.8rem;
  }
  .form-control {
    padding: 1.05rem;
    font-size: 1.2rem;
  }
  .gender-group {
    gap: 2rem;
  }
}
</style>
