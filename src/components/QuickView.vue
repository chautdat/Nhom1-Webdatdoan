<template>
  <div
    v-if="showQuickView"
    class="quick-view-overlay"
    @click.self="closeQuickView"
  >
    <div class="quick-view-container">
      <button class="close-btn" @click="closeQuickView">✕</button>

      <!-- Loading -->
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Đang tải thông tin sản phẩm...</p>
      </div>

      <!-- Error -->
      <div v-else-if="error" class="error-state">
        <div class="error-icon">⚠️</div>
        <p>{{ error }}</p>
        <button @click="fetchProduct" class="retry-btn">🔄 Thử lại</button>
      </div>

      <!-- Product -->
      <div v-else-if="product" class="quick-view-content">
        <!-- Left: Image -->
        <div class="product-image">
          <div v-if="!product.isAvailable" class="sold-out-overlay">
            <span class="sold-out-badge">🔴 TẠM HẾT</span>
          </div>
          <img
            :src="getProductImage(product.imageUrl)"
            :alt="product.productName"
          />
        </div>

        <!-- Right: Details -->
        <div class="product-details">
          <!-- Title -->
          <h2 class="product-name">{{ product.productName }}</h2>

          <!-- Price - PURPLE -->
          <div class="product-price">
            {{ formatPrice(product.discountPrice || product.price) }}
          </div>

          <!-- Info Box - Yellow -->
          <div class="product-info-box">
            <span class="info-icon">🍽️</span>
            <span class="info-text">Thơm Ngon</span>
          </div>

          <!-- Description -->
          <div v-if="product.description" class="product-description-box">
            <h3 class="desc-title">📝 Mô Tả Sản Phẩm</h3>
            <p class="desc-text">{{ product.description }}</p>
          </div>

          <!-- Note for admin -->
          <div class="note-box">
            <label for="quickview-note">Ghi chú cho quán (tùy chọn):</label>
            <textarea
              id="quickview-note"
              v-model="note"
              rows="3"
              placeholder="Ví dụ: Ít cay, không hành..."
            ></textarea>
          </div>

          <!-- Quantity -->
          <div class="quantity-section">
            <label class="quantity-label">Số Lượng:</label>
            <div class="quantity-controls">
              <button
                @click="decreaseQuantity"
                :disabled="quantity <= 1"
                class="qty-btn"
              >
                −
              </button>
              <input
                type="number"
                v-model.number="quantity"
                min="1"
                readonly
                class="qty-input"
              />
              <button @click="increaseQuantity" class="qty-btn">+</button>
            </div>
          </div>

          <!-- Subtotal - CYAN -->
          <div class="subtotal-box">
            <span class="subtotal-label">Tạm Tính:</span>
            <span class="subtotal-price">{{
              formatPrice((product.discountPrice || product.price) * quantity)
            }}</span>
          </div>

          <!-- Add to Cart Button - PURPLE -->
          <button
            v-if="user"
            @click="addToCart"
            class="btn-add-to-cart"
            :disabled="!product.isAvailable || isAdding"
          >
            <span v-if="isAdding">⏳ Đang thêm...</span>
            <span v-else-if="!product.isAvailable">❌ Tạm Hết Hàng</span>
            <span v-else>Thêm Vào Giỏ Hàng</span>
          </button>

          <!-- Login Button -->
          <button v-else class="btn-login" @click="redirectToLogin">
            🔐 Đăng nhập để mua hàng
          </button>

          <p v-if="product && !product.isAvailable" class="unavailable-text">
            Món ăn hiện tạm hết. Vui lòng chọn món khác.
          </p>
        </div>
      </div>

      <!-- No product -->
      <div v-else class="no-product-state">
        <div class="empty-icon">📦</div>
        <p>Không tải được sản phẩm</p>
      </div>
    </div>

    <!-- Toast Notification - Góc trên phải màn hình -->
    <transition name="toast-slide">
      <div v-if="toastMessage" class="toast-notification">
        <div class="toast-icon">✓</div>
        <div class="toast-content">
          <div class="toast-title">Thành công!</div>
          <div class="toast-message">{{ toastMessage }}</div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import api from "@/axios";
