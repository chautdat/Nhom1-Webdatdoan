<template>
  <div class="orders-admin">
    <header class="header">
      <div class="header-left">
        <h1 class="title">📦 Orders Management</h1>
        <p class="subtitle">Quản lý đơn hàng theo thời gian thực</p>
      </div>

      <div class="stats">
        <div class="stat-box">
          <div class="stat-icon">📊</div>
          <div class="stat-info">
            <p class="stat-number">{{ totalOrdersDisplay }}</p>
            <span class="stat-label">Tổng đơn</span>
          </div>
        </div>

        <div class="stat-box pink">
          <div class="stat-icon">⏳</div>
          <div class="stat-info">
            <p class="stat-number">{{ pendingCount }}</p>
            <span class="stat-label">Chờ xử lý</span>
          </div>
        </div>

        <div class="stat-box green">
          <div class="stat-icon">💵</div>
          <div class="stat-info">
            <p class="stat-number">{{ cashPendingCount }}</p>
            <span class="stat-label">Cần xác nhận CASH</span>
          </div>
        </div>
      </div>
    </header>

    <div v-if="errorMsg" class="error-banner">
      ⚠️ {{ errorMsg }}
      <button class="banner-close" @click="errorMsg = ''">✕</button>
    </div>

    <div class="table-card">
      <div class="card-header">
        <h2 class="card-title">📋 Danh Sách Đơn Hàng</h2>
        <span class="count-badge">{{ filteredOrders.length }}</span>
      </div>

      <div class="search-toolbar">
        <input
          v-model.trim="searchQuery"
          type="text"
          class="search-input"
          placeholder="🔍 Tìm theo mã đơn, tên khách, số điện thoại"
        />

        <select v-model="selectedStatus" class="form-select compact">
          <option value="ALL">Tất cả trạng thái</option>
          <option
            v-for="status in statusOptions"
            :key="status.value"
            :value="status.value"
          >
            {{ status.label }}
          </option>
        </select>

        <select v-model.number="pageSize" class="form-select compact">
          <option :value="10">10 / trang</option>
          <option :value="20">20 / trang</option>
          <option :value="50">50 / trang</option>
        </select>

        <button
          class="btn btn-secondary refresh-btn"
          @click="refresh"
          :disabled="loading"
        >
          Làm mới
        </button>
      </div>

      <div v-if="loading && !orders.length" class="loading-state">
        <div class="spinner"></div>
        <p>Đang tải danh sách đơn hàng...</p>
      </div>

      <div v-else-if="!filteredOrders.length" class="empty-state">
        <div class="empty-icon">📭</div>
        <p class="empty-text">Không có đơn hàng phù hợp</p>
      </div>

      <div v-else class="table-wrapper">
        <table class="data-table">
          <thead>
            <tr>
              <th>Mã đơn</th>
              <th>Khách hàng</th>
              <th>Tổng tiền</th>
              <th>Thanh toán</th>
              <th>Trạng thái đơn</th>
              <th>Ngày tạo</th>
              <th>Thao tác</th>
            </tr>
          </thead>

          <tbody>
            <tr
              v-for="order in filteredOrders"
              :key="getOrderId(order)"
              class="data-row"
            >
              <td>
                <div class="order-code-wrap">
                  <div class="order-code" :title="getOrderCode(order)">
                    {{ getShortOrderCode(order) }}
                  </div>
                </div>
                <div class="muted">#{{ getOrderId(order) }}</div>
              </td>

              <td>
                <div class="customer-name">
                  {{ order.recipientName || "Khách lẻ" }}
                </div>
                <div class="muted">{{ order.phone || "Không có SĐT" }}</div>
              </td>

              <td>
                <span class="total-amount">{{
                  formatPrice(order.totalAmount)
                }}</span>
              </td>

              <td>
                <div class="payment-method">
                  {{ formatPaymentMethod(order.paymentMethod) }}
                </div>
                <span
                  class="payment-badge"
                  :class="paymentBadgeClass(order.paymentStatus)"
                >
                  {{ normalizePaymentStatus(order.paymentStatus) }}
                </span>
              </td>

              <td>
                <div class="status-cell">
                  <span
                    class="status-pill"
                    :class="orderBadgeClass(getOrderStatus(order))"
                  >
                    {{ orderStatusLabel(getOrderStatus(order)) }}
                  </span>

                  <div class="status-actions">
                    <select
                      v-model="statusDraft[getOrderId(order)]"
                      class="form-select mini status-select"
                      :disabled="updatingStatusId === getOrderId(order)"
                    >
                      <option
                        v-for="status in statusOptions"
                        :key="`${getOrderId(order)}-${status.value}`"
                        :value="status.value"
                      >
                        {{ status.label }}
                      </option>
                    </select>

                    <button
                      class="btn-update status-update"
                      :disabled="!canUpdateStatus(order)"
                      @click="changeStatus(order)"
                    >
                      {{
                        updatingStatusId === getOrderId(order)
                          ? "Đang cập nhật..."
                          : "Cập nhật"
                      }}
                    </button>
                  </div>
                </div>
              </td>

              <td>{{ formatDate(order.createdAt) }}</td>

              <td>
                <div class="col-actions">
                  <button
                    v-if="getNextOrderStatus(getOrderStatus(order))"
                    class="btn-action btn-advance"
                    :disabled="updatingStatusId === getOrderId(order)"
                    :title="`Chuyển sang ${orderStatusLabel(
                      getNextOrderStatus(getOrderStatus(order)),
                    )}`"
                    @click="advanceStatus(order)"
                  >
                    {{
                      updatingStatusId === getOrderId(order)
                        ? "Đang chuyển..."
                        : `→ ${orderStatusLabel(
                            getNextOrderStatus(getOrderStatus(order)),
                          )}`
                    }}
                  </button>

                  <button
                    v-if="showConfirmCash(order)"
                    class="btn-action btn-cash"
                    :disabled="confirmingCashId === getOrderId(order)"
                    @click="confirmCash(order)"
                  >
                    {{
                      confirmingCashId === getOrderId(order)
                        ? "Đang xác nhận..."
                        : "Xác nhận CASH"
                    }}
                  </button>

                  <button
                    class="btn-action btn-delete"
                    :disabled="deletingId === getOrderId(order)"
                    @click="deleteOrder(order)"
                  >
                    {{
                      deletingId === getOrderId(order) ? "Đang xóa..." : "Xóa"
                    }}
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="table-footer" v-if="totalPages > 0">
        <button
          class="btn-page"
          :disabled="loading || page === 0"
          @click="changePage(page - 1)"
        >
          Trang trước
        </button>

        <div class="paging-info">
          Trang {{ page + 1 }} / {{ totalPages }}
          <span v-if="totalElements >= 0">(Tổng {{ totalElements }} đơn)</span>
        </div>

        <button
          class="btn-page"
          :disabled="loading || page + 1 >= totalPages"
          @click="changePage(page + 1)"
        >
          Trang sau
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import api from "@/axios";
import Swal from "sweetalert2";

export default {
  name: "AdminOrders",
  data() {
    return {
      orders: [],
      loading: false,
      errorMsg: "",
      searchQuery: "",
      selectedStatus: "ALL",
      page: 0,
      pageSize: 10,
      totalPages: 0,
      totalElements: 0,
      statusDraft: {},
      updatingStatusId: null,
      deletingId: null,
      confirmingCashId: null,
      statusOptions: [
        { value: "PENDING", label: "Chờ xử lý" },
        { value: "CONFIRMED", label: "Đã xác nhận" },
        { value: "PREPARING", label: "Đang chuẩn bị" },
        { value: "READY", label: "Sẵn sàng" },
        { value: "DELIVERING", label: "Đang giao" },
        { value: "DELIVERED", label: "Đã giao" },
        { value: "CANCELLED", label: "Đã hủy" },
      ],
    };
  },
  computed: {
    filteredOrders() {
      const q = this.searchQuery.toLowerCase().trim();
      if (!q) return this.orders;

      return this.orders.filter((order) => {
        const id = String(this.getOrderId(order) || "").toLowerCase();
        const code = String(this.getOrderCode(order) || "").toLowerCase();
        const name = String(order.recipientName || "").toLowerCase();
        const phone = String(order.phone || "").toLowerCase();

        return (
          id.includes(q) ||
          code.includes(q) ||
          name.includes(q) ||
          phone.includes(q)
        );
      });
    },
    totalOrdersDisplay() {
      if (this.totalElements > 0) return this.totalElements;
      return this.orders.length;
    },
    pendingCount() {
      return this.orders.filter(
        (order) => this.getOrderStatus(order) === "PENDING",
      ).length;
    },
    cashPendingCount() {
      return this.orders.filter((order) => this.showConfirmCash(order)).length;
    },
  },
  watch: {
    selectedStatus() {
      this.page = 0;
      this.fetchOrders();
    },
    pageSize() {
      this.page = 0;
      this.fetchOrders();
    },
  },
  mounted() {
    this.fetchOrders();
  },
  methods: {
    getOrderId(order) {
      return order?.orderId ?? order?.id;
    },
    getOrderCode(order) {
      return (
        order?.orderNumber ||
        order?.orderCode ||
        `ORD-${this.getOrderId(order)}`
      );
    },
    getShortOrderCode(order) {
      const code = String(this.getOrderCode(order) || "");
      if (!code) return "-";
      if (code.length <= 4) return code;
      return code.slice(-4);
    },
    getOrderStatus(order) {
      return String(
        order?.orderStatus || order?.status || "PENDING",
      ).toUpperCase();
    },
    orderStatusLabel(status) {
      const map = {
        PENDING: "Chờ xử lý",
        CONFIRMED: "Đã xác nhận",
        PREPARING: "Đang chuẩn bị",
        READY: "Sẵn sàng",
        DELIVERING: "Đang giao",
        DELIVERED: "Đã giao",
        CANCELLED: "Đã hủy",
      };
      return map[status] || status;
    },
    normalizePaymentStatus(value) {
      return String(value || "UNKNOWN").toUpperCase();
    },
    formatPaymentMethod(method) {
      const m = String(method || "").toUpperCase();
      if (m === "CASH") return "Tiền mặt";
      return method || "Không rõ";
    },
    formatPrice(value) {
      return new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      }).format(Number(value || 0));
    },
    formatDate(value) {
      if (!value) return "-";
      const d = new Date(value);
      if (Number.isNaN(d.getTime())) return "-";
      return d.toLocaleString("vi-VN");
    },
    paymentBadgeClass(status) {
      const s = this.normalizePaymentStatus(status);
      if (s === "PAID") return "paid";
      if (s === "PENDING") return "pending";
      if (s === "FAILED" || s === "EXPIRED") return "failed";
      return "neutral";
    },
    orderBadgeClass(status) {
      const map = {
        PENDING: "status-pending",
        CONFIRMED: "status-confirmed",
        PREPARING: "status-preparing",
        READY: "status-ready",
        DELIVERING: "status-delivering",
        DELIVERED: "status-delivered",
        CANCELLED: "status-cancelled",
      };
      return map[status] || "status-neutral";
    },
    getNextOrderStatus(status) {
      const current = String(status || "").toUpperCase();
      const flow = {
        PENDING: "CONFIRMED",
        CONFIRMED: "PREPARING",
        PREPARING: "READY",
        READY: "DELIVERING",
        DELIVERING: "DELIVERED",
      };
      return flow[current] || "";
    },
    canUpdateStatus(order) {
      const id = this.getOrderId(order);
      if (this.updatingStatusId === id) return false;

      const current = this.getOrderStatus(order);
      const next = this.statusDraft[id];

      return Boolean(next) && next !== current;
    },
    showConfirmCash(order) {
      return (
        String(order?.paymentMethod || "").toUpperCase() === "CASH" &&
        this.normalizePaymentStatus(order?.paymentStatus) === "PENDING"
      );
    },
    hydrateStatusDraft() {
      const nextDraft = {};
      this.orders.forEach((order) => {
        nextDraft[this.getOrderId(order)] = this.getOrderStatus(order);
      });
      this.statusDraft = nextDraft;
    },
    async fetchOrders() {
      this.loading = true;
      this.errorMsg = "";

      try {
        const params = {
          page: this.page,
          size: this.pageSize,
        };

        if (this.selectedStatus !== "ALL") {
          params.status = this.selectedStatus.toLowerCase();
        }

        const res = await api.get("/admin/orders", { params });
        const payload = res.data?.data;

        const list = Array.isArray(payload)
          ? payload
          : Array.isArray(payload?.content)
          ? payload.content
          : [];

        this.orders = list;
        this.totalElements = Number(payload?.totalElements ?? list.length);
        this.totalPages = Number(payload?.totalPages ?? (list.length ? 1 : 0));

        this.hydrateStatusDraft();
      } catch (error) {
        this.errorMsg =
          error.response?.data?.message || "Không thể tải danh sách đơn hàng";
      } finally {
        this.loading = false;
      }
    },
    async advanceStatus(order) {
      const id = this.getOrderId(order);
      const nextStatus = this.getNextOrderStatus(this.getOrderStatus(order));
      if (!nextStatus) return;

      this.statusDraft[id] = nextStatus;
      await this.changeStatus(order);
    },
    async changeStatus(order) {
      const id = this.getOrderId(order);
      const nextStatus = this.statusDraft[id];
      if (!nextStatus) return;

      this.updatingStatusId = id;
      this.errorMsg = "";

      try {
        await api.patch(`/admin/orders/${id}/status`, { status: nextStatus });
        await Swal.fire({
          icon: "success",
          title: "Cập nhật thành công",
          text: `Đơn ${this.getOrderCode(
            order,
          )} đã chuyển sang trạng thái ${this.orderStatusLabel(nextStatus)}`,
          timer: 1600,
          showConfirmButton: false,
        });
        await this.fetchOrders();
      } catch (error) {
        this.errorMsg =
          error.response?.data?.message || "Không thể cập nhật trạng thái đơn";
        await Swal.fire({
          icon: "error",
          title: "Cập nhật thất bại",
          text: this.errorMsg,
        });
      } finally {
        this.updatingStatusId = null;
      }
    },
    async confirmCash(order) {
      const id = this.getOrderId(order);

      this.confirmingCashId = id;
      this.errorMsg = "";

      try {
        await api.put(`/admin/orders/${id}/confirm-cash`);
        await Swal.fire({
          icon: "success",
          title: "Xác nhận thành công",
          text: `Đã xác nhận thanh toán CASH cho đơn ${this.getOrderCode(
            order,
          )}`,
          timer: 1600,
          showConfirmButton: false,
        });
        await this.fetchOrders();
      } catch (error) {
        this.errorMsg =
          error.response?.data?.message || "Không thể xác nhận thanh toán CASH";
        await Swal.fire({
          icon: "error",
          title: "Xác nhận thất bại",
          text: this.errorMsg,
        });
      } finally {
        this.confirmingCashId = null;
      }
    },
    async deleteOrder(order) {
      const id = this.getOrderId(order);
      const code = this.getOrderCode(order);

      const result = await Swal.fire({
        title: "Xóa đơn hàng?",
        text: "Thao tác này không thể hoàn tác.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#6366f1",
        cancelButtonColor: "#6b7280",
        confirmButtonText: "Xóa",
        cancelButtonText: "Hủy",
      });
      if (!result.isConfirmed) return;

      this.deletingId = id;
      this.errorMsg = "";

      try {
        await api.delete(`/admin/orders/${id}`);
        await Swal.fire({
          icon: "success",
          title: "Đã xóa đơn",
          text: `Đơn ${code} đã được xóa`,
          timer: 1600,
          showConfirmButton: false,
        });

        if (this.orders.length === 1 && this.page > 0) {
          this.page -= 1;
        }

        await this.fetchOrders();
      } catch (error) {
        this.errorMsg =
          error.response?.data?.message || "Không thể xóa đơn hàng";
        await Swal.fire({
          icon: "error",
          title: "Xóa thất bại",
          text: this.errorMsg,
        });
      } finally {
        this.deletingId = null;
      }
    },
    changePage(nextPage) {
      if (nextPage < 0 || nextPage >= this.totalPages) return;
      this.page = nextPage;
      this.fetchOrders();
    },
    refresh() {
      this.fetchOrders();
    },
  },
};
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.orders-admin {
  padding: 1.8rem 4%;
  max-width: 1600px;
  margin: 0 auto;
  min-height: 100vh;
  background: linear-gradient(135deg, #f4f7fb 0%, #e1e9f7 100%);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    "Helvetica Neue", Arial, sans-serif;
}

.error-banner {
  padding: 10px 16px;
  border-radius: 10px;
  margin-bottom: 1.2rem;
  font-weight: 600;
  font-size: 1.08rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.error-banner {
  background: #fee2e2;
  color: #b91c1c;
  border: 1px solid #fecaca;
}

.banner-close {
  border: none;
  background: transparent;
  color: inherit;
  cursor: pointer;
  font-size: 1.05rem;
  font-weight: 700;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem 2rem;
  color: #3b82f6;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f4f7fb;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-state p {
  font-size: 1.05rem;
  font-weight: 600;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  gap: 1.5rem;
}

.header-left {
  flex: 1;
}

.title {
  font-size: 2.35rem;
  font-weight: 800;
  color: #2563eb;
  margin: 0 0 0.4rem 0;
  letter-spacing: -0.5px;
}

.subtitle {
  font-size: 1.15rem;
  color: #64748b;
  margin: 0;
}

.stats {
  display: flex;
  gap: 1.2rem;
}

.stat-box {
  background: rgba(59, 130, 246, 0.08);
  backdrop-filter: blur(10px);
  padding: 1.1rem 1.5rem;
  border-radius: 14px;
  display: flex;
  align-items: center;
  gap: 0.85rem;
  min-width: 150px;
  transition: all 0.3s;
  border: 1px solid rgba(59, 130, 246, 0.15);
}

.stat-box:hover {
  background: rgba(59, 130, 246, 0.12);
  transform: translateY(-2px);
  box-shadow: 0 3px 10px rgba(59, 130, 246, 0.15);
}

.stat-box.pink {
  background: rgba(244, 63, 94, 0.08);
  border: 1px solid rgba(244, 63, 94, 0.15);
}

.stat-box.green {
  background: rgba(16, 185, 129, 0.08);
  border: 1px solid rgba(16, 185, 129, 0.2);
}

.stat-icon {
  font-size: 2rem;
  line-height: 1;
}

.stat-number {
  font-size: 1.9rem;
  font-weight: 800;
  color: #2563eb;
  margin: 0;
  line-height: 1;
}

.stat-label {
  font-size: 1rem;
  color: #64748b;
  font-weight: 600;
}

.table-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.card-header {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  padding: 1.5rem 1.8rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 1.55rem;
  font-weight: 700;
  color: #fff;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.6rem;
}

.count-badge {
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  padding: 0.35rem 0.85rem;
  border-radius: 12px;
  font-size: 0.98rem;
  font-weight: 700;
  color: #fff;
}

.search-toolbar {
  padding: 1.2rem 1.6rem;
  border-bottom: 1px solid #f3f4f6;
  display: grid;
  grid-template-columns: 1fr 220px 140px 120px;
  gap: 0.9rem;
  align-items: center;
}

.search-input,
.form-select {
  width: 100%;
  padding: 0.85rem 1rem;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  font-size: 1.05rem;
  font-family: inherit;
  background: #fafbfc;
  transition: all 0.3s;
}

.search-input:focus,
.form-select:focus {
  outline: none;
  border-color: #3b82f6;
  background: #fff;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
}

.compact {
  padding-top: 0.72rem;
  padding-bottom: 0.72rem;
}

.mini {
  padding: 0.58rem 0.7rem;
  font-size: 0.98rem;
}

.btn {
  border: none;
  border-radius: 10px;
  font-size: 1.02rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #eef2ff;
  color: #3730a3;
  border: 1px solid #c7d2fe;
}

.btn-secondary:hover {
  background: #e0e7ff;
}

.refresh-btn {
  padding: 0.75rem 0.9rem;
}

.empty-state {
  text-align: center;
  padding: 3rem 2rem;
  color: #9ca3af;
}

.empty-icon {
  font-size: 4rem;
  opacity: 0.3;
  margin-bottom: 1rem;
}

.empty-text {
  font-size: 1.12rem;
  font-weight: 600;
  margin: 0;
}

.table-wrapper {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 1.02rem;
}

.data-table thead {
  background: #f9fafb;
  border-bottom: 2px solid #e5e7eb;
}

.data-table th {
  padding: 1rem 0.85rem;
  text-align: left;
  font-size: 0.95rem;
  font-weight: 700;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  white-space: nowrap;
}

.data-row {
  border-bottom: 1px solid #f3f4f6;
  transition: background 0.2s;
}

.data-row:hover {
  background: #f9fafb;
}

.data-table td {
  padding: 1rem 0.85rem;
  vertical-align: top;
}

.order-code {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1f2937;
  max-width: 220px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-code-wrap {
  display: flex;
  flex-direction: column;
  gap: 0.18rem;
}

.customer-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1f2937;
}

.muted {
  color: #64748b;
  font-size: 0.98rem;
  margin-top: 0.2rem;
}

.total-amount {
  font-size: 1.16rem;
  font-weight: 700;
  color: #10b981;
  white-space: nowrap;
}

.payment-method {
  font-size: 1.05rem;
  color: #374151;
  margin-bottom: 0.3rem;
}

.payment-badge,
.status-pill {
  display: inline-flex;
  align-items: center;
  padding: 0.32rem 0.75rem;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 700;
  white-space: nowrap;
}

.payment-badge.pending {
  background: #fef3c7;
  color: #92400e;
}

.payment-badge.paid {
  background: #d1fae5;
  color: #065f46;
}

.payment-badge.failed {
  background: #fee2e2;
  color: #991b1b;
}

.payment-badge.neutral {
  background: #e5e7eb;
  color: #4b5563;
}

.status-pill.status-pending {
  background: #fef3c7;
  color: #92400e;
}

.status-pill.status-confirmed,
.status-pill.status-preparing,
.status-pill.status-delivering {
  background: #dbeafe;
  color: #1e40af;
}

.status-pill.status-ready,
.status-pill.status-delivered {
  background: #d1fae5;
  color: #065f46;
}

.status-pill.status-cancelled {
  background: #fee2e2;
  color: #991b1b;
}

.status-pill.status-neutral {
  background: #e5e7eb;
  color: #4b5563;
}

.status-cell {
  display: flex;
  flex-direction: column;
  gap: 0.52rem;
  min-width: 170px;
}

.status-actions {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.status-select {
  flex: 1 1 128px;
  min-width: 128px;
}

.status-update {
  flex: 0 0 auto;
  min-width: 96px;
}

.btn-update {
  border: none;
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  color: #fff;
  padding: 0.55rem 0.8rem;
  border-radius: 8px;
  font-size: 0.98rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-update:hover {
  transform: translateY(-1px);
  box-shadow: 0 3px 8px rgba(79, 70, 229, 0.25);
}

.btn-update:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.col-actions {
  display: flex;
  gap: 0.65rem;
  align-items: flex-start;
  flex-wrap: wrap;
  min-width: 210px;
}

.btn-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.55rem 0.95rem;
  border: none;
  border-radius: 8px;
  font-size: 0.98rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.btn-advance {
  background: #eef2ff;
  color: #4338ca;
  border: 1px solid #c7d2fe;
  white-space: normal;
  line-height: 1.1;
  min-width: 132px;
  text-align: center;
}

.btn-advance:hover {
  background: #e0e7ff;
  transform: translateY(-1px);
}

.btn-cash {
  background: #f59e0b;
  color: #fff;
}

.btn-cash:hover {
  background: #d97706;
  transform: translateY(-1px);
}

.btn-delete {
  background: #ef4444;
  color: #fff;
}

.btn-delete:hover {
  background: #dc2626;
  transform: translateY(-1px);
}

.btn-action:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.table-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 0.75rem;
  padding: 1rem 1.4rem 1.4rem;
  border-top: 1px solid #f3f4f6;
}

.btn-page {
  padding: 0.65rem 0.95rem;
  border: 1px solid #c7d2fe;
  border-radius: 9px;
  background: #eef2ff;
  color: #4338ca;
  font-size: 0.97rem;
  font-weight: 700;
  cursor: pointer;
}

.btn-page:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.paging-info {
  color: #334155;
  font-size: 1.02rem;
  font-weight: 600;
}

@media (max-width: 1200px) {
  .search-toolbar {
    grid-template-columns: 1fr 1fr;
  }

  .refresh-btn {
    grid-column: span 2;
  }
}

@media (max-width: 768px) {
  .orders-admin {
    padding: 1.2rem 4%;
  }

  .title {
    font-size: 2rem;
  }

  .stats {
    width: 100%;
    flex-direction: column;
  }

  .stat-box {
    width: 100%;
  }

  .search-toolbar {
    grid-template-columns: 1fr;
  }

  .refresh-btn {
    grid-column: span 1;
  }

  .data-table {
    min-width: 1080px;
  }
}

@media (max-width: 1280px) {
  .status-cell {
    min-width: 170px;
  }
}
</style>
