<template>
  <vue-basic-alert :duration="300" :closeIn="2000" ref="alert" />

  <section class="feedback-page">
    <!-- Hero Section -->
    <div class="hero-section">
      <div class="hero-content">
        <span class="badge">Góp Ý & Liên Hệ</span>
        <h1 class="hero-title">Chúng Tôi Lắng Nghe Bạn</h1>
        <p class="hero-subtitle">
          Mọi ý kiến đóng góp của bạn đều giúp chúng tôi phục vụ tốt hơn
        </p>
      </div>
    </div>

    <div class="container">
      <!-- Contact Cards -->
      <div class="contact-cards">
        <div class="contact-card">
          <div class="card-icon">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
              />
            </svg>
          </div>
          <h3>Thời Gian Hoạt Động</h3>
          <p>Thứ 2 - Chủ Nhật</p>
          <p class="highlight">7:00 - 22:00</p>
        </div>

        <div class="contact-card">
          <div class="card-icon phone">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"
              />
            </svg>
          </div>
          <h3>Hotline</h3>
          <p class="highlight">+84 123 456 789</p>
          <p>Hỗ trợ 24/7</p>
        </div>

        <div class="contact-card">
          <div class="card-icon email">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
              />
            </svg>
          </div>
          <h3>Email</h3>
          <p class="highlight">support@pdq.vn</p>
          <p>Phản hồi trong 24h</p>
        </div>

        <div class="contact-card">
          <div class="card-icon location">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
              />
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
              />
            </svg>
          </div>
          <h3>Địa Chỉ</h3>
          <p>Khu Công Nghệ Cao TP.HCM</p>
          <p class="highlight">TP. Thủ Đức</p>
        </div>
      </div>

      <!-- Main Content Grid -->
      <div class="content-grid">
        <!-- Feedback Form -->
        <div class="form-wrapper">
          <div class="form-header">
            <h2>Gửi Góp Ý Của Bạn</h2>
            <p>Điền thông tin bên dưới, chúng tôi sẽ liên hệ sớm nhất</p>
          </div>

          <form
            id="feedbackForm"
            @submit="handleSubmit"
            novalidate
            autocomplete="off"
          >
            <div class="form-grid">
              <div class="form-group">
                <label for="fName">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                    />
                  </svg>
                  Họ và Tên
                </label>
                <input
                  type="text"
                  id="fName"
                  v-model="feedback.name"
                  placeholder="Nguyễn Văn A"
                  required
                />
                <span v-if="errors.name" class="error-msg">{{
                  errors.name
                }}</span>
              </div>

              <div class="form-group">
                <label for="fPhone">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M12 18h.01M8 21h8a2 2 0 002-2V5a2 2 0 00-2-2H8a2 2 0 00-2 2v14a2 2 0 002 2z"
                    />
                  </svg>
                  Số Điện Thoại
                </label>
                <input
                  type="text"
                  id="fPhone"
                  v-model="feedback.phone"
                  placeholder="0912 345 678"
                  required
                />
                <span v-if="errors.phone" class="error-msg">{{
                  errors.phone
                }}</span>
              </div>

              <div class="form-group">
                <label for="fEmail">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M16 12a4 4 0 10-8 0 4 4 0 008 0zm0 0v1.5a2.5 2.5 0 005 0V12a9 9 0 10-9 9m4.5-1.206a8.959 8.959 0 01-4.5 1.207"
                    />
                  </svg>
                  Email (Tuỳ chọn)
                </label>
                <input
                  type="email"
                  id="fEmail"
                  v-model="feedback.email"
                  placeholder="example@email.com"
                />
                <span v-if="errors.email" class="error-msg">{{
                  errors.email
                }}</span>
              </div>

              <div class="form-group">
                <label for="fSubject">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z"
                    />
                  </svg>
                  Chủ Đề
                </label>
                <input
                  type="text"
                  id="fSubject"
                  v-model="feedback.subject"
                  placeholder="Góp ý về chất lượng món ăn"
                  required
                />
                <span v-if="errors.subject" class="error-msg">{{
                  errors.subject
                }}</span>
              </div>

              <div class="form-group full-width">
                <label for="fMessage">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
                    />
                  </svg>
                  Nội Dung Góp Ý
                </label>
                <textarea
                  id="fMessage"
                  placeholder="Bạn muốn chúng tôi cải thiện điều gì? Chia sẻ chi tiết tại đây..."
                  v-model="feedback.message"
                  rows="5"
                  required
                ></textarea>
                <span v-if="errors.message" class="error-msg">{{
                  errors.message
                }}</span>
              </div>
            </div>

            <button class="submit-btn" type="submit" :disabled="submitting">
              <svg
                v-if="!submitting"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"
                />
              </svg>
              <svg
                v-else
                class="spinner"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
              >
                <circle
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  stroke-width="4"
                  opacity="0.25"
                ></circle>
                <path
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                  opacity="0.75"
                ></path>
              </svg>
              <span v-if="submitting">Đang gửi...</span>
              <span v-else>Gửi Góp Ý</span>
            </button>
          </form>
        </div>

        <!-- Google Map -->
        <div class="map-wrapper">
          <div class="map-header">
            <h2>Vị Trí Của Chúng Tôi</h2>
            <p>Khu Công Nghệ Cao TP.HCM, Phường Hiệp Phú, TP. Thủ Đức</p>
          </div>

          <div class="map-container">
            <iframe
              src="https://www.google.com/maps/embed?pb=!1m17!1m12!1m3!1d3918.3050945750673!2d106.78339607573662!3d10.855232354658958!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m2!1m1!2zMTDCsDUxJzE4LjgiTiAxMDbCsDQ3JzA4LjgiRQ!5e0!3m2!1svi!2s!4v1734445800000!5m2!1svi!2s"
              width="100%"
              height="100%"
              style="border: 0"
              allowfullscreen=""
              loading="lazy"
              referrerpolicy="no-referrer-when-downgrade"
            ></iframe>
          </div>

          <div class="address-details">
            <div class="address-item">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
                />
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
                />
              </svg>
              <div>
                <strong>Địa chỉ chi tiết:</strong>
                <p>Phân khu đào tạo E1, Khu Công nghệ cao TP.HCM</p>
                <p>Phường Hiệp Phú, TP. Thủ Đức, TP.HCM</p>
                <p class="note">(Trước đây là Quận 9)</p>
              </div>
            </div>

            <a
              href="https://www.google.com/maps/dir//10.855232352763043,106.7857806923059/@10.855232,106.785780,17z"
              target="_blank"
              class="directions-btn"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"
                />
              </svg>
              Chỉ Đường Đến Đây
            </a>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import axios from "axios";
