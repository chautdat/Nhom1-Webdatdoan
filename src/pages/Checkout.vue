<template>
  <div class="checkout-page">
    <!-- Main Container -->
    <div class="checkout-container">
      <!-- Left Column - Order Summary -->
      <div class="left-column">
        <div class="section-card order-summary">
          <div class="section-header">
            <h2>
              <span class="icon">🛒</span>
              Đơn hàng của bạn
            </h2>
          </div>

          <!-- Cart Items Preview WITH IMAGES -->
          <div class="cart-items-preview">
            <div
              v-for="item in cart"
              :key="item.cartItemId"
              class="cart-item-mini"
            >
              <img
                :src="getImageUrl(item.productImage)"
                :alt="item.productName || 'Sản phẩm'"
                class="item-image"
                @error="handleImageError"
              />
              <div class="item-info">
                <p class="item-name">{{ item.productName || "Sản phẩm" }}</p>
                <p class="item-detail">
                  {{ item.quantity || 0 }} x {{ formatVND(item.price) }}
                </p>
              </div>
              <div class="item-total">
                {{
                  formatVND(
                    item.subtotal || (item.quantity || 0) * (item.price || 0),
                  )
                }}
              </div>
            </div>

            <div v-if="cart.length === 0" class="empty-cart">
              <span class="empty-icon">🛒</span>
              <p>Giỏ hàng trống</p>
            </div>
          </div>

          <!-- Price Breakdown -->
          <div class="price-breakdown">
            <div class="price-row">
              <span class="label"
                >Tạm tính ({{ cartSummary.totalItems || 0 }} món)</span
              >
              <span class="value">{{
                formatVND(cartSummary.totalPrice || 0)
              }}</span>
            </div>

            <div class="price-row">
              <span class="label">Phí giao hàng</span>
              <span class="value">
                {{ formatVND(summary.deliveryFee) }}
              </span>
            </div>

            <div class="divider"></div>

            <div class="price-row total">
              <span class="label">Tổng cộng</span>
              <span class="value">
                {{ formatVND(summary.finalTotal) }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Column - Checkout Form -->
      <div class="right-column">
        <form @submit="handleSubmit" novalidate autocomplete="off">
          <!-- Shipping Info -->
          <div class="section-card">
            <div class="section-header">
              <h2>
                <span class="icon">📍</span>
                Thông tin giao hàng
              </h2>
            </div>

            <div class="form-body">
              <div class="form-group">
                <label class="form-label">Tên người nhận</label>
                <input
                  type="text"
                  placeholder="Nhập tên người nhận"
                  class="form-input"
                  v-model="checkoutObj.recipientName"
                  :class="{ error: errorObj.nameErr.length }"
                />
                <p v-if="errorObj.nameErr.length" class="error-message">
                  {{ errorObj.nameErr[0] }}
                </p>
              </div>

              <div class="form-group">
                <label class="form-label">Số điện thoại</label>
                <input
                  type="text"
                  placeholder="Nhập số điện thoại"
                  class="form-input"
                  v-model="checkoutObj.phone"
                  :class="{ error: errorObj.phoneErr.length }"
                />
                <p v-if="errorObj.phoneErr.length" class="error-message">
                  {{ errorObj.phoneErr[0] }}
                </p>
              </div>

              <!-- ADDRESS SELECTOR COMPONENT -->
              <AddressSelector
                ref="addressSelector"
                v-model="checkoutObj.addressLine"
                :errors="addressErrors"
                @update:city="checkoutObj.city = $event"
                @update:district="checkoutObj.district = $event"
                @update:ward="checkoutObj.ward = $event"
                @address-selected="onAddressSelected"
              />
            </div>
          </div>

          <!-- Payment Method -->
          <div class="section-card">
            <div class="section-header">
              <h2>
                <span class="icon">💳</span>
                Phương thức thanh toán
              </h2>
            </div>

            <div class="form-body">
              <div class="payment-methods">
                <label
                  class="payment-card"
                  :class="{ selected: checkoutObj.paymentMethod === 'cash' }"
                >
                  <input
                    type="radio"
                    value="cash"
                    v-model="checkoutObj.paymentMethod"
                  />
                  <div class="payment-content">
                    <span class="payment-icon">💵</span>
                    <div class="payment-info">
                      <h4>Tiền mặt</h4>
                      <p>Thanh toán khi nhận hàng</p>
                    </div>
                  </div>
                  <span class="check-mark">✓</span>
                </label>

                <label
                  class="payment-card"
                  :class="{ selected: checkoutObj.paymentMethod === 'vnpay' }"
                >
                  <input
                    type="radio"
                    value="vnpay"
                    v-model="checkoutObj.paymentMethod"
                  />
                  <div class="payment-content">
                    <span class="payment-icon">💳</span>
                    <div class="payment-info">
                      <h4>VNPay</h4>
                      <p>Thanh toán qua cổng VNPay</p>
                    </div>
                  </div>
                  <span class="check-mark">✓</span>
                </label>
              </div>

              <!-- VNPay Notice -->
              <transition name="fade">
                <div
                  v-if="checkoutObj.paymentMethod === 'vnpay'"
                  class="vnpay-notice"
                >
                  <div class="notice-icon">ℹ️</div>
                  <div class="notice-content">
                    <strong>Thanh toán qua VNPay</strong>
                    <p>
                      Sau khi nhấn "Xác nhận đặt hàng", bạn sẽ được chuyển đến
                      trang thanh toán VNPay an toàn.
                    </p>
                  </div>
                </div>
              </transition>

              <p v-if="errorObj.payErr.length" class="error-message">
                {{ errorObj.payErr[0] }}
              </p>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="action-buttons">
            <button
              type="submit"
              class="btn-primary"
              :disabled="cart.length === 0 || isSubmitting"
            >
              <span v-if="isSubmitting">
                <span class="spinner"></span>
                {{
                  checkoutObj.paymentMethod === "vnpay"
                    ? "Đang chuyển hướng..."
                    : "Đang xử lý..."
                }}
              </span>
              <span v-else>
                {{
                  checkoutObj.paymentMethod === "vnpay"
                    ? "💳 XÁC NHẬN & THANH TOÁN VNPAY"
                    : "✓ XÁC NHẬN ĐẶT HÀNG"
                }}
              </span>
            </button>

            <router-link to="/menu" class="btn-secondary">
              ← Tiếp tục mua sắm
            </router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import api from "@/axios";
import storage, { STORAGE_KEYS } from "@/utils/storage";
import Swal from "sweetalert2";
import AddressSelector from "@/components/AddressSelector.vue";

export default {
  name: "Checkout",

  components: {
    AddressSelector,
  },

  data() {
    return {
      checkoutObj: {
        recipientName: "",
        phone: "",
        addressLine: "",
        city: "",
        district: "",
        ward: "",
        paymentMethod: "cash",
      },
      errorObj: {
        nameErr: [],
        phoneErr: [],
        addressErr: [],
        payErr: [],
      },
      addressErrors: {
        city: "",
        district: "",
        ward: "",
        detail: "",
      },
      cart: [],
      cartSummary: {
        cartId: null,
        totalItems: 0,
        totalPrice: 0,
      },
      isSubmitting: false,
    };
  },

  computed: {
    ...mapState(["user"]),
    summary() {
      return this.calculateSummary();
    },
  },

  mounted() {
    this.loadCart();

    if (this.user) {
      this.checkoutObj.recipientName = this.user.fullName || "";
      this.checkoutObj.phone = this.user.phone || "";
    }
  },

  methods: {
    onAddressSelected(address) {
      console.log("📍 Address selected from dropdown:", address);

      // Cập nhật thông tin địa chỉ đã chọn
      this.checkoutObj.recipientName =
        address.recipientName || this.checkoutObj.recipientName;
      this.checkoutObj.phone = address.phone || this.checkoutObj.phone;
      this.checkoutObj.addressLine = address.addressLine || "";
      this.checkoutObj.city = address.city || "";
      this.checkoutObj.district = address.district || "";
      this.checkoutObj.ward = address.ward || "";

      this.addressErrors = {
        city: "",
        district: "",
        ward: "",
        detail: "",
      };
    },

    async loadCart() {
      if (!this.user) {
        this.cart = [];
        this.cartSummary = {
          cartId: null,
          totalItems: 0,
          totalPrice: 0,
        };
        return;
      }

      try {
        const token = storage.getToken();

        if (!token) {
          this.$router.push("/login");
          return;
        }

        const res = await api.get("/cart", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (res.data.success && res.data.data) {
          const cartData = res.data.data;

          this.cartSummary = {
            cartId: cartData.cartId,
            totalItems: cartData.totalItems || 0,
            totalPrice: cartData.totalPrice || 0,
          };

          this.cart = cartData.items || [];
        }
      } catch (err) {
        console.error("Không thể load giỏ hàng", err);

        if (err.response?.status === 401 || err.response?.status === 403) {
          storage.clearAuth();
          this.$store.commit("setUser", null);
          this.$router.push("/login");
        }
      }
    },

    handleImageError(e) {
      e.target.src = "https://placehold.co/80x80?text=No+Image";
    },

    getImageUrl(url) {
      if (!url) {
        return "https://placehold.co/80x80?text=No+Image";
      }

      if (url.startsWith("http://") || url.startsWith("https://")) {
        return url;
      }

      if (url.startsWith("/uploads/")) {
        return `http://localhost:3000${url}`;
      }

      return `http://localhost:3000/uploads/${url}`;
    },

    formatVND(amount) {
      return Number(amount).toLocaleString("vi-VN") + " ₫";
    },

    calculateSummary() {
      let deliveryFee = 15000;

      if (this.cartSummary.totalPrice > 200000) deliveryFee = 0;
      if (this.cart.length === 0) deliveryFee = 0;
      const finalTotal = Math.max(0, this.cartSummary.totalPrice + deliveryFee);

      return {
        subtotal: this.cartSummary.totalPrice,
        deliveryFee,
        finalTotal,
      };
    },

    validateForm() {
      this.errorObj = {
        nameErr: [],
        phoneErr: [],
        addressErr: [],
        payErr: [],
      };

      this.addressErrors = {
        city: "",
        district: "",
        ward: "",
        detail: "",
      };

      if (!this.checkoutObj.recipientName)
        this.errorObj.nameErr.push("Vui lòng nhập tên người nhận!");

      if (!this.checkoutObj.phone)
        this.errorObj.phoneErr.push("Vui lòng nhập số điện thoại!");
      else if (!/^[0-9]{10,11}$/.test(this.checkoutObj.phone))
        this.errorObj.phoneErr.push("Số điện thoại không hợp lệ!");

      const addressValidation = this.$refs.addressSelector.validate();
      this.addressErrors = addressValidation.errors || {
        city: "",
        district: "",
        ward: "",
        detail: "",
      };

      // Nếu đã chọn địa chỉ đã lưu, bỏ qua lỗi địa chỉ
      if (addressValidation.isValid) {
        this.errorObj.addressErr = [];
      }

      if (!this.checkoutObj.paymentMethod)
        this.errorObj.payErr.push("Vui lòng chọn phương thức thanh toán!");

      const basicValid = Object.values(this.errorObj).every(
        (v) => v.length === 0,
      );
      return basicValid && addressValidation.isValid;
    },

    async clearCartAfterOrder(token) {
      try {
        if (!token) return;
        await api.delete("/cart", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
      } catch (err) {
        console.warn("⚠️ Không xóa được giỏ hàng sau khi đặt:", err);
      }
    },

    async handleSubmit(e) {
      e.preventDefault();

      console.log("📝 CHECKOUT - START");

      if (!this.validateForm()) {
        return;
      }

      if (this.cart.length === 0) {
        await Swal.fire({
          icon: "info",
          title: "Giỏ hàng trống",
          text: "Vui lòng thêm sản phẩm trước khi thanh toán",
          confirmButtonColor: "#00b067",
        });
        return;
      }

      this.isSubmitting = true;
      const token = storage.getToken();

      try {
        const orderRes = await api.post(
          "/orders",
          {
            recipientName: this.checkoutObj.recipientName,
            phone: this.checkoutObj.phone,
            addressLine: this.checkoutObj.addressLine,
            ward: this.checkoutObj.ward || null,
            district: this.checkoutObj.district || null,
            city: this.checkoutObj.city || null,
            paymentMethod: this.checkoutObj.paymentMethod,
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          },
        );

        if (!orderRes.data.success) {
          throw new Error(orderRes.data.message || "Failed to create order");
        }

        const responseData = orderRes.data.data;

        await this.clearCartAfterOrder(token);
        await this.loadCart();

        // ✅ PAYMENT REDIRECT (VNPay)
        if (responseData.requiresPayment && responseData.paymentUrl) {
          console.log("💳 Redirecting to Payment Gateway...");

          storage.set(STORAGE_KEYS.pendingOrderId, responseData.order.orderId);
          storage.set(
            STORAGE_KEYS.pendingOrderNumber,
            responseData.order.orderNumber,
          );

          await Swal.fire({
            title: "Đang chuyển hướng...",
            text: "Bạn sẽ được chuyển đến trang thanh toán an toàn",
            icon: "info",
            timer: 2000,
            showConfirmButton: false,
            allowOutsideClick: false,
          });

          window.location.href = responseData.paymentUrl;
          return;
        }

        // ✅ CASH PAYMENT
        const orderInfo = responseData.order || {};
        if (orderInfo.orderId) {
          storage.set(STORAGE_KEYS.pendingOrderId, orderInfo.orderId);
        }
        if (orderInfo.orderNumber) {
          storage.set(STORAGE_KEYS.pendingOrderNumber, orderInfo.orderNumber);
        }

        await Swal.fire({
          icon: "success",
          title: "Đặt hàng thành công!",
          text: "Cảm ơn bạn đã đặt hàng.",
          timer: 1400,
          showConfirmButton: false,
        });

        this.$router.push({
          path: "/thank",
          query: {
            orderId: orderInfo.orderId,
            orderNumber: orderInfo.orderNumber,
          },
        });
      } catch (err) {
        console.error("❌ CHECKOUT FAILED:", err);

        if (err.response?.status === 401 || err.response?.status === 403) {
          await Swal.fire({
            icon: "warning",
            title: "Phiên hết hạn",
            text: "Vui lòng đăng nhập lại!",
            confirmButtonColor: "#e74c3c",
          });
          storage.clearAuth();
          this.$store.commit("setUser", null);
          this.$router.push("/login");
        } else {
          const errorMessage =
            err.response?.data?.message ||
            err.response?.data?.error ||
            err.message ||
            "Không thể tạo đơn hàng. Vui lòng thử lại!";
          await Swal.fire({
            icon: "error",
            title: "Lỗi!",
            text: errorMessage,
            confirmButtonColor: "#e74c3c",
          });
        }
      } finally {
        this.isSubmitting = false;
      }
    },
  },
};
</script>

<style scoped>
/* ========================================
   MAIN LAYOUT
   ======================================== */
.checkout-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9f5f0 100%);
  padding: 2rem 1rem;
  font-size: 17px;
}

.checkout-container {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 480px 1fr;
  gap: 2rem;
  align-items: start;
}

/* ========================================
   TOAST NOTIFICATION
   ======================================== */
.toast {
  position: fixed;
  top: 2rem;
  right: 2rem;
  background: #10b981;
  color: white;
  padding: 1rem 1.5rem;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(16, 185, 129, 0.3);
  display: flex;
  align-items: center;
  gap: 1rem;
  z-index: 1000;
  min-width: 320px;
}

.toast-icon {
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  font-weight: bold;
}

.toast-title {
  font-weight: 700;
  margin-bottom: 0.25rem;
}

.toast-message {
  font-size: 0.9rem;
  opacity: 0.95;
}

/* ========================================
   SECTION CARDS
   ======================================== */
.section-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 1.5rem;
  overflow: hidden;
  transition: all 0.3s ease;
}

