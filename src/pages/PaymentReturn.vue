<template>
  <div class="payment-return">
    <div class="container">
      <!-- Loading -->
      <div v-if="loading" class="loading-box">
        <div class="spinner"></div>
        <h2>Đang xác nhận thanh toán...</h2>
        <p>Vui lòng đợi trong giây lát</p>
      </div>

      <!-- Success -->
      <div v-else-if="success" class="success-box">
        <div class="icon success-icon">✓</div>
        <h2>Thanh toán thành công!</h2>
        <p class="message">
          Đơn hàng <strong>{{ orderNumber }}</strong> đã được thanh toán.
        </p>

        <div class="order-info">
          <div class="info-row">
            <span class="label">Mã giao dịch:</span>
            <span class="value">{{ transactionId }}</span>
          </div>
          <div class="info-row">
            <span class="label">Số tiền:</span>
            <span class="value">{{ formatVND(amount) }}</span>
          </div>
        </div>

        <div class="actions">
          <button @click="goToOrders" class="btn-primary">Xem đơn hàng</button>
          <button @click="goToHome" class="btn-secondary">Về trang chủ</button>
        </div>
      </div>

      <!-- Failed -->
      <div v-else class="error-box">
        <div class="icon error-icon">✕</div>
        <h2>Thanh toán thất bại!</h2>
        <p class="message">{{ errorMessage }}</p>

        <div class="actions">
          <button @click="goToCheckout" class="btn-primary">Thử lại</button>
          <button @click="goToHome" class="btn-secondary">Về trang chủ</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "PaymentReturn",

  data() {
    return {
      loading: true,
      success: false,
      orderNumber: "",
      transactionId: "",
      amount: 0,
      errorMessage: "",
    };
  },

  async mounted() {
    await this.handleCallback();
  },

  methods: {
    async handleCallback() {
      try {
        const queryParams = this.$route.query;

        console.log("📥 Payment return params:", queryParams);

        // ✅ Backend đã verify và redirect về với params
        // Frontend chỉ cần đọc params để hiển thị

        if (!queryParams.orderNumber) {
          throw new Error("Không có thông tin đơn hàng");
        }

        // ✅ Đọc kết quả từ query params (đã được backend verify)
        const isSuccess = queryParams.success === "true";
        const responseCode = queryParams.responseCode;

        this.orderNumber = queryParams.orderNumber;
        this.transactionId = queryParams.transactionId || "N/A";
        this.amount = parseInt(queryParams.amount) || 0;

        if (isSuccess && responseCode === "00") {
          this.success = true;
        } else {
          this.success = false;
          this.errorMessage = this.getErrorMessage(responseCode);
        }
      } catch (err) {
        console.error("❌ Payment return error:", err);

        this.success = false;
        this.errorMessage =
          err.message || "Có lỗi xảy ra khi xác nhận thanh toán";
      } finally {
        this.loading = false;
      }
    },

    getErrorMessage(responseCode) {
      const errorMessages = {
        "07": "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).",
        "09": "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.",
        10: "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần",
        11: "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.",
        12: "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.",
        13: "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch.",
        24: "Giao dịch không thành công do: Khách hàng hủy giao dịch",
        51: "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.",
        65: "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.",
        75: "Ngân hàng thanh toán đang bảo trì.",
        79: "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch",
        99: "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê)",
      };

      return (
        errorMessages[responseCode] || "Thanh toán thất bại. Vui lòng thử lại!"
      );
    },

    formatVND(amount) {
      return Number(amount).toLocaleString("vi-VN") + " ₫";
    },

    goToOrders() {
      this.$router.push("/my-order");
    },

    goToCheckout() {
      this.$router.push("/checkout");
    },

    goToHome() {
      this.$router.push("/");
    },
  },
};
</script>

<style scoped>
/* GIỮ NGUYÊN CSS CŨ */
.payment-return {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9f5f0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.container {
  max-width: 600px;
  width: 100%;
}

.loading-box,
.success-box,
.error-box {
  background: white;
  border-radius: 16px;
  padding: 3rem 2rem;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.spinner {
  width: 60px;
  height: 60px;
  border: 4px solid #e5e5e5;
  border-top-color: #10b981;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1.5rem;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 3rem;
  margin: 0 auto 1.5rem;
}

.success-icon {
  background: #ecfdf5;
  color: #10b981;
}

.error-icon {
  background: #fef2f2;
  color: #ef4444;
}

h2 {
  font-size: 1.75rem;
  color: #1a1a1a;
  margin-bottom: 1rem;
}

.message {
  font-size: 1.1rem;
  color: #666;
  margin-bottom: 2rem;
}

.order-info {
  background: #f9fafb;
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 2rem;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 0.75rem 0;
  border-bottom: 1px solid #e5e7eb;
}

.info-row:last-child {
  border-bottom: none;
}

.label {
  color: #666;
  font-weight: 500;
}

.value {
  color: #1a1a1a;
  font-weight: 700;
}

.actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

.btn-primary,
.btn-secondary {
  flex: 1;
  padding: 1rem;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.btn-primary {
  background: #10b981;
  color: white;
}

.btn-primary:hover {
  background: #059669;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.btn-secondary {
  border: 2px solid #10b981;
  background: white;
  color: #10b981;
}

.btn-secondary:hover {
  background: #ecfdf5;
}

@media (max-width: 768px) {
  .payment-return {
    padding: 1rem;
  }

  .loading-box,
  .success-box,
  .error-box {
    padding: 2rem 1.5rem;
  }

  .actions {
    flex-direction: column;
  }
}
</style>
