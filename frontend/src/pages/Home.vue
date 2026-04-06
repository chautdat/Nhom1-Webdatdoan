<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-background">
        <div class="hero-circle circle-1"></div>
        <div class="hero-circle circle-2"></div>
        <div class="hero-circle circle-3"></div>
      </div>

      <div class="hero-container">
        <div class="hero-content">
          <span class="hero-badge">
            <i class="fas fa-sparkles"></i>
            Chào mừng đến với PDQ!
          </span>
          <h1 class="hero-title">Ngon Như<br />Nhà Làm 😋</h1>
          <p class="hero-description">
            Chúng tôi cam kết mang đến những món ăn được chế biến từ nguyên liệu
            tươi ngon, đảm bảo chất lượng hàng đầu. Thưởng thức ẩm thực Việt Nam
            với hương vị bùng nổ và tinh tế ngay tại nhà bạn!
          </p>
          <div class="hero-buttons">
            <router-link to="/menu" class="primary-btn" @click="scrollToTop">
              <i class="fas fa-utensils"></i>
              Đặt món ngay
            </router-link>
            <router-link to="/about" class="secondary-btn" @click="scrollToTop">
              <i class="fas fa-info-circle"></i>
              Tìm hiểu thêm
            </router-link>
          </div>

          <!-- Stats -->
          <div class="hero-stats">
            <div class="stat-item">
              <i class="fas fa-utensils"></i>
              <div>
                <strong>100+</strong>
                <span>Món ăn</span>
              </div>
            </div>
            <div class="stat-item">
              <i class="fas fa-users"></i>
              <div>
                <strong>50K+</strong>
                <span>Khách hàng</span>
              </div>
            </div>
            <div class="stat-item">
              <i class="fas fa-star"></i>
              <div>
                <strong>4.9/5</strong>
                <span>Đánh giá</span>
              </div>
            </div>
          </div>
        </div>

        <div class="hero-image">
          <div class="image-wrapper">
            <img
              src="../assets/images/b.png"
              alt="PDQ Food"
              class="main-image"
              loading="lazy"
              decoding="async"
              @error="onImgError"
            />
            <!-- GIỮ nguyên ảnh và class, chỉ thêm lazy + fallback -->
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features-section">
      <div class="container">
        <div class="section-header">
          <span class="section-badge">ĐẶC ĐIỂM</span>
          <h2 class="section-title">Tại Sao Chọn PDQ?</h2>
          <p class="section-subtitle">
            Khám phá lý do khiến khách hàng luôn tin tưởng chúng tôi
          </p>
        </div>

        <div class="features-grid">
          <div
            class="feature-card"
            v-for="(feature, index) in features"
            :key="index"
          >
            <div class="feature-icon">
              <i :class="feature.icon"></i>
            </div>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- About Section -->
    <section class="about-section">
      <div class="container">
        <div class="about-content">
          <div class="about-image">
            <img
              src="../assets/images/home1.png"
              alt="PDQ Restaurant"
              loading="lazy"
              decoding="async"
              @error="onImgError"
            />
            <div class="about-badge">
              <i class="fas fa-award"></i>
              <span>Chất lượng<br />hàng đầu</span>
            </div>
          </div>

          <div class="about-text">
            <span class="section-badge">VỀ CHÚNG TÔI</span>
            <h2 class="section-title">
              Khám Phá Bí Quyết<br />Tạo Nên Hương Vị Tuyệt Hảo
            </h2>
            <p>
              PDQ luôn cam kết mang đến những món ăn chất lượng cao nhất. Mỗi
              món ăn được chế biến tỉ mỉ với tâm huyết, mang lại hương vị bùng
              nổ, tinh tế và đáng nhớ. Dịch vụ giao hàng nhanh chóng giúp bạn
              tận hưởng trải nghiệm như tại nhà hàng.
            </p>
            <ul class="about-list">
              <li>
                <i class="fas fa-check-circle"></i>
                <span>Nguyên liệu tươi mỗi ngày</span>
              </li>
              <li>
                <i class="fas fa-check-circle"></i>
                <span>Đầu bếp chuyên nghiệp giàu kinh nghiệm</span>
              </li>
              <li>
                <i class="fas fa-check-circle"></i>
                <span>Giao hàng nhanh chóng, đúng giờ</span>
              </li>
              <li>
                <i class="fas fa-check-circle"></i>
                <span>Hỗ trợ khách hàng 24/7</span>
              </li>
            </ul>
            <router-link to="/about" class="primary-btn" @click="scrollToTop">
              <i class="fas fa-info-circle"></i>
              Xem thêm về chúng tôi
            </router-link>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta-section">
      <div class="cta-content">
        <h2>Đói bụng rồi?</h2>
        <p>Đặt món ngay để thưởng thức bữa ăn nóng hổi tại nhà!</p>
        <router-link to="/menu" class="cta-button" @click="scrollToTop">
          <i class="fas fa-shopping-bag"></i>
          Đặt món ngay
        </router-link>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: "Home",
  data() {
    return {
      // fallback 1x1 trong suốt để tránh icon ảnh vỡ
      fallbackImage:
        "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAusB9o1u3yQAAAAASUVORK5CYII=",

      features: [
        {
          icon: "fas fa-shipping-fast",
          title: "Giao Hàng Nhanh",
          description: "Giao hàng trong 30 phút, đảm bảo món ăn còn nóng",
        },
        {
          icon: "fas fa-leaf",
          title: "Nguyên Liệu Tươi Ngon",
          description:
            "Chọn lọc nguyên liệu tươi sống mỗi ngày từ nguồn tin cậy",
        },
        {
          icon: "fas fa-award",
          title: "Chất Lượng Hàng Đầu",
          description: "Đạt chuẩn vệ sinh ATTP, được kiểm định nghiêm ngặt",
        },
        {
          icon: "fas fa-headset",
          title: "Hỗ Trợ 24/7",
          description: "Đội ngũ CSKH sẵn sàng hỗ trợ bạn mọi lúc mọi nơi",
        },
      ],
    };
  },
  methods: {
    scrollToTop() {
      window.scrollTo({ top: 0, behavior: "smooth" });
    },
    onImgError(e) {
      e.target.src = this.fallbackImage;
    },
  },
};
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.home-page {
  background: #fff;
  overflow-x: hidden;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 5%;
}

