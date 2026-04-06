<template>
  <div class="orders-page">
    <div class="orders-header">
      <h1>📦 Đơn Hàng Của Tôi</h1>
      <p>Theo dõi và quản lý đơn hàng của bạn</p>
    </div>

    <div class="filters">
      <button
        v-for="status in orderStatuses"
        :key="status.value"
        :class="['filter-btn', { active: activeFilter === status.value }]"
        @click="activeFilter = status.value"
      >
        {{ status.label }}
        <span class="count">{{ getCountByStatus(status.value) }}</span>
      </button>
    </div>

    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Đang tải đơn hàng...</p>
    </div>

    <div v-else-if="filteredOrders.length" class="orders-list">
      <div
        v-for="order in filteredOrders"
        :key="order.orderId"
        class="order-card"
      >
        <div class="order-header">
          <div class="order-info">
            <h3>{{ order.orderCode || order.orderNumber }}</h3>
            <span :class="['status-badge', (order.status || '').toLowerCase()]">
              {{ getStatusLabel(order.status) }}
            </span>
          </div>
          <div class="order-date">{{ formatDate(order.createdAt) }}</div>
        </div>

        <div class="order-items">
          <div
            v-for="item in order.items"
            :key="item.orderItemId"
            class="order-item"
          >
            <img
              :src="getProductImage(item.productImage)"
              :alt="item.productName"
            />
            <div class="item-info">
              <h4>{{ item.productName }}</h4>
              <p>x{{ item.quantity }}</p>
            </div>
            <div class="item-price">{{ formatPrice(item.subtotal) }}đ</div>
          </div>
        </div>

        <div class="order-footer">
          <div class="order-total">
            <div class="total-row">
              <span>Phí ship:</span>
              <span>{{ formatPrice(getShipping(order)) }}đ</span>
            </div>
            <div class="total-row">
              <span>Tổng cộng:</span>
              <strong>{{ formatPrice(order.totalAmount) }}đ</strong>
            </div>
          </div>
          <div class="order-actions">
            <button
              v-if="(order.status || '').toUpperCase() === 'PENDING'"
              class="btn btn-danger"
              @click="openCancelModal(order)"
            >
              ❌ Hủy đơn
            </button>
            <button class="btn btn-outline" @click="viewDetails(order)">
              👁️ Chi tiết
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <div class="empty-icon">📦</div>
      <h2>Chưa có đơn hàng nào</h2>
      <p>Hãy khám phá thực đơn và đặt món ngay!</p>
      <router-link to="/menu" class="btn btn-primary"
        >🍽️ Xem thực đơn</router-link
      >
    </div>

    <cancel-modal
      v-if="showCancelModal"
      :order="selectedOrder"
      @close="showCancelModal = false"
      @cancelled="handleOrderCancelled"
    />
  </div>
</template>

<script>
import api from "@/axios";
import storage from "@/utils/storage";
import CancelModal from "@/components/CancelModal.vue";

export default {
  name: "Orders",
  components: { CancelModal },

  data() {
    return {
      orders: [],
      loading: false,
      activeFilter: "ALL",
      showCancelModal: false,
      selectedOrder: null,
      orderStatuses: [
        { value: "ALL", label: "Tất cả" },
        { value: "PENDING", label: "Chờ xác nhận" },
        { value: "CONFIRMED", label: "Đã xác nhận" },
        { value: "PREPARING", label: "Đang chuẩn bị" },
        { value: "SHIPPING", label: "Đang giao" },
        { value: "DELIVERED", label: "Đã giao" },
        { value: "CANCELLED", label: "Đã hủy" },
      ],
    };
  },

  computed: {
    filteredOrders() {
      if (this.activeFilter === "ALL") return this.orders;
      return this.orders.filter(
        (o) => (o.status || "").toUpperCase() === this.activeFilter,
      );
    },
  },

  mounted() {
    this.loadOrders();
  },

  methods: {
    async loadOrders() {
      try {
        this.loading = true;
        const token = storage.getToken();
        const res = await api.get("/orders/my-orders", {
          headers: token ? { Authorization: `Bearer ${token}` } : {},
        });
        const data = res.data.data || [];
        this.orders = Array.isArray(data)
          ? data.map((o) => this.normalizeOrder(o))
          : [];
        console.log("✅ Loaded orders:", this.orders.length);
      } catch (err) {
        console.error("❌ Error loading orders:", err);
        alert("Không thể tải danh sách đơn hàng");
      } finally {
        this.loading = false;
      }
    },

    normalizeOrder(order) {
      if (!order) return order;
      let status = (order.status || order.orderStatus || "")
        .toString()
        .toUpperCase();
      if (status === "DELIVERING") status = "SHIPPING";
      const orderCode =
        order.orderCode || order.orderNumber || `ORD-${order.orderId}`;
      return {
        ...order,
        status,
        orderCode,
        paymentStatus: (order.paymentStatus || "").toUpperCase(),
        paymentMethod: (order.paymentMethod || "").toUpperCase(),
      };
    },

    handleOrderCancelled() {
      this.showCancelModal = false;
      this.loadOrders();
    },

    getCountByStatus(status) {
      if (status === "ALL") return this.orders.length;
      return this.orders.filter(
        (o) => (o.status || "").toUpperCase() === status,
      ).length;
    },

    getStatusLabel(status) {
      const found = this.orderStatuses.find(
        (s) => s.value === (status || "").toUpperCase(),
      );
      return found ? found.label : status;
    },

    formatDate(date) {
      return new Date(date).toLocaleString("vi-VN");
    },

    formatPrice(price) {
      const value = typeof price === "number" ? price : Number(price || 0);
      return new Intl.NumberFormat("vi-VN").format(value);
    },

    getShipping(order) {
      const candidates = [
        order?.shippingFee,
        order?.deliveryFee,
        order?.shippingCost,
        order?.feeShip,
        order?.feeShipping,
      ].map((v) => Number(v));
      const found = candidates.find((v) => !Number.isNaN(v));
      return found || 0;
    },

    getProductImage(url) {
      if (!url) return "/images/notfound.png";
      if (url.startsWith("http")) return url;
      return `http://localhost:3000${url.startsWith("/") ? "" : "/"}${url}`;
    },

    openCancelModal(order) {
      this.selectedOrder = order;
      this.showCancelModal = true;
    },

    viewDetails(order) {
      this.$router.push(`/orders/${order.orderId}/track`);
    },
  },
};
</script>