.section-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.section-header {
  padding: 1.5rem 2rem;
  border-bottom: 1px solid #f0f0f0;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
}

.section-header h2 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1a1a1a;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin: 0;
}

.section-header .icon {
  font-size: 1.5rem;
}

.form-body {
  padding: 2rem;
  font-size: 1.05rem;
}

/* ========================================
   LEFT COLUMN - ORDER SUMMARY
   ======================================== */
.left-column {
  position: sticky;
  top: 2rem;
}

.order-summary {
  position: relative;
}

/* ✅ Cart Items Preview WITH IMAGES */
.cart-items-preview {
  padding: 1.5rem 2rem;
  max-height: 400px;
  overflow-y: auto;
}

.cart-item-mini {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.75rem 0;
  border-bottom: 1px solid #f5f5f5;
}

.cart-item-mini:last-child {
  border-bottom: none;
}

/* ✅ HÌNH ẢNH MÓN ĂN - 60x60px */
.item-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
  border: 2px solid #f0f0f0;
  transition: all 0.3s ease;
}

.item-image:hover {
  transform: scale(1.05);
  border-color: #10b981;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-weight: 700;
  font-size: 1rem;
  color: #333;
  margin: 0 0 0.25rem 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-detail {
  font-size: 0.95rem;
  color: #666;
  margin: 0;
}

.item-total {
  font-weight: 700;
  color: #10b981;
  font-size: 0.95rem;
  flex-shrink: 0;
}

.empty-cart {
  text-align: center;
  padding: 3rem 2rem;
  color: #999;
}

.empty-icon {
  font-size: 3rem;
  display: block;
  margin-bottom: 1rem;
  opacity: 0.5;
}

/* Scrollbar styling */
.cart-items-preview::-webkit-scrollbar {
  width: 6px;
}

.cart-items-preview::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.cart-items-preview::-webkit-scrollbar-thumb {
  background: #10b981;
  border-radius: 10px;
}

.cart-items-preview::-webkit-scrollbar-thumb:hover {
  background: #059669;
}

/* Price Breakdown */
.price-breakdown {
  padding: 1.5rem 2rem;
  border-top: 2px dashed #e5e5e5;
  background: #fafafa;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.9rem 0;
  font-size: 1rem;
}

.price-row .label {
  color: #666;
  font-weight: 500;
}

.price-row .value {
  color: #333;
  font-weight: 600;
}

.divider {
  height: 1px;
  background: #e0e0e0;
  margin: 0.75rem 0;
}

.price-row.total {
  padding-top: 1rem;
  font-size: 1.2rem;
}

.price-row.total .label,
.price-row.total .value {
  color: #10b981;
  font-weight: 700;
}


/* ========================================
   RIGHT COLUMN - FORM
   ======================================== */
.right-column {
  min-width: 0;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-label {
  display: block;
  font-weight: 600;
  font-size: 1.1rem;
  color: #333;
  margin-bottom: 0.5rem;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 0.95rem 1.25rem;
  border: 1.5px solid #e0e0e0;
  border-radius: 10px;
  font-size: 1.1rem;
  transition: all 0.2s ease;
  font-family: inherit;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #10b981;
  background: #f9fffe;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.form-input.error,
.form-textarea.error {
  border-color: #ef4444;
}

.form-textarea {
  resize: vertical;
  min-height: 100px;
}

.error-message {
  color: #ef4444;
  font-size: 0.85rem;
  margin-top: 0.5rem;
  margin-bottom: 0;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

/* Payment Methods */
.payment-methods {
  display: grid;
  gap: 1rem;
}

.payment-card {
  position: relative;
  display: flex;
  align-items: center;
  padding: 1.5rem;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: white;
}

.payment-card:hover {
  border-color: #10b981;
  background: #f9fffe;
  transform: translateX(4px);
}

.payment-card.selected {
  border-color: #10b981;
  background: #ecfdf5;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.payment-card input[type="radio"] {
  position: absolute;
  opacity: 0;
  pointer-events: none;
}

.payment-content {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex: 1;
}

.payment-icon {
  font-size: 2rem;
}

.payment-info h4 {
  font-size: 1.05rem;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 0.25rem 0;
}

.payment-info p {
  font-size: 1rem;
  color: #666;
  margin: 0;
}

.check-mark {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #e5e7eb;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.85rem;
  font-weight: bold;
  transition: all 0.2s ease;
}

.payment-card.selected .check-mark {
  background: #10b981;
}

/* VNPay Notice */
.vnpay-notice {
  margin-top: 1rem;
  padding: 1.25rem;
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
  border-left: 4px solid #3b82f6;
  border-radius: 10px;
  display: flex;
  gap: 1rem;
}

.notice-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.notice-content strong {
  display: block;
  color: #1e40af;
  font-size: 0.95rem;
  margin-bottom: 0.5rem;
}

.notice-content p {
  color: #1e3a8a;
  font-size: 1rem;
  line-height: 1.5;
  margin: 0;
}

/* Action Buttons */
.action-buttons {
  margin-top: 2rem;
}

.btn-primary {
  width: 100%;
  padding: 1.25rem 2rem;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 1.05rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.4);
}

.btn-primary:disabled {
  background: #d1d5db;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-secondary {
  display: block;
  width: 100%;
  padding: 1rem 2rem;
  margin-top: 1rem;
  background: white;
  color: #10b981;
  border: 2px solid #10b981;
  border-radius: 12px;
  font-size: 1.05rem;
  font-weight: 600;
  text-align: center;
  text-decoration: none;
  transition: all 0.2s ease;
}

.btn-secondary:hover {
  background: #ecfdf5;
  transform: translateX(-2px);
}

/* Spinners */
.spinner,
.spinner-small {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  display: inline-block;
  animation: spin 0.6s linear infinite;
}

.spinner-small {
  width: 14px;
  height: 14px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Transitions */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
}

.slide-down-enter-from {
  transform: translateY(-100%);
  opacity: 0;
}

.slide-down-leave-to {
  transform: translateY(-100%);
  opacity: 0;
}

.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* ========================================
   RESPONSIVE
   ======================================== */
@media (max-width: 1200px) {
  .checkout-container {
    grid-template-columns: 420px 1fr;
  }
}

@media (max-width: 992px) {
  .checkout-container {
    grid-template-columns: 1fr;
    max-width: 680px;
  }

  .left-column {
    position: relative;
    top: 0;
  }

  .cart-items-preview {
    max-height: 300px;
  }
}

@media (max-width: 768px) {
  .checkout-page {
    padding: 1rem 0.5rem;
  }

  .checkout-container {
    gap: 1rem;
  }

  .section-header {
    padding: 1.25rem 1.5rem;
  }

  .section-header h2 {
    font-size: 1.1rem;
  }

  .form-body {
    padding: 1.5rem;
  }

  .price-breakdown {
    padding: 1.25rem 1.5rem;
  }

  .payment-card {
    padding: 1.25rem;
  }

  .payment-icon {
    font-size: 1.75rem;
  }

  .toast {
    right: 1rem;
    left: 1rem;
    min-width: auto;
  }
}
</style>
