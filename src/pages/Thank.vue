<template>
  <div class="thank-you-container">
    <div class="thank-you-card">
      <!-- SUCCESS ICON -->
      <div class="icon-circle" :class="paymentStatus">
        <span class="icon">{{ paymentStatus === "success" ? "✓" : "!" }}</span>
      </div>

      <!-- TITLE -->
      <h1>{{ pageTitle }}</h1>

      <!-- MESSAGE -->
      <p class="message">{{ pageMessage }}</p>

      <!-- ORDER DETAILS -->
      <div v-if="orderInfo" class="order-details">
        <div class="detail-row">
          <span>Mã đơn hàng:</span>
          <strong>{{ orderInfo.orderNumber || orderNumber }}</strong>
        </div>
        <div class="detail-row" v-if="orderInfo.subtotal != null">
          <span>Tạm tính:</span>
          <strong>{{ formatVND(orderInfo.subtotal) }}</strong>
        </div>
        <div class="detail-row" v-if="orderInfo.shippingFee != null">
          <span>Phí vận chuyển:</span>
          <strong>{{ formatVND(orderInfo.shippingFee) }}</strong>
        </div>
        <div class="detail-row" v-if="orderInfo.totalAmount != null">
          <span>Tổng tiền:</span>
          <strong>{{ formatVND(orderInfo.totalAmount) }}</strong>
        </div>
        <div class="detail-row" v-if="orderInfo.paymentMethod">
          <span>Phương thức:</span>
          <strong>{{ getPaymentMethodText(orderInfo.paymentMethod) }}</strong>
        </div>
        <div class="detail-row" v-if="orderInfo.recipientName">
          <span>Người nhận:</span>
          <strong>{{ orderInfo.recipientName }}</strong>
        </div>
      </div>

      <!-- VNPAY SUCCESS INFO -->
      <div v-if="isVNPaySuccess" class="vnpay-success">
        <p>💳 <strong>Thanh toán VNPay thành công!</strong></p>
        <p>Đơn hàng của bạn đã được thanh toán qua VNPay.</p>
      </div>

      <!-- ACTIONS -->
      <div class="actions">
        <button @click="goToOrderDetail" class="btn btn-primary" v-if="orderId">
          Xem chi tiết đơn hàng
        </button>
        <button @click="goToHome" class="btn btn-secondary">
          Về trang chủ
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import api from "@/axios";
import storage, { STORAGE_KEYS } from "@/utils/storage";