<style scoped>
.orders-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}
.orders-header {
  text-align: center;
  margin-bottom: 2rem;
}
.orders-header h1 {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
}
.orders-header p {
  color: #6b7280;
}
.filters {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  justify-content: center;
}
.filter-btn {
  padding: 0.5rem 1rem;
  border: 2px solid #e5e7eb;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
.filter-btn:hover {
  border-color: #00b067;
}
.filter-btn.active {
  background: #00b067;
  color: white;
  border-color: #00b067;
}
.count {
  background: rgba(0, 0, 0, 0.1);
  padding: 0.2rem 0.5rem;
  border-radius: 999px;
  font-size: 0.875rem;
}
.loading {
  text-align: center;
  padding: 3rem;
}
.spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #e5e7eb;
  border-top-color: #00b067;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
.orders-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}
.order-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #e5e7eb;
}
.order-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}
.order-info h3 {
  font-size: 1.25rem;
  font-weight: 700;
  margin: 0;
}
.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 999px;
  font-size: 0.875rem;
  font-weight: 600;
  text-transform: capitalize;
}
.status-badge.pending {
  background: #fef3c7;
  color: #92400e;
}
.status-badge.confirmed {
  background: #dbeafe;
  color: #1e40af;
}
.status-badge.preparing {
  background: #fce7f3;
  color: #9f1239;
}
.status-badge.shipping {
  background: #e0e7ff;
  color: #3730a3;
}
.status-badge.delivered {
  background: #d1fae5;
  color: #065f46;
}
.status-badge.cancelled {
  background: #fee2e2;
  color: #991b1b;
}
.order-date {
  color: #6b7280;
  font-size: 0.875rem;
}
.order-items {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-bottom: 1rem;
}
.order-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.75rem;
  background: #f9fafb;
  border-radius: 8px;
}
.order-item img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
}
.item-info {
  flex: 1;
}
.item-info h4 {
  font-size: 1rem;
  font-weight: 600;
  margin: 0 0 0.25rem 0;
}
.item-info p {
  margin: 0;
  color: #6b7280;
  font-size: 0.875rem;
}
.item-price {
  font-weight: 700;
  color: #00b067;
}
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 1rem;
  border-top: 1px solid #e5e7eb;
}
.order-total {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}
.total-row {
  display: flex;
  gap: 0.5rem;
  align-items: baseline;
}
.order-total strong {
  font-size: 1.25rem;
  color: #00b067;
}
.order-actions {
  display: flex;
  gap: 0.5rem;
}
.btn {
  padding: 0.5rem 1rem;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s;
}
.btn-primary {
  background: #00b067;
  color: white;
}
.btn-primary:hover {
  background: #00965c;
}
.btn-danger {
  background: #ef4444;
  color: white;
}
.btn-danger:hover {
  background: #dc2626;
}
.btn-outline {
  background: white;
  border: 2px solid #e5e7eb;
  color: #374151;
}
.btn-outline:hover {
  border-color: #00b067;
  color: #00b067;
}
.btn-warning {
  background: #f59e0b;
  color: white;
}
.btn-warning:hover:not(:disabled) {
  background: #d97706;
}
.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.countdown {
  font-size: 0.875rem;
  font-weight: 700;
  color: #ef4444;
  margin-left: 0.5rem;
}
.payment-warning {
  background: #fef3c7;
  border: 1px solid #fbbf24;
  border-radius: 8px;
  padding: 0.75rem;
  margin-bottom: 0.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
.payment-actions-inline {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}
.empty-state {
  text-align: center;
  padding: 3rem;
}
.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}
.empty-state h2 {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
}
.empty-state p {
  color: #6b7280;
  margin-bottom: 1.5rem;
}
</style>
