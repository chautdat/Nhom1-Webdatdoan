<template>
  <div class="admin-orders">
    <h2>Danh sách đơn hàng (Admin)</h2>

    <div v-if="loading" class="info">Đang tải...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="!orders.length" class="info">Chưa có đơn hàng.</div>

    <table v-else class="orders-table">
      <thead>
        <tr>
          <th>Mã đơn</th>
          <th>User</th>
          <th>Tổng tiền</th>
          <th>Phương thức</th>
          <th>TT Thanh toán</th>
          <th>Trạng thái đơn</th>
          <th>Hành động</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="order in orders" :key="order.orderId">
          <td>{{ order.orderId }}</td>
          <td>{{ order.userId }}</td>
          <td>{{ formatPrice(order.totalAmount) }}</td>
          <td>{{ order.paymentMethod }}</td>
          <td>
            <span
              class="badge"
              :class="badgePaymentStatus(order.paymentStatus)"
            >
              {{ order.paymentStatus }}
            </span>
          </td>
          <td>{{ order.orderStatus }}</td>
          <td>
            <!-- CASH: admin xác nhận thanh toán -->
            <template v-if="isCash(order)">
              <button
                v-if="order.paymentStatus === 'PENDING'"
                class="btn btn-primary"
                :disabled="confirmingId === order.orderId"
                @click="confirmCash(order)"
              >
                {{
                  confirmingId === order.orderId
                    ? "Đang xác nhận..."
                    : "Xác nhận đã thanh toán CASH"
                }}
              </button>
              <span
                v-else-if="order.paymentStatus === 'PAID'"
                class="badge badge-success"
              >
                Đã thanh toán (CASH)
              </span>
            </template>

            <!-- VNPAY: chỉ xem -->
            <template v-else>
              <span
                class="badge"
                :class="badgePaymentStatus(order.paymentStatus)"
              >
                {{ order.paymentStatus }}
              </span>
            </template>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import api from "@/axios";

export default {
  name: "AdminOrders",
  data() {
    return {
      orders: [],
      loading: false,
      error: "",
      confirmingId: null,
    };
  },
  mounted() {
    this.fetchOrders();
  },
  methods: {
    isCash(order) {
      return (order.paymentMethod || "").toUpperCase() === "CASH";
    },
    formatPrice(v) {
      return Number(v || 0).toLocaleString("vi-VN") + "đ";
    },
    badgePaymentStatus(s) {
      const status = (s || "").toUpperCase();
      if (status === "PAID") return "badge-success";
      if (status === "PENDING") return "badge-warning";
      if (status === "FAILED") return "badge-danger";
      return "badge-outline";
    },
    async fetchOrders() {
      this.loading = true;
      this.error = "";
      try {
        const res = await api.get("/admin/orders");
        this.orders = res.data?.data || [];
      } catch (err) {
        this.error = err.response?.data?.message || "Không thể tải đơn hàng";
      } finally {
        this.loading = false;
      }
    },
    async confirmCash(order) {
      try {
        this.confirmingId = order.orderId;
        await api.put(`/admin/orders/${order.orderId}/confirm-cash`);
        await this.fetchOrders();
      } catch (err) {
        alert(err.response?.data?.message || "Không thể xác nhận thanh toán");
      } finally {
        this.confirmingId = null;
      }
    },
  },
};
</script>

<style scoped>
.admin-orders {
  padding: 24px;
}
.orders-table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
}
.orders-table th,
.orders-table td {
  padding: 10px;
  border: 1px solid #e5e7eb;
}
.badge {
  padding: 4px 8px;
  border-radius: 8px;
  font-weight: 700;
  font-size: 12px;
}
.badge-success {
  background: #e5f8ed;
  color: #167d3a;
}
.badge-warning {
  background: #fff7e6;
  color: #ad6800;
}
.badge-danger {
  background: #ffecec;
  color: #c62828;
}
.badge-outline {
  border: 1px solid #e5e7eb;
  color: #444;
}
.btn {
  padding: 8px 12px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 700;
}
.btn-primary {
  background: #4f46e5;
  color: #fff;
}
.info {
  padding: 12px;
  color: #555;
}
.error {
  padding: 12px;
  color: #c62828;
}
</style>
