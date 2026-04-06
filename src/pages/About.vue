<template>
  <div class="about-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <span class="hero-badge">VỀ CHÚNG TÔI</span>
        <h1 class="hero-title">Món Ngon Chất Lượng<br />Mỗi Ngày</h1>
        <p class="hero-subtitle">Trải nghiệm ẩm thực đậm đà, phục vụ tận tâm</p>
        <div class="hero-stats">
          <div class="stat-item">
            <i class="fas fa-award"></i>
            <div>
              <strong>10+</strong>
              <span>Giải thưởng</span>
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
        <img src="../assets/images/taco-chefcartoon.png" alt="PDQ Chef" />
        <div class="hero-decoration"></div>
      </div>
    </section>

    <!-- Story Section -->
    <section class="story-section">
      <div class="container">
        <div class="story-content">
          <div class="story-text">
            <span class="section-badge">CÂU CHUYỆN</span>
            <h2 class="section-title">Về Nhà Hàng PDQ</h2>
            <p>
              Nhà hàng <strong>PDQ</strong> được thành lập năm 2025 tại Việt Nam
              bởi <strong>các đầu bếp hàng đầu</strong>. Với niềm đam mê ẩm thực
              và sự tin yêu của khách hàng, PDQ đã mở rộng khắp trong nước và
              quốc tế.
            </p>
            <p>
              Chúng tôi mang đến trải nghiệm ẩm thực Việt Nam đậm đà, với những
              nguyên liệu tươi ngon và hương vị truyền thống vốn quen thuộc
              trong từng món ăn. Tại PDQ, chúng tôi luôn đặt chất lượng lên hàng
              đầu — phục vụ nhanh chóng, chu đáo và đầy tâm huyết, để mỗi bữa ăn
              của bạn luôn trọn vẹn và đáng nhớ.
            </p>
            <div class="story-features">
              <div class="feature-item">
                <i class="fas fa-check-circle"></i>
                <span>Nguyên liệu tươi mỗi ngày</span>
              </div>
              <div class="feature-item">
                <i class="fas fa-check-circle"></i>
                <span>Đầu bếp chuyên nghiệp</span>
              </div>
              <div class="feature-item">
                <i class="fas fa-check-circle"></i>
                <span>Phục vụ tận tâm 24/7</span>
              </div>
            </div>
          </div>
          <div class="story-images">
            <div class="image-grid">
              <img
                src="../assets/images/about1.png"
                alt="Food 1"
                class="grid-img img-1"
              />
              <img
                src="../assets/images/about2.png"
                alt="Food 2"
                class="grid-img img-2"
              />
              <img
                src="../assets/images/about3.png"
                alt="Food 3"
                class="grid-img img-3"
              />
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Values Section -->
    <section class="values-section">
      <div class="container">
        <span class="section-badge">GIÁ TRỊ CỐT LÕI</span>
        <h2 class="section-title">Điều Chúng Tôi Tin Tưởng</h2>
        <div class="values-grid">
          <div class="value-card">
            <div class="value-icon">
              <i class="fas fa-heart"></i>
            </div>
            <h3>Tình Yêu</h3>
            <p>
              Mỗi món ăn được chế biến với tình yêu và sự tận tâm của đầu bếp
            </p>
          </div>
          <div class="value-card">
            <div class="value-icon">
              <i class="fas fa-leaf"></i>
            </div>
            <h3>Tươi Ngon</h3>
            <p>Nguyên liệu tươi sống được chọn lọc kỹ càng mỗi ngày</p>
          </div>
          <div class="value-card">
            <div class="value-icon">
              <i class="fas fa-bolt"></i>
            </div>
            <h3>Nhanh Chóng</h3>
            <p>Giao hàng nhanh, đúng giờ, đảm bảo món ăn còn nóng</p>
          </div>
          <div class="value-card">
            <div class="value-icon">
              <i class="fas fa-shield-alt"></i>
            </div>
            <h3>An Toàn</h3>
            <p>Quy trình chế biến đảm bảo vệ sinh an toàn thực phẩm</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Team Section -->
    <section class="team-section">
      <div class="container">
        <span class="section-badge">ĐỘI NGŨ</span>
        <h2 class="section-title">Những Người Tạo Nên PDQ</h2>
        <p class="section-subtitle">Đội ngũ phát triển đầy nhiệt huyết 💚</p>

        <div class="team-grid">
          <div class="team-card" v-for="(member, index) in team" :key="index">
            <div class="team-image">
              <img :src="getTeamImage(member.img)" :alt="member.name" />
              <div class="team-overlay">
                <div class="social-links">
                  <a href="#" class="social-link" @click.prevent>
                    <i class="fab fa-facebook-f"></i>
                  </a>
                  <a href="#" class="social-link" @click.prevent>
                    <i class="fab fa-instagram"></i>
                  </a>
                  <a href="#" class="social-link" @click.prevent>
                    <i class="fab fa-github"></i>
                  </a>
                  <a href="#" class="social-link" @click.prevent>
                    <i class="fab fa-linkedin-in"></i>
                  </a>
                </div>
              </div>
            </div>
            <div class="team-info">
              <h3 class="team-name">{{ member.name }}</h3>
              <p class="team-role">{{ member.role }}</p>
              <p class="team-desc">{{ member.desc }}</p>
              <div class="team-skills">
                <span
                  v-for="(skill, idx) in member.skills"
                  :key="idx"
                  class="skill-tag"
                >
                  {{ skill }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Quote Section -->
    <section class="quote-section">
      <div class="quote-content">
        <i class="fas fa-quote-left quote-icon"></i>
        <h2 class="quote-text">Ẩm thực là sợi dây kết nối mọi người</h2>
      </div>
    </section>

    <!-- Gallery Section -->

    <!-- CTA Section -->
    <section class="cta-section">
      <div class="cta-content">
        <h2>Sẵn sàng trải nghiệm?</h2>
        <p>Đặt món ngay hôm nay và cảm nhận sự khác biệt</p>
        <router-link to="/menu" class="cta-button">
          <i class="fas fa-utensils"></i>
          Xem thực đơn
        </router-link>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: "About",

  data() {
    return {
      team: [
        {
          name: "Châu Tấn Đạt",
          role: "Frontend Developer",
          desc: "Thiết kế giao diện hiện đại, tối ưu trải nghiệm người dùng.",
          img: "member1.jpg",
        },
        {
          name: "Nguyễn Võ Tấn Phương",
          role: "Backend Developer",
          desc: "Xây dựng API nhanh, bảo mật, đảm bảo dữ liệu ổn định.",
          img: "member2.jpg",
        },
        {
          name: "Nguyễn Phú Minh Quân",
          role: "Full Stack Developer",
          desc: "Kết nối frontend và backend, đảm bảo hệ thống hoạt động mượt mà.",
          img: "member3.jpg",
        },
      ],
    };
  },

  methods: {
    getTeamImage(filename) {
      // Fallback to placeholder if image not found
      try {
        return require(`../assets/images/team/${filename}`);
      } catch (e) {
        return `https://ui-avatars.com/api/?name=${
          filename.split(".")[0]
        }&size=400&background=00b067&color=fff&bold=true`;
      }
    },

    openGallery(index) {
      console.log("Opening gallery at index:", index);
      // Có thể thêm lightbox ở đây
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

.about-page {
  background: #fff;
}

/* Container */
.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 5%;
}

/* Section Badge */
.section-badge {
  display: inline-block;
  background: linear-gradient(135deg, #00b067 0%, #00d97e 100%);
  color: white;
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  margin-bottom: 16px;
}

.section-title {
  font-size: 40px;
  font-weight: 900;
  color: #1a1a1a;
  margin-bottom: 16px;
  line-height: 1.2;
}

.section-subtitle {
  font-size: 18px;
  color: #666;
  margin-bottom: 48px;
}

/* Hero Section */
.hero-section {
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  padding: 80px 5%;
  display: flex;
  align-items: center;
  gap: 60px;
  min-height: 600px;
  position: relative;
  overflow: hidden;
}

.hero-content {
  flex: 1;
  z-index: 2;
}

.hero-badge {
  display: inline-block;
  background: rgba(0, 176, 103, 0.1);
  color: #00b067;
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 2px;
  margin-bottom: 20px;
}

.hero-title {
  font-size: 56px;
  font-weight: 900;
  color: #1a1a1a;
  margin-bottom: 20px;
  line-height: 1.1;
}

.hero-subtitle {
  font-size: 20px;
  color: #666;
  margin-bottom: 40px;
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
  font-size: 36px;
  color: #00b067;
}

.stat-item strong {
  display: block;
  font-size: 28px;
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
  z-index: 2;
}

.hero-image img {
  width: 100%;
  max-width: 500px;
  filter: drop-shadow(0 20px 40px rgba(0, 0, 0, 0.15));
  animation: float 3s ease-in-out infinite;
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

.hero-decoration {
  position: absolute;
  width: 400px;
  height: 400px;
  background: radial-gradient(
    circle,
    rgba(0, 176, 103, 0.1) 0%,
    transparent 70%
  );
  border-radius: 50%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
}

/* Story Section */
.story-section {
  padding: 100px 5%;
  background: white;
}

.story-content {
  display: flex;
  gap: 80px;
  align-items: center;
}

.story-text {
  flex: 1;
}

.story-text p {
  font-size: 17px;
  line-height: 1.8;
  color: #444;
  margin-bottom: 20px;
}

.story-features {
  margin-top: 32px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.feature-item i {
  font-size: 20px;
  color: #00b067;
}

.story-images {
  flex: 1;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  position: relative;
}

.grid-img {
  width: 100%;
  height: 250px;
  object-fit: cover;
  border-radius: 20px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.grid-img:hover {
  transform: scale(1.05);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
}

.img-1 {
  grid-column: span 2;
}

/* Values Section */
.values-section {
  padding: 100px 5%;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  text-align: center;
}

.values-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 32px;
  margin-top: 48px;
}

.value-card {
  background: white;
  padding: 40px 28px;
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.value-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

.value-icon {
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

.value-icon i {
  font-size: 32px;
  color: white;
}

.value-card h3 {
  font-size: 22px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 12px;
}

.value-card p {
  font-size: 15px;
  color: #666;
  line-height: 1.6;
}

/* Team Section */
.team-section {
  padding: 100px 5%;
  background: white;
  text-align: center;
}

.team-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 40px;
  margin-top: 48px;
}

.team-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.team-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.team-image {
  position: relative;
  height: 300px;
  overflow: hidden;
}

.team-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.team-card:hover .team-image img {
  transform: scale(1.1);
}

.team-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    135deg,
    rgba(0, 176, 103, 0.95) 0%,
    rgba(0, 217, 126, 0.95) 100%
  );
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.team-card:hover .team-overlay {
  opacity: 1;
}

.social-links {
  display: flex;
  gap: 16px;
}

.social-link {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  transition: all 0.3s ease;
}

.social-link:hover {
  background: white;
  color: #00b067;
  transform: translateY(-4px);
}

.team-info {
  padding: 28px;
  text-align: left;
}

.team-name {
  font-size: 22px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.team-role {
  font-size: 14px;
  font-weight: 600;
  color: #00b067;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 12px;
}

.team-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 16px;
}

.team-skills {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.skill-tag {
  background: #f0fdf4;
  color: #00b067;
  padding: 6px 14px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
}

/* Quote Section */
.quote-section {
  padding: 100px 5%;
  background: linear-gradient(135deg, #00b067 0%, #00d97e 100%);
  text-align: center;
  color: white;
  position: relative;
  overflow: hidden;
}

.quote-content {
  max-width: 900px;
  margin: 0 auto;
  position: relative;
  z-index: 2;
}

.quote-icon {
  font-size: 48px;
  opacity: 0.3;
  margin-bottom: 24px;
}

.quote-text {
  font-size: 36px;
  font-weight: 700;
  line-height: 1.4;
  margin-bottom: 20px;
  font-style: italic;
}

.quote-author {
  font-size: 18px;
  opacity: 0.9;
}

/* Gallery Section */
.gallery-section {
  padding: 100px 5%;
  background: white;
  text-align: center;
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-top: 48px;
}

.gallery-item {
  position: relative;
  height: 280px;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
}

.gallery-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.gallery-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.gallery-item:hover .gallery-overlay {
  opacity: 1;
}

.gallery-item:hover img {
  transform: scale(1.1);
}

.gallery-overlay i {
  color: white;
  font-size: 32px;
}

/* CTA Section */
.cta-section {
  padding: 100px 5%;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  text-align: center;
}

.cta-content h2 {
  font-size: 40px;
  font-weight: 900;
  color: #1a1a1a;
  margin-bottom: 16px;
}

.cta-content p {
  font-size: 18px;
  color: #666;
  margin-bottom: 32px;
}

.cta-button {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  background: linear-gradient(135deg, #00b067 0%, #00d97e 100%);
  color: white;
  padding: 18px 40px;
  border-radius: 12px;
  text-decoration: none;
  font-size: 18px;
  font-weight: 700;
  box-shadow: 0 8px 24px rgba(0, 176, 103, 0.3);
  transition: all 0.3s ease;
}

.cta-button:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 176, 103, 0.4);
}

/* Responsive */
@media (max-width: 1024px) {
  .hero-section {
    flex-direction: column;
    text-align: center;
    padding: 60px 5%;
  }

  .hero-title {
    font-size: 44px;
  }

  .hero-stats {
    justify-content: center;
  }

  .story-content {
    flex-direction: column;
  }

  .section-title {
    font-size: 32px;
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 36px;
  }

  .section-title {
    font-size: 28px;
  }

  .quote-text {
    font-size: 28px;
  }

  .values-grid,
  .team-grid {
    grid-template-columns: 1fr;
  }

  .gallery-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  }
}
</style>
