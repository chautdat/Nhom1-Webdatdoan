<template>
  <div class="payment-callback">
    <div class="loading" v-if="loading">
      <div class="spinner"></div>
      <p>Đang xử lý thanh toán...</p>
    </div>

    <div v-else class="result">
      <div v-if="success" class="success-icon">✅</div>
      <div v-else class="error-icon">❌</div>

      <h2>{{ message }}</h2>
      <p>{{ description }}</p>

      <router-link to="/orders" class="btn btn-primary">
        Xem đơn hàng
      </router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: "PaymentCallback",

  data() {
    return {
      loading: true,
      success: false,
      message: "",
      description: "",
    };
  },

  mounted() {
    this.handleCallback();
  },

  methods: {
    handleCallback() {
      const params = new URLSearchParams(window.location.search);
      const status = params.get("vnp_ResponseCode") || params.get("status");

      setTimeout(() => {
        this.loading = false;

        if (status === "00" || status === "success") {
          this.success = true;
          this.message = "Thanh toán thành công!";
          this.description = "Đơn hàng của bạn đã được thanh toán";
        } else {
          this.success = false;
          this.message = "Thanh toán chưa hoàn tất";
          this.description = "Bạn có thể thanh toán lại trong mục Đơn hàng của tôi";
        }

        setTimeout(() => {
          this.$router.push("/orders");
        }, 3000);
      }, 1500);
    },
  },
};
</script>

<style scoped>
.payment-callback {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 2rem;
}

.loading,
.result {
  text-align: center;
  max-width: 500px;
}

.spinner {
  width: 60px;
  height: 60px;
  border: 6px solid #e5e7eb;
  border-top-color: #00b067;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1.5rem;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.success-icon,
.error-icon {
  font-size: 4rem;
  margin-bottom: 1.5rem;
}

.btn {
  display: inline-block;
  margin-top: 1.5rem;
  padding: 0.75rem 2rem;
  background: #00b067;
  color: white;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 600;
}

.btn:hover {
  background: #00965c;
}
</style>