import { mapState } from "vuex";
import storage from "@/utils/storage";
import Swal from "sweetalert2";

export default {
  name: "QuickView",

  props: {
    productId: {
      type: [Number, String],
      default: null,
    },
    showQuickView: Boolean,
  },

  data() {
    return {
      product: null,
      loading: false,
      error: null,
      quantity: 1,
      isAdding: false,
      toastMessage: "",
      toastTimer: null,
      redirectTimer: null,
      toastDuration: 1200,
      note: "",
    };
  },

  computed: {
    ...mapState(["user"]),
  },

  watch: {
    showQuickView(newVal) {
      if (newVal && this.productId) {
        this.fetchProduct();
      }
    },
    productId(newVal) {
      if (this.showQuickView && newVal) {
        this.fetchProduct();
      }
    },
  },

  methods: {
    async fetchProduct() {
      this.loading = true;
      this.error = null;
      this.product = null;

      try {
        const id = Number(this.productId);
        const res = await api.get(`/products/${id}`);
        this.product = res.data.data;
        this.quantity = 1;
        this.note = "";
      } catch (err) {
        this.error =
          err.response?.data?.message || "Không thể tải thông tin sản phẩm";
      } finally {
        this.loading = false;
      }
    },

    getProductImage(url) {
      if (!url) return "/images/notfound.png";
      const backendUrl = "http://localhost:3000";
      if (url.startsWith("http")) return url;
      if (url.startsWith("/uploads")) return `${backendUrl}${url}`;
      return `${backendUrl}/uploads/${url}`;
    },

    formatPrice(price) {
      return Number(price).toLocaleString("vi-VN") + "đ";
    },

    increaseQuantity() {
      this.quantity++;
    },

    decreaseQuantity() {
      if (this.quantity > 1) this.quantity--;
    },

    async addToCart() {
      if (!this.product) return;

      try {
        if (!this.product.isAvailable) {
          alert("Món ăn này hiện đang tạm hết!");
          return;
        }

        if (!this.user) {
          alert("Vui lòng đăng nhập để thêm vào giỏ hàng!");
          this.$router.push("/login");
          return;
        }

        const token = storage.getToken();
        if (!token) {
          await Swal.fire({
            icon: "warning",
            title: "Phiên hết hạn",
            text: "Vui lòng đăng nhập lại",
            confirmButtonColor: "#e74c3c",
          });
          storage.clearAll();
          this.$store.commit("setUser", null);
          this.$router.push("/login");
          return;
        }

        this.isAdding = true;

        const res = await api.post(
          "/cart/items",
          {
            productId: this.product.productId || this.product.id,
            quantity: Math.max(1, this.quantity),
            notes: (this.note || "").trim(),
          },
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );

        if (res.data.success) {
          // Popup thông báo thành công
          await Swal.fire({
            icon: "success",
            title: "Đã thêm vào giỏ hàng!",
            text: this.product.productName || "",
            showConfirmButton: false,
            timer: this.toastDuration,
            timerProgressBar: true,
          });

          // Thông báo NavBar cập nhật badge
          this.dispatchCartUpdated();

          // Quay lại menu
          this.closeQuickView();
          this.$router.push("/menu");
        } else {
          await Swal.fire({
            icon: "error",
            title:
              res.data.message === "Insufficient stock" ? "Hết hàng" : "Lỗi!",
            text: res.data.message || "Lỗi khi thêm vào giỏ hàng",
            confirmButtonColor: "#00b067",
          });
        }
      } catch (err) {
        if (err.response?.status === 401 || err.response?.status === 403) {
          await Swal.fire({
            icon: "warning",
            title: "Phiên hết hạn",
            text: "Vui lòng đăng nhập lại",
            confirmButtonColor: "#e74c3c",
          });
          storage.clearAll();
          this.$store.commit("setUser", null);
          this.$router.push("/login");
        } else {
          Swal.fire({
            icon: "error",
            title:
              err.response?.data?.message === "Insufficient stock"
                ? "Hết hàng"
                : "Lỗi!",
            text: err.response?.data?.message || "Lỗi khi thêm vào giỏ hàng!",
            confirmButtonColor: "#e74c3c",
          });
        }
      } finally {
        this.isAdding = false;
      }
    },

    dispatchCartUpdated() {
      try {
        const evt = new Event("cart-updated");
        window.dispatchEvent(evt);
      } catch (e) {
        console.warn("Không thể dispatch cart-updated:", e);
      }
    },

    redirectToLogin() {
      this.$router.push("/login");
    },

    closeQuickView() {
      this.$emit("close");
      this.product = null;
      this.quantity = 1;
      this.note = "";
      this.error = null;
      this.isAdding = false;
      if (this.toastTimer) {
        clearTimeout(this.toastTimer);
        this.toastTimer = null;
      }
      if (this.redirectTimer) {
        clearTimeout(this.redirectTimer);
        this.redirectTimer = null;
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

/* Overlay – Green Glass */
.quick-view-overlay {
  position: fixed;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  width: 100vw !important;
  height: 100vh !important;
  background: rgba(0, 70, 47, 0.85);
  backdrop-filter: blur(8px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 100000 !important; /* ← Tăng lên */
  animation: fadeIn 0.3s ease;
  overflow-y: auto; /* ← Thêm scroll */
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* Modal Container */
.quick-view-container {
  background: white;
  border-radius: 22px;
  max-width: 900px;
  width: 92%;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  box-shadow: 0 20px 80px rgba(0, 0, 0, 0.4);
  animation: slideUp 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
  z-index: 100001 !important; /* ← Thêm z-index */
  margin: auto; /* ← Căn giữa */
}

@keyframes slideUp {
  from {
    transform: translateY(80px) scale(0.9);
    opacity: 0;
  }
  to {
    transform: translateY(0) scale(1);
    opacity: 1;
  }
}

/* Close Button – Green */
.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #00ba74 0%, #009e61 100%);
  border: none;
  font-size: 20px;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: 0.25s;
  box-shadow: 0 4px 12px rgba(0, 176, 103, 0.4);
  z-index: 10;
}

.close-btn:hover {
  transform: rotate(90deg) scale(1.1);
}

/* Loading */
.loading-state {
  text-align: center;
  padding: 4rem 2rem;
}

.spinner {
  width: 56px;
  height: 56px;
  border: 6px solid #dff5ea;
  border-top-color: #00b067;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-state p {
  font-weight: 600;
  color: #007b4d;
}

/* ================================
   TOAST NOTIFICATION - GÓC TRÊN PHẢI
   ================================ */
.toast-notification {
  position: fixed;
  top: 24px;
  right: 24px;
  display: flex;
  gap: 14px;
  align-items: center;
  padding: 16px 20px;
  border-radius: 14px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  z-index: 100002 !important; /* ← Cao nhất */
  min-width: 320px;
  max-width: 420px;
}

.toast-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.25);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 20px;
  flex-shrink: 0;
}

.toast-content {
  flex: 1;
}

.toast-title {
  font-weight: 800;
  font-size: 16px;
  margin-bottom: 4px;
}

.toast-message {
  font-size: 14px;
  opacity: 0.95;
}

/* Toast Animation */
.toast-slide-enter-active {
  animation: toastSlideIn 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.toast-slide-leave-active {
  animation: toastSlideOut 0.3s ease-out;
}

@keyframes toastSlideIn {
  from {
    transform: translateX(400px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes toastSlideOut {
  from {
    transform: translateX(0);
    opacity: 1;
  }
  to {
    transform: translateX(400px);
    opacity: 0;
  }
}

/* Error */
.error-state {
  padding: 4rem;
  text-align: center;
}

.error-icon {
  font-size: 3.5rem;
  margin-bottom: 1rem;
}

.error-state p {
  color: #b91c1c;
  font-weight: 600;
  margin-bottom: 1rem;
}

.retry-btn {
  padding: 0.85rem 1.8rem;
  background: #00b067;
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
  transition: 0.2s;
}

.retry-btn:hover {
  background: #00965c;
}

/* Layout */
.quick-view-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2.2rem;
  padding: 2rem 2.5rem;
}

/* Product Image */
.product-image {
  background: #f3f9f6;
  padding: 1.5rem;
  border-radius: 18px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.product-image img {
  width: 100%;
  max-height: 420px;
  object-fit: contain;
  border-radius: 14px;
}

/* Sold Out Overlay */
.sold-out-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  border-radius: 18px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.sold-out-badge {
  background: #c62828;
  padding: 0.7rem 1.4rem;
  border-radius: 12px;
  color: white;
  font-weight: 800;
}

/* Product Details */
.product-details {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.product-name {
  font-size: 1.7rem;
  font-weight: 800;
  color: #1b1f22;
  line-height: 1.3;
}

/* Green price */
.product-price {
  font-size: 2rem;
  font-weight: 900;
  color: #00a463;
}

/* Info box (green highlight) */
.product-info-box {
  display: flex;
  align-items: center;
  gap: 0.7rem;
  padding: 1rem 1.2rem;
  background: #e8f7ee;
  border-left: 5px solid #00a463;
  border-radius: 12px;
}

.info-text {
  color: #05603a;
  font-weight: 600;
}

/* Description */
.product-description-box {
  background: #f8fafc;
  border: 2px solid #e2e8f0;
  padding: 1rem;
  border-radius: 12px;
}

.desc-title {
  font-size: 1.05rem;
  font-weight: 700;
  color: #334155;
}

.desc-text {
  font-size: 0.95rem;
  color: #475569;
  line-height: 1.6;
}

.note-box {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.note-box label {
  font-weight: 700;
  color: #065f46;
  font-size: 0.95rem;
}

.note-box textarea {
  width: 100%;
  padding: 12px;
  border: 2px solid #d9eadf;
  border-radius: 12px;
  resize: vertical;
  min-height: 72px;
  font-size: 0.95rem;
  font-family: inherit;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.note-box textarea:focus {
  border-color: #00a463;
  box-shadow: 0 0 0 3px rgba(0, 164, 99, 0.12);
}

/* Quantity */
.quantity-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quantity-controls {
  display: flex;
  gap: 12px;
  align-items: center;
}

.qty-btn {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  border: 2px solid #d9eadf;
  background: white;
  font-size: 1.3rem;
  color: #00a463;
  cursor: pointer;
  transition: 0.2s;
  font-weight: 700;
}

.qty-btn:hover:not(:disabled) {
  background: #00a463;
  color: white;
  border-color: #00a463;
}

.qty-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.qty-input {
  width: 60px;
  text-align: center;
  border: none;
  font-weight: 800;
  font-size: 1.2rem;
  background: transparent;
}

/* Subtotal – mint green */
.subtotal-box {
  background: #dff7ec;
  border: 2px solid #9fe7c2;
  padding: 1rem 1.2rem;
  border-radius: 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.subtotal-label {
  font-weight: 600;
  color: #065f46;
}

.subtotal-price {
  font-size: 1.6rem;
  font-weight: 900;
  color: #065f46;
}

/* Buttons – PDQ Green */
.btn-add-to-cart {
  width: 100%;
  background: linear-gradient(135deg, #00b067, #00d97e);
  padding: 1rem 1.2rem;
  border-radius: 12px;
  border: none;
  font-weight: 800;
  font-size: 1.05rem;
  color: white;
  cursor: pointer;
  box-shadow: 0 6px 20px rgba(0, 150, 92, 0.4);
  transition: 0.25s;
}

.btn-add-to-cart:hover:not(:disabled) {
  transform: translateY(-3px);
  background: #00a463;
}

.btn-add-to-cart:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-login {
  width: 100%;
  padding: 1rem;
  border-radius: 12px;
  background: #00b067;
  color: white;
  border: none;
  font-weight: 800;
  cursor: pointer;
  transition: 0.25s;
}

.btn-login:hover {
  background: #00965c;
}

.unavailable-text {
  color: #dc2626;
  font-weight: 600;
  text-align: center;
  font-size: 14px;
}

/* No Product */
.no-product-state {
  padding: 4rem;
  text-align: center;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
  opacity: 0.3;
}

.no-product-state p {
  color: #6b7280;
  font-weight: 600;
}

/* Responsive */
@media (max-width: 768px) {
  .quick-view-content {
    grid-template-columns: 1fr;
    padding: 1.8rem;
  }

  .toast-notification {
    top: 16px;
    right: 16px;
    left: 16px;
    min-width: auto;
    max-width: none;
  }
}
</style>