/* Section Header */
.section-header {
  text-align: center;
  margin-bottom: 60px;
}
.section-badge {
  display: inline-block;
  background: linear-gradient(135deg, #00b067 0%, #00d97e 100%);
  color: #fff;
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 1.5px;
  margin-bottom: 16px;
}
.section-title {
  font-size: 42px;
  font-weight: 900;
  color: #1a1a1a;
  margin-bottom: 12px;
  line-height: 1.2;
}
.section-subtitle {
  font-size: 18px;
  color: #666;
  max-width: 600px;
  margin: 0 auto;
}

/* Hero Section */
.hero-section {
  min-height: 90vh;
  display: flex;
  align-items: center;
  padding: 80px 5%;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
}
.hero-background {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}
.hero-circle {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(
    circle,
    rgba(0, 176, 103, 0.1) 0%,
    transparent 70%
  );
}
.circle-1 {
  width: 500px;
  height: 500px;
  top: -250px;
  left: -250px;
  animation: float 8s infinite ease-in-out;
}
.circle-2 {
  width: 400px;
  height: 400px;
  bottom: -200px;
  right: -200px;
  animation: float 10s infinite ease-in-out reverse;
}
.circle-3 {
  width: 300px;
  height: 300px;
  top: 50%;
  right: 10%;
  animation: float 12s infinite ease-in-out;
}

.hero-container {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 80px;
  position: relative;
  z-index: 2;
}
.hero-content {
  flex: 1;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(0, 176, 103, 0.1);
  color: #00b067;
  padding: 10px 20px;
  border-radius: 25px;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 24px;
}
.hero-title {
  font-size: 64px;
  font-weight: 900;
  color: #1a1a1a;
  line-height: 1.1;
  margin-bottom: 24px;
}
.hero-description {
  font-size: 18px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 36px;
  max-width: 550px;
}
.hero-buttons {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 48px;
}

.primary-btn {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  background: linear-gradient(135deg, #00b067 0%, #00d97e 100%);
  color: #fff;
  padding: 16px 32px;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 700;
  font-size: 16px;
  box-shadow: 0 8px 24px rgba(0, 176, 103, 0.3);
  transition: all 0.3s ease;
}
.primary-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(0, 176, 103, 0.4);
}
.secondary-btn {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  background: #fff;
  color: #00b067;
  padding: 16px 32px;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 700;
  font-size: 16px;
  border: 2px solid #00b067;
  transition: all 0.3s ease;
}
.secondary-btn:hover {
  background: #00b067;
  color: #fff;
  transform: translateY(-2px);
}

.hero-stats {
  display: flex;
  gap: 40px;
  flex-wrap: wrap;
}
.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
}
.stat-item i {
  font-size: 32px;
  color: #00b067;
}
.stat-item strong {
  display: block;
  font-size: 24px;
  font-weight: 900;
  color: #1a1a1a;
  line-height: 1;
}
.stat-item span {
  display: block;
  font-size: 13px;
  color: #666;
  margin-top: 4px;
}

