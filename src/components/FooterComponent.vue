<template>
  <div class="footer">
    <!-- Newsletter Section - Compact -->
    <div class="news-letter">
      <h3>Nhận thông tin mới nhất</h3>
      <form @submit.prevent>
        <input
          type="email"
          name="useremailreceiveinfo"
          placeholder="Nhập email của bạn"
          id="useremailreceiveinfo"
        />
        <input type="submit" value="Đăng ký" />
      </form>
    </div>

    <!-- Links Section - Compact Grid -->
    <div class="box-container">
      <div class="box">
        <h3>Liên kết</h3>
        <router-link @click="scrollToTop()" to="/">
          <i class="fas fa-arrow-right"></i> Trang chủ
        </router-link>
        <router-link @click="scrollToTop()" to="/about">
          <i class="fas fa-arrow-right"></i> Giới thiệu
        </router-link>
        <router-link @click="scrollToTop()" to="/menu">
          <i class="fas fa-arrow-right"></i> Thực đơn
        </router-link>
      </div>

      <div class="box">
        <h3>Tài khoản</h3>
        <div v-if="user">
          <router-link @click="scrollToTop()" to="/cart">
            <i class="fas fa-arrow-right"></i> Giỏ hàng
          </router-link>
          <router-link @click="scrollToTop()" to="/my-order">
            <i class="fas fa-arrow-right"></i> Đơn hàng
          </router-link>
          <router-link @click="scrollToTop()" to="/profile">
            <i class="fas fa-arrow-right"></i> Hồ sơ
          </router-link>
        </div>
        <div v-else>
          <router-link @click="scrollToTop()" to="/login">
            <i class="fas fa-arrow-right"></i> Đăng nhập
          </router-link>
          <router-link @click="scrollToTop()" to="/register">
            <i class="fas fa-arrow-right"></i> Đăng ký
          </router-link>
        </div>
      </div>

      <div class="box">
        <h3>Giờ mở cửa</h3>
        <p><i class="fas fa-clock"></i> Thứ 2 - Chủ nhật</p>
        <p><strong>7:00 - 22:00</strong></p>
        <div class="social-compact">
          <a href="https://www.facebook.com/" class="fab fa-facebook-f"></a>
          <a href="https://twitter.com/" class="fab fa-twitter"></a>
          <a href="https://www.instagram.com/" class="fab fa-instagram"></a>
        </div>
      </div>
    </div>

    <!-- Copyright - Simple -->
    <div class="bottom">
      <p>© 2024 PDQ Restaurant. All rights reserved.</p>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";

export default {
  name: "FooterComponent",

  computed: {
    ...mapState(["user"]),
  },

  methods: {
    scrollToTop() {
      window.scrollTo({ top: 0, behavior: "smooth" });
    },
  },
};
</script>

<style scoped>
/* ========================================
   FOOTER - COMPACT VERSION
   ======================================== */
.footer {
  background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%);
  padding: 2rem 7%;
  color: #fff;
  font-size: 1rem;
}

/* ========================================
   NEWSLETTER - COMPACT
   ======================================== */
.news-letter {
  text-align: center;
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.news-letter h3 {
  font-size: 1.7rem;
  color: #fff;
  font-weight: 600;
  margin-bottom: 1rem;
}

.news-letter form {
  max-width: 500px;
  margin: 0 auto;
  display: flex;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.news-letter form input[type="email"] {
  flex: 1;
  padding: 0.75rem 1rem;
  font-size: 1rem;
  color: #333;
  border: none;
  outline: none;
  background: #fff;
}

.news-letter form input[type="email"]::placeholder {
  color: #999;
}

.news-letter form input[type="submit"] {
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  font-weight: 600;
  color: #fff;
  background: #27ae60;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.news-letter form input[type="submit"]:hover {
  background: #219150;
}

/* ========================================
   BOX CONTAINER - COMPACT GRID
   ======================================== */
.box-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.box h3 {
  font-size: 1.25rem;
  color: #27ae60;
  font-weight: 700;
  margin-bottom: 1rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.box p {
  font-size: 1rem;
  color: #ccc;
  margin: 0.5rem 0;
  line-height: 1.6;
}

.box p strong {
  color: #fff;
  font-size: 1rem;
}

.box p i {
  color: #27ae60;
  margin-right: 0.5rem;
}

.box a {
  display: block;
  font-size: 1rem;
  color: #ccc;
  padding: 0.4rem 0;
  transition: all 0.2s ease;
  text-decoration: none;
}

.box a:hover {
  color: #27ae60;
  padding-left: 0.5rem;
}

.box a i {
  font-size: 0.7rem;
  margin-right: 0.5rem;
  color: #27ae60;
  transition: all 0.2s ease;
}

.box a:hover i {
  margin-right: 1rem;
}

/* Social Icons - Compact */
.social-compact {
  display: flex;
  gap: 0.75rem;
  margin-top: 1rem;
}

.social-compact a {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(39, 174, 96, 0.2);
  color: #27ae60;
  font-size: 0.9rem;
  transition: all 0.3s ease;
  padding: 0;
}

.social-compact a:hover {
  background: #27ae60;
  color: #fff;
  transform: translateY(-2px);
  padding: 0;
}

/* ========================================
   BOTTOM - SIMPLE COPYRIGHT
   ======================================== */
.bottom {
  text-align: center;
  padding-top: 1.5rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.bottom p {
  font-size: 0.95rem;
  color: #999;
  margin: 0;
}

/* ========================================
   RESPONSIVE
   ======================================== */
@media (max-width: 768px) {
  .footer {
    padding: 1.5rem 5%;
  }

  .news-letter h3 {
    font-size: 1.3rem;
  }

  .news-letter form {
    max-width: 100%;
  }

  .news-letter form input[type="email"],
  .news-letter form input[type="submit"] {
    font-size: 0.85rem;
    padding: 0.65rem 1rem;
  }

  .box-container {
    grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
    gap: 1.5rem;
  }

  .box h3 {
    font-size: 1rem;
  }

  .box a,
  .box p {
    font-size: 0.8rem;
  }

  .social-compact a {
    width: 28px;
    height: 28px;
    font-size: 0.8rem;
  }
}

@media (max-width: 480px) {
  .footer {
    padding: 1.5rem 4%;
  }

  .box-container {
    grid-template-columns: 1fr;
    gap: 1.5rem;
    text-align: center;
  }

  .box a {
    padding: 0.3rem 0;
  }

  .box a:hover {
    padding-left: 0;
  }

  .social-compact {
    justify-content: center;
  }

  .news-letter form {
    flex-direction: column;
    gap: 0.5rem;
  }

  .news-letter form input[type="submit"] {
    width: 100%;
  }
}
</style>