import VueBasicAlert from "vue-basic-alert";

export default {
  name: "FeedbackPage",
  components: {
    VueBasicAlert,
  },
  data() {
    return {
      submitting: false,
      feedback: {
        name: "",
        phone: "",
        email: "",
        subject: "",
        message: "",
      },
      errors: {},
    };
  },
  methods: {
    validate() {
      const errs = {};
      const name = this.feedback.name?.trim();
      const phone = this.feedback.phone?.trim();
      const email = this.feedback.email?.trim();
      const subject = this.feedback.subject?.trim();
      const message = this.feedback.message?.trim();

      if (!name) errs.name = "Vui lòng nhập họ và tên";
      if (!phone) {
        errs.phone = "Vui lòng nhập số điện thoại";
      } else if (!/^0\d{9,10}$/.test(phone) && !/^84\d{9}$/.test(phone)) {
        errs.phone = "Số điện thoại không hợp lệ (bắt đầu 0... hoặc 84...)";
      }
      if (email && !/^[\w.-]+@([\w-]+\.)+[\w-]{2,}$/i.test(email)) {
        errs.email = "Email không hợp lệ";
      }
      if (!subject) errs.subject = "Vui lòng nhập chủ đề góp ý";
      if (!message || message.length < 10) {
        errs.message = "Nội dung tối thiểu 10 ký tự";
      }
      this.errors = errs;
      return Object.keys(errs).length === 0;
    },

    resetForm() {
      this.feedback = {
        name: "",
        phone: "",
        email: "",
        subject: "",
        message: "",
      };
      this.errors = {};
      const form = document.getElementById("feedbackForm");
      if (form) form.reset();
    },

    async handleSubmit(e) {
      e.preventDefault();
      if (!this.validate()) return;

      this.submitting = true;
      try {
        await axios.post("/feedback", {
          name: this.feedback.name.trim(),
          phone: this.feedback.phone.trim(),
          email: this.feedback.email?.trim() || undefined,
          subject: this.feedback.subject.trim(),
          message: this.feedback.message.trim(),
        });

        this.$refs.alert.showAlert(
          "success",
          "Cảm ơn bạn đã góp ý! Chúng tôi sẽ phản hồi sớm nhất.",
          "Gửi thành công ✨"
        );
        this.resetForm();
      } catch (err) {
        console.error("❌ Feedback error:", err);
        this.$refs.alert.showAlert(
          "error",
          err.response?.data?.message ||
            "Không thể gửi góp ý. Vui lòng thử lại sau.",
          "Gửi thất bại"
        );
      } finally {
        this.submitting = false;
      }
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

.feedback-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fdf9 0%, #f0f9ff 50%, #fef3f2 100%);
}

/* Hero Section */
.hero-section {
  background: linear-gradient(135deg, #00b067 0%, #00d97e 100%);
  padding: 4rem 2rem;
  text-align: center;
  color: white;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320"><path fill="%23ffffff" fill-opacity="0.1" d="M0,96L48,112C96,128,192,160,288,160C384,160,480,128,576,122.7C672,117,768,139,864,138.7C960,139,1056,117,1152,101.3C1248,85,1344,75,1392,69.3L1440,64L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path></svg>')
    bottom center no-repeat;
  opacity: 0.3;
}

.hero-content {
  position: relative;
  z-index: 1;
  max-width: 800px;
  margin: 0 auto;
}

.badge {
  display: inline-block;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  padding: 0.5rem 1.5rem;
  border-radius: 50px;
  font-size: 1.05rem;
  font-weight: 600;
  letter-spacing: 1px;
  text-transform: uppercase;
  margin-bottom: 1rem;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.hero-title {
  font-size: 3.4rem;
  font-weight: 800;
  margin-bottom: 1rem;
  text-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
}

.hero-subtitle {
  font-size: 1.35rem;
  opacity: 0.95;
  font-weight: 400;
  line-height: 1.6;
}

/* Container */
.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 3rem 2rem;
}

/* Contact Cards */
.contact-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.contact-card {
  background: white;
  padding: 2rem;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s, box-shadow 0.3s;
  border: 1px solid rgba(0, 176, 103, 0.1);
}

.contact-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(0, 176, 103, 0.15);
}

.card-icon {
  width: 60px;
  height: 60px;
  margin: 0 auto 1rem;
  background: linear-gradient(135deg, #00b067, #00d97e);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.card-icon.phone {
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
}
.card-icon.email {
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
}
.card-icon.location {
  background: linear-gradient(135deg, #ef4444, #f87171);
}

.card-icon svg {
  width: 30px;
  height: 30px;
}

.contact-card h3 {
  font-size: 1.3rem;
  color: #1f2937;
  margin-bottom: 0.5rem;
  font-weight: 700;
}

.contact-card p {
  color: #6b7280;
  font-size: 1.1rem;
  line-height: 1.6;
  margin: 0.25rem 0;
}

.contact-card .highlight {
  color: #00b067;
  font-weight: 700;
  font-size: 1.25rem;
}

/* Content Grid */
.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
}

/* Form Wrapper */
.form-wrapper {
  background: white;
  padding: 2.5rem;
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 176, 103, 0.1);
}

.form-header {
  margin-bottom: 2rem;
}

.form-header h2 {
  font-size: 2.1rem;
  color: #1f2937;
  margin-bottom: 0.5rem;
  font-weight: 800;
}

.form-header p {
  color: #6b7280;
  font-size: 1.1rem;
  line-height: 1.6;
}

/* Form Grid */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group.full-width {
  grid-column: span 2;
}

.form-group label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.1rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 0.5rem;
}

.form-group label svg {
  width: 18px;
  height: 18px;
  color: #00b067;
}

.form-group input,
.form-group textarea {
  padding: 0.875rem 1rem;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  font-size: 1.1rem;
  font-family: inherit;
  transition: all 0.3s;
  background: #f9fafb;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #00b067;
  background: white;
  box-shadow: 0 0 0 3px rgba(0, 176, 103, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 120px;
}

.error-msg {
  color: #ef4444;
  font-size: 1rem;
  margin-top: 0.35rem;
  font-weight: 500;
}

/* Submit Button */
.submit-btn {
  margin-top: 1.5rem;
  width: 100%;
  padding: 1.1rem;
  background: linear-gradient(135deg, #00b067, #00d97e);
  color: white;
  font-size: 1.15rem;
  font-weight: 700;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(0, 176, 103, 0.3);
}

.submit-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #009959, #00c470);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 176, 103, 0.4);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.submit-btn svg {
  width: 20px;
  height: 20px;
}

.spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Map Wrapper */
.map-wrapper {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 176, 103, 0.1);
}

.map-header {
  padding: 2rem;
  background: linear-gradient(135deg, #f0fdf4, #dcfce7);
  border-bottom: 1px solid rgba(0, 176, 103, 0.1);
}

.map-header h2 {
  font-size: 1.8rem;
  color: #1f2937;
  margin-bottom: 0.5rem;
  font-weight: 800;
}

.map-header p {
  color: #6b7280;
  font-size: 1.1rem;
  line-height: 1.6;
}

.map-container {
  height: 400px;
  position: relative;
}

.map-container iframe {
  width: 100%;
  height: 100%;
  border: none;
}

/* Address Details */
.address-details {
  padding: 1.5rem 2rem;
  background: #f9fafb;
}

.address-item {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.address-item svg {
  width: 24px;
  height: 24px;
  color: #00b067;
  flex-shrink: 0;
  margin-top: 0.2rem;
}

.address-item strong {
  display: block;
  color: #1f2937;
  margin-bottom: 0.5rem;
  font-size: 1.1rem;
}

.address-item p {
  color: #6b7280;
  font-size: 1.05rem;
  line-height: 1.6;
  margin: 0.2rem 0;
}

.address-item .note {
  font-style: italic;
  color: #9ca3af;
  font-size: 0.95rem;
}

/* Directions Button */
.directions-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem 1.6rem;
  background: linear-gradient(135deg, #00b067, #00d97e);
  color: white;
  text-decoration: none;
  border-radius: 10px;
  font-weight: 600;
  font-size: 1.1rem;
  transition: all 0.3s;
  box-shadow: 0 2px 10px rgba(0, 176, 103, 0.2);
}

.directions-btn:hover {
  background: linear-gradient(135deg, #009959, #00c470);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 176, 103, 0.3);
}

.directions-btn svg {
  width: 20px;
  height: 20px;
}

/* Responsive */
@media (max-width: 1024px) {
  .content-grid {
    grid-template-columns: 1fr;
  }

  .hero-title {
    font-size: 2.8rem;
  }
}

@media (max-width: 768px) {
  .hero-section {
    padding: 3rem 1.5rem;
  }

  .hero-title {
    font-size: 2.25rem;
  }

  .hero-subtitle {
    font-size: 1.1rem;
  }

  .contact-cards {
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
    gap: 1rem;
  }

  .container {
    padding: 2rem 1rem;
  }

  .form-wrapper,
  .map-wrapper {
    padding: 1.5rem;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .form-group.full-width {
    grid-column: span 1;
  }

  .map-container {
    height: 300px;
  }
}

@media (max-width: 480px) {
  .hero-title {
    font-size: 2rem;
  }

  .contact-cards {
    grid-template-columns: 1fr;
  }

  .form-wrapper,
  .map-wrapper {
    padding: 1rem;
  }

  .form-header h2 {
    font-size: 1.7rem;
  }
}
</style>
