<template>
  <div class="payment-result" :class="isSuccess ? 'success' : 'failed'">
    <div class="result-card">
      <div class="icon">{{ isSuccess ? "✓" : "✕" }}</div>
      <h2>{{ isSuccess ? "Thanh toán thành công" : "Thanh toán thất bại" }}</h2>
      <p>Mã đơn: {{ orderId || "N/A" }}</p>
      <p>Số tiền: {{ formatPrice(amount) }}</p>
      <p>Mã kết quả: {{ code || "N/A" }}</p>

      <div class="actions">
        <router-link to="/orders" class="btn btn-primary">Xem đơn hàng của tôi</router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "PaymentResult",
  data() {
    return {
      code: "",
      orderId: "",
      amount: 0,
    };
  },
  computed: {
    isSuccess() {
      return this.code === "00";
    },
  },
  mounted() {
    const params = new URLSearchParams(window.location.search);
    this.code = params.get("code") || "";
    this.orderId = params.get("orderId") || "";
    this.amount = Number(params.get("amount") || 0);
  },
  methods: {
    formatPrice(v) {
      return Number(v || 0).toLocaleString("vi-VN") + "đ";
    },
  },
};
</script>

<style scoped>
.payment-result {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
.result-card {
  padding: 32px;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
  text-align: center;
}
.payment-result.success {
  background: #f0fdf4;
}
.payment-result.failed {
  background: #fff5f5;
}
.icon {
  font-size: 48px;
  margin-bottom: 12px;
}
.btn {
  padding: 10px 16px;
  border-radius: 10px;
  border: none;
  font-weight: 700;
  cursor: pointer;
  text-decoration: none;
}
.btn-primary {
  background: #4f46e5;
  color: #fff;
}
</style>