export default {
  name: "ThankYou",

  data() {
    return {
      orderId: null,
      orderNumber: null,
      orderInfo: null,
      paymentStatus: "success", // 'success' hoặc 'warning'
      isVNPaySuccess: false,
    };
  },

  computed: {
    pageTitle() {
      if (this.paymentStatus === "success") {
        return this.isVNPaySuccess
          ? "Thanh toán thành công!"
          : "Đặt hàng thành công!";
      }
      return "Đơn hàng đã được tạo";
    },

    pageMessage() {
      if (this.paymentStatus === "success" && this.isVNPaySuccess) {
        return "Cảm ơn bạn đã đặt hàng và thanh toán qua VNPay. Đơn hàng của bạn đang được xử lý.";
      }
      return "Cảm ơn bạn đã đặt hàng. Chúng tôi sẽ liên hệ với bạn sớm nhất!";
    },
  },

  mounted() {
    console.log("\n═══════════════════════════════════════════════════════");
    console.log("🎉 THANK YOU PAGE - Mounted");
    console.log("   Query params:", this.$route.query);
    console.log("═══════════════════════════════════════════════════════");

    this.processQueryParams();
  },

  methods: {
    processQueryParams() {
      const query = this.$route.query;

      // ✅ LẤY ORDER ID và ORDER NUMBER từ query params
      this.orderId = query.orderId || storage.get(STORAGE_KEYS.pendingOrderId);
      this.orderNumber =
        query.orderNumber || storage.get(STORAGE_KEYS.pendingOrderNumber);

      console.log("   Order ID:", this.orderId);
      console.log("   Order Number:", this.orderNumber);

      // ✅ KIỂM TRA VNPAY PAYMENT
      if (query.payment === "success") {
        this.isVNPaySuccess = true;
        this.paymentStatus = "success";
        console.log("   ✅ VNPay payment success detected");
      } else if (query.payment === "failed") {
        this.paymentStatus = "warning";
        console.log("   ❌ VNPay payment failed detected");
      }

      // ✅ LOAD ORDER DETAILS
      if (this.orderId) {
        this.loadOrderDetails(this.orderId);
      } else if (this.orderNumber) {
        this.loadOrderDetailsByNumber(this.orderNumber);
      }

      // ✅ CLEAR PENDING ORDER INFO
      storage.clearPendingOrder();

      console.log("═══════════════════════════════════════════════════════\n");
    },

    async loadOrderDetails(orderId) {
      try {
        const token = storage.getToken();

        if (!token) {
          console.log("   ⚠️  No token, skipping order details load");
          return;
        }

        console.log("   → Loading order details for ID:", orderId);

        const res = await api.get(`/orders/${orderId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (res.data.success && res.data.data) {
          this.orderInfo = res.data.data;
          this.orderNumber = this.orderInfo.orderNumber;
          console.log("   ✅ Order details loaded:", this.orderInfo);
        }
      } catch (error) {
        console.error("   ❌ Error loading order details:", error);
      }
    },

    async loadOrderDetailsByNumber(orderNumber) {
      try {
        const token = storage.getToken();

        if (!token) {
          console.log("   ⚠️  No token, skipping order details load");
          return;
        }

        console.log("   → Loading order details for number:", orderNumber);

        const res = await api.get(`/orders/number/${orderNumber}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (res.data.success && res.data.data) {
          this.orderInfo = res.data.data;
          this.orderId = this.orderInfo.orderId;
          console.log("   ✅ Order details loaded:", this.orderInfo);
        }
      } catch (error) {
        console.error("   ❌ Error loading order details:", error);
      }
    },

    formatVND(amount) {
      return Number(amount).toLocaleString("vi-VN") + " VNĐ";
    },

    getPaymentMethodText(method) {
      const methods = {
        cash: "Tiền mặt",
        vnpay: "VNPay",
        momo: "MoMo",
        bank_transfer: "Chuyển khoản",
      };
      return methods[method] || method;
    },

    goToOrderDetail() {
      if (this.orderId) {
        this.$router.push(`/my-order`);
      }
    },

    goToHome() {
      this.$router.push("/");
    },
  },
};
</script>

<style scoped>
.thank-you-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 2rem;
}

.thank-you-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 600px;
  width: 100%;
  padding: 3rem;
  text-align: center;
  animation: slideUp 0.5s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.icon-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 2rem;
  animation: scaleIn 0.5s ease-out 0.2s both;
}

@keyframes scaleIn {
  0% {
    transform: scale(0);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

.icon-circle.success {
  background: #d1fae5;
  border: 4px solid #10b981;
}

.icon-circle.warning {
  background: #fef3c7;
  border: 4px solid #f59e0b;
}

.icon {
  font-size: 3rem;
  font-weight: bold;
}

.success .icon {
  color: #10b981;
}

.warning .icon {
  color: #f59e0b;
}

h1 {
  font-size: 2rem;
  color: #1f2937;
  margin-bottom: 1rem;
}

.message {
  font-size: 1.1rem;
  color: #4b5563;
  line-height: 1.6;
  margin-bottom: 2rem;
}

.order-details {
  background: #f9fafb;
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 2rem;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  padding: 0.8rem 0;
  border-bottom: 1px solid #e5e7eb;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-row span {
  color: #6b7280;
  font-size: 0.95rem;
}

.detail-row strong {
  color: #1f2937;
  font-size: 1rem;
}

.vnpay-success {
  background: #dbeafe;
  border-left: 4px solid #3b82f6;
  padding: 1rem 1.5rem;
  border-radius: 8px;
  margin-bottom: 2rem;
  text-align: left;
}

.vnpay-success p {
  margin: 0;
  color: #1e40af;
  font-size: 0.95rem;
  line-height: 1.6;
}

.vnpay-success p:first-child {
  margin-bottom: 0.5rem;
}

.actions {
  display: flex;
  gap: 1rem;
}

.btn {
  flex: 1;
  padding: 1rem 2rem;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background: #667eea;
  color: white;
}

.btn-primary:hover {
  background: #5568d3;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-secondary {
  background: #f3f4f6;
  color: #374151;
}

.btn-secondary:hover {
  background: #e5e7eb;
}

@media (max-width: 768px) {
  .thank-you-card {
    padding: 2rem 1.5rem;
  }

  h1 {
    font-size: 1.5rem;
  }

  .actions {
    flex-direction: column;
  }
}
</style>