.hero-image {
  flex: 1;
  position: relative;
}
.image-wrapper {
  position: relative;
}

/* Ảnh chính */
.main-image {
  width: 100%;
  max-width: 550px;
  animation: float 6s infinite ease-in-out;
  filter: drop-shadow(0 20px 40px rgba(0, 0, 0, 0.15));
}

/* Ảnh phở PNG: cắt rìa bằng mask elip để ẩn viền ô ca-rô, KHÔNG có nền trắng mờ */
.floating-image {
  position: absolute;
  top: -10%;
  right: -10%;
  width: 60%;
  opacity: 0.9;
  animation: spin 40s linear infinite;
  pointer-events: none;

  /* mask để che phần rìa checkerboard */
  -webkit-mask-image: radial-gradient(
    ellipse 82% 66% at 56% 56%,
    #000 62%,
    transparent 64%
  );
  mask-image: radial-gradient(
    ellipse 82% 66% at 56% 56%,
    #000 62%,
    transparent 64%
  );
}

/* KHÔNG dùng vòng sáng trắng */
.image-wrapper::before {
  content: none;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}
@keyframes spin {
  from {
    transform: rotate(0);
  }
  to {
    transform: rotate(360deg);
  }
}

/* Features Section */
.features-section {
  padding: 100px 5%;
  background: #fff;
}
.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 32px;
}
.feature-card {
  text-align: center;
  padding: 40px 24px;
  background: #f9f9f9;
  border-radius: 20px;
  transition: all 0.3s ease;
}
.feature-card:hover {
  background: #fff;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  transform: translateY(-8px);
}
.feature-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  background: linear-gradient(135deg, #00b067 0%, #00d97e 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(0, 176, 103, 0.3);
}
.feature-icon i {
  font-size: 32px;
  color: #fff;
}
.feature-card h3 {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 12px;
}
.feature-card p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

/* About Section */
.about-section {
  padding: 100px 5%;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
}
.about-content {
  display: flex;
  gap: 80px;
  align-items: center;
}
.about-image {
  flex: 1;
  position: relative;
}
.about-image img {
  width: 100%;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}
.about-badge {
  position: absolute;
  top: 20px;
  right: 20px;
  background: #fff;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  text-align: center;
}
.about-badge i {
  font-size: 32px;
  color: #00b067;
  margin-bottom: 8px;
  display: block;
}
.about-badge span {
  font-size: 13px;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1.3;
}

.about-text {
  flex: 1;
}
.about-text p {
  font-size: 16px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 32px;
}
.about-list {
  list-style: none;
  margin-bottom: 32px;
}
.about-list li {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  color: #333;
  margin-bottom: 16px;
}
.about-list i {
  font-size: 20px;
  color: #00b067;
}

/* CTA Section */
.cta-section {
  padding: 100px 5%;
  background: linear-gradient(135deg, #00b067 0%, #00d97e 100%);
  text-align: center;
  color: #fff;
}
.cta-content h2 {
  font-size: 48px;
  font-weight: 900;
  margin-bottom: 16px;
}
.cta-content p {
  font-size: 20px;
  margin-bottom: 36px;
  opacity: 0.9;
}
.cta-button {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  background: #fff;
  color: #00b067;
  padding: 18px 40px;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 700;
  font-size: 18px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}
.cta-button:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.3);
}

/* Responsive */
@media (max-width: 1024px) {
  .hero-container,
  .about-content {
    flex-direction: column;
    text-align: center;
  }
  .hero-title {
    font-size: 48px;
  }
  .section-title {
    font-size: 36px;
  }
  .hero-stats {
    justify-content: center;
  }
  .about-text {
    text-align: left;
  }
}
@media (max-width: 768px) {
  .hero-section {
    padding: 60px 5%;
  }
  .hero-title {
    font-size: 36px;
  }
  .section-title {
    font-size: 28px;
  }
  .cta-content h2 {
    font-size: 32px;
  }
}
</style>
