<template>
  <div class="dashboard-page">
    <v-container fluid class="pa-0 dashboard-shell">
      <v-card class="dashboard-hero" rounded="xl" elevation="0">
        <div class="dashboard-hero__orb dashboard-hero__orb--one"></div>
        <div class="dashboard-hero__orb dashboard-hero__orb--two"></div>

        <div class="dashboard-hero__copy">
          <div class="eyebrow">Admin Dashboard</div>
          <h1 class="dashboard-title">Xin chào, Admin!</h1>
          <p class="dashboard-subtitle">
            Tổng quan ngắn gọn cho vận hành hôm nay: doanh thu, đơn mới và trạng thái xử lý.
          </p>
        </div>

        <div class="dashboard-hero__actions">
          <v-chip
            prepend-icon="mdi-calendar"
            variant="tonal"
            color="primary"
            size="large"
            class="hero-chip"
          >
            {{ getCurrentDateShort() }}
          </v-chip>

          <div class="hero-actions__buttons">
            <v-btn
              :to="{ name: 'AdminOrders' }"
              color="primary"
              variant="flat"
              prepend-icon="mdi-cart"
              class="hero-button"
            >
              Đơn hàng
            </v-btn>
            <v-btn
              :to="{ name: 'AdminMenu' }"
              color="primary"
              variant="tonal"
              prepend-icon="mdi-food"
              class="hero-button"
            >
              Thực đơn
            </v-btn>
          </div>

          <div class="hero-actions__meta">
            <span class="hero-actions__dot"></span>
            <span>{{ pendingCount }} đơn chờ xử lý</span>
          </div>
        </div>
      </v-card>

      <v-row class="dashboard-metrics" dense>
        <v-col
          v-for="card in cards"
          :key="card.key"
          cols="12"
          sm="6"
          xl="3"
        >
          <v-card class="metric-card" rounded="xl" elevation="0">
            <v-avatar :color="card.tone" size="48" class="metric-card__icon">
              <v-icon size="24" color="white">{{ card.icon }}</v-icon>
            </v-avatar>

            <div class="metric-card__body">
              <div class="metric-card__label">{{ card.title }}</div>
              <div class="metric-card__value">{{ card.value }}</div>
              <v-chip
                v-if="card.trend !== null"
                size="x-small"
                :color="card.trend > 0 ? 'success' : 'error'"
                variant="tonal"
                class="metric-card__chip"
              >
                <v-icon start size="x-small">
                  {{ card.trend > 0 ? 'mdi-arrow-up' : 'mdi-arrow-down' }}
                </v-icon>
                {{ Math.abs(card.trend) }}% {{ card.trendLabel }}
              </v-chip>
            </div>
          </v-card>
        </v-col>
      </v-row>

      <div class="dashboard-widgets">
        <v-card class="widget-card widget-card--revenue" rounded="xl" elevation="0">
          <div class="widget-header">
            <div>
              <div class="widget-eyebrow">Tín hiệu vận hành</div>
              <h2 class="widget-title">Doanh thu gần đây</h2>
            </div>
            <v-chip size="small" color="success" variant="tonal">
              {{ previewOrders.length }} đơn gần nhất
            </v-chip>
          </div>

          <div class="revenue-widget">
            <div class="revenue-widget__copy">
              <div class="revenue-widget__label">Tổng doanh thu</div>
              <div class="revenue-widget__value">{{ cards[0].value }}</div>

              <v-chip
                v-if="cards[0].trend !== null"
                size="small"
                :color="cards[0].trend > 0 ? 'success' : 'error'"
                variant="tonal"
                class="revenue-widget__chip"
              >
                <v-icon start size="small">
                  {{ cards[0].trend > 0 ? 'mdi-arrow-up' : 'mdi-arrow-down' }}
                </v-icon>
                {{ Math.abs(cards[0].trend) }}% {{ cards[0].trendLabel }}
              </v-chip>

              <div class="revenue-widget__snapshot">
                <div
                  v-for="item in revenueSnapshot"
                  :key="item.label"
                  class="snapshot-item"
                >
                  <span class="snapshot-item__label">{{ item.label }}</span>
                  <strong class="snapshot-item__value">{{ item.value }}</strong>
                </div>
              </div>
            </div>

            <div class="revenue-widget__chart">
              <svg
                viewBox="0 0 420 180"
                class="revenue-sparkline"
                preserveAspectRatio="none"
              >
                <defs>
                  <linearGradient id="dashboardRevenueFill" x1="0" x2="0" y1="0" y2="1">
                    <stop
                      offset="0%"
                      style="stop-color: rgb(var(--v-theme-primary)); stop-opacity: 0.28"
                    />
                    <stop
                      offset="100%"
                      style="stop-color: rgb(var(--v-theme-primary)); stop-opacity: 0.02"
                    />
                  </linearGradient>
                  <linearGradient id="dashboardRevenueStroke" x1="0" x2="1">
                    <stop offset="0%" stop-color="rgb(var(--v-theme-primary))" />
                    <stop offset="100%" stop-color="rgb(14, 165, 233)" />
                  </linearGradient>
                </defs>
                <path
                  :d="revenueSparkline.area"
                  fill="url(#dashboardRevenueFill)"
                  stroke="none"
                ></path>
                <polyline
                  :points="revenueSparkline.points"
                  fill="none"
                  stroke="url(#dashboardRevenueStroke)"
                  stroke-width="4"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                ></polyline>
              </svg>
            </div>
          </div>
        </v-card>

        <v-card class="widget-card widget-card--orders" rounded="xl" elevation="0">
          <div class="widget-header">
            <div>
              <div class="widget-eyebrow">Đơn mới nhất</div>
              <h2 class="widget-title">Recent Orders</h2>
            </div>
            <v-btn
              :to="{ name: 'AdminOrders' }"
              color="primary"
              variant="tonal"
              size="small"
              append-icon="mdi-arrow-right"
            >
              Xem tất cả
            </v-btn>
          </div>

          <div v-if="loadingOrders" class="widget-state">
            <v-progress-circular indeterminate color="primary" size="40"></v-progress-circular>
            <div>Đang tải đơn gần nhất...</div>
          </div>

          <div v-else-if="!previewOrders.length" class="widget-state widget-state--empty">
            <v-icon size="32" color="grey-lighten-1">mdi-inbox</v-icon>
            <div>Chưa có đơn gần đây</div>
          </div>

          <div v-else class="orders-list">
            <div
              v-for="order in previewOrders"
              :key="order.orderId"
              class="order-row"
            >
              <div class="order-row__left">
                <div class="order-row__code" :title="getOrderCode(order)">
                  #{{ getShortOrderCode(order) }}
                </div>
                <div class="order-row__meta">
                  {{ order.recipientName || 'Khách lẻ' }}
                </div>
              </div>

              <div class="order-row__right">
                <div class="order-row__amount">
                  {{ formatCurrency(order.finalAmount) }}
                </div>
                <v-chip
                  size="x-small"
                  :color="getStatusColor(order.orderStatus)"
                  variant="tonal"
                >
                  {{ getStatusLabel(order.orderStatus) }}
                </v-chip>
              </div>
            </div>
          </div>

          <v-btn
            :to="{ name: 'AdminOrders' }"
            color="primary"
            variant="flat"
            class="widget-cta"
            prepend-icon="mdi-cart"
            block
          >
            Mở trang đơn hàng
          </v-btn>
        </v-card>

        <v-card class="widget-card widget-card--status" rounded="xl" elevation="0">
          <div class="widget-header">
            <div>
              <div class="widget-eyebrow">Nhịp xử lý</div>
              <h2 class="widget-title">Trạng thái đơn</h2>
            </div>
            <v-chip size="small" color="primary" variant="tonal">
              {{ orders.length }} đơn
            </v-chip>
          </div>

          <div class="status-grid">
            <div
              v-for="item in orderFlowItems"
              :key="item.key"
              class="status-card"
            >
              <span class="status-card__label">{{ item.label }}</span>
              <strong class="status-card__value">{{ item.value }}</strong>
              <v-progress-linear
                :model-value="item.ratio"
                :color="item.color"
                height="4"
                rounded
                class="status-card__bar"
              ></v-progress-linear>
            </div>
          </div>

          <div class="status-footer">
            <div class="status-footer__note">
              Vận hành gọn trong một màn hình, không cần mở thêm trang.
            </div>

            <div class="status-footer__actions">
              <v-btn
                :to="{ name: 'AdminOrders' }"
                color="primary"
                variant="tonal"
                size="small"
                prepend-icon="mdi-cart"
              >
                Orders
              </v-btn>
              <v-btn
                :to="{ name: 'AdminMenu' }"
                color="primary"
                variant="tonal"
                size="small"
                prepend-icon="mdi-food"
              >
                Menu
              </v-btn>
            </div>
          </div>
        </v-card>
      </div>
    </v-container>
  </div>
</template>

<script>
import storage from "@/utils/storage";
import api from "@/axios";

export default {
  name: "AdminDashboard",

  data() {
    return {
      cards: [
        {
          key: "revenue",
          title: "Tổng doanh thu",
          value: "Loading...",
          trend: null,
          icon: "mdi-currency-usd",
          tone: "primary",
          trendLabel: "tháng này",
        },
        {
          key: "today-orders",
          title: "Đơn hôm nay",
          value: "Loading...",
          trend: null,
          icon: "mdi-cart",
          tone: "blue-darken-2",
          trendLabel: "so với hôm qua",
        },
        {
          key: "users",
          title: "Người dùng",
          value: "Loading...",
          trend: null,
          icon: "mdi-account-group",
          tone: "blue-darken-3",
          trendLabel: "tăng trưởng",
        },
        {
          key: "products",
          title: "Sản phẩm",
          value: "Loading...",
          trend: null,
          icon: "mdi-silverware-fork-knife",
          tone: "light-blue-darken-1",
          trendLabel: "món mới",
        },
      ],
      orders: [],
      loadingOrders: false,
    };
  },

  computed: {
    pendingCount() {
      return this.orders.filter((order) => this.getOrderStatus(order) === "pending").length;
    },

    previewOrders() {
      return this.orders.slice(0, 3);
    },

    revenueSnapshot() {
      return [
        {
          label: "Đơn hôm nay",
          value: this.cards[1].value,
        },
        {
          label: "Người dùng",
          value: this.cards[2].value,
        },
        {
          label: "Sản phẩm",
          value: this.cards[3].value,
        },
      ];
    },

    revenueSparkline() {
      const series = this.orders
        .map((order) => Number(order?.finalAmount || 0))
        .filter((value) => Number.isFinite(value) && value > 0);

      return this.buildSparkline(series.length ? series : [18, 28, 24, 36, 31, 42, 35], 420, 180);
    },

    orderFlowItems() {
      const statuses = [
        { key: "PENDING", label: "Chờ xử lý", color: "warning" },
        { key: "CONFIRMED", label: "Đã xác nhận", color: "info" },
        { key: "PREPARING", label: "Đang chuẩn bị", color: "primary" },
        { key: "DELIVERING", label: "Đang giao", color: "purple" },
        { key: "DELIVERED", label: "Hoàn thành", color: "success" },
        { key: "CANCELLED", label: "Đã hủy", color: "error" },
      ];

      const total = this.orders.length || 1;

      return statuses.map((status) => {
        const value = this.orders.filter(
          (order) => this.getOrderStatus(order) === status.key,
        ).length;

        return {
          ...status,
          value,
          ratio: Math.round((value / total) * 100),
        };
      });
    },
  },

  mounted() {
    this.fetchDashboardData();
  },

  methods: {
    async fetchDashboardData() {
      const token = storage.getToken();
      if (!token) {
        this.$router.push("/login");
        return;
      }

      await Promise.all([
        this.fetchStats(token),
        this.fetchLatestOrders(token),
      ]);
    },

    async fetchStats(token) {
      const headers = { Authorization: `Bearer ${token}` };

      const [earningsRes, todayOrdersRes, usersRes, productsRes] = await Promise.allSettled([
        api.get("/orders/total-earnings", { headers }),
        api.get("/orders/today-count", { headers }),
        api.get("/users/count", { headers }),
        api.get("/products/count", { headers }),
      ]);

      this.cards[0].value = this.formatCurrency(this.readSettledNumber(earningsRes));
      this.cards[0].trend = 12.5;

      this.cards[1].value = String(this.readSettledNumber(todayOrdersRes));
      this.cards[1].trend = -5.2;

      this.cards[2].value = String(this.readSettledNumber(usersRes));
      this.cards[2].trend = 8.1;

      this.cards[3].value = String(this.readSettledNumber(productsRes));
      this.cards[3].trend = 15.3;
    },

    readSettledNumber(result, fallback = 0) {
      if (!result || result.status !== "fulfilled") return fallback;

      const payload = result.value?.data;

      if (typeof payload === "number") return payload;
      if (typeof payload === "string") return Number(payload) || fallback;
      if (payload?.data !== undefined) return Number(payload.data) || fallback;
      if (payload?.success && payload?.data !== undefined) {
        return Number(payload.data) || fallback;
      }

      return Number(payload) || fallback;
    },

    async fetchLatestOrders(token) {
      this.loadingOrders = true;

      try {
        const res = await api.get("/admin/orders", {
          headers: { Authorization: `Bearer ${token}` },
          params: {
            page: 0,
            size: 3,
            sort: "createdAt,desc",
          },
        });

        this.orders = this.extractOrders(res.data).slice(0, 3);
      } catch (error) {
        this.orders = [];
      } finally {
        this.loadingOrders = false;
      }
    },

    extractOrders(payload) {
      const data = payload?.data ?? payload;

      if (Array.isArray(data)) return data;
      if (Array.isArray(data?.content)) return data.content;
      if (Array.isArray(payload?.data?.content)) return payload.data.content;
      return [];
    },

    getCurrentDateShort() {
      const date = new Date();
      return date.toLocaleDateString("vi-VN", {
        weekday: "short",
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
      });
    },

    getInitials(name) {
      if (!name) return "?";
      return name
        .split(" ")
        .map((n) => n[0])
        .join("")
        .toUpperCase()
        .slice(0, 2);
    },

    getOrderCode(order) {
      return (
        order?.orderNumber || order?.orderCode || `ORD-${order?.orderId || order?.id || "-"}`
      );
    },

    getShortOrderCode(order) {
      const code = String(this.getOrderCode(order) || "");
      if (!code) return "-";
      if (code.length <= 4) return code;
      return code.slice(-4);
    },

    getOrderStatus(order) {
      const status = String(
        order?.orderStatus || order?.status || order?.order_status || "",
      )
        .trim()
        .toLowerCase();

      if (status === "shipping") return "delivering";
      return status;
    },

    getStatusLabel(status) {
      const map = {
        pending: "Chờ xử lý",
        confirmed: "Đã xác nhận",
        preparing: "Đang chuẩn bị",
        ready: "Sẵn sàng",
        delivering: "Đang giao",
        shipping: "Đang giao",
        delivered: "Đã hoàn thành",
        cancelled: "Đã hủy",
      };
      return map[(status || "").toLowerCase()] || status || "Không rõ";
    },

    getStatusColor(status) {
      const map = {
        pending: "warning",
        confirmed: "info",
        preparing: "primary",
        ready: "success",
        delivering: "purple",
        shipping: "purple",
        delivered: "success",
        cancelled: "error",
      };
      return map[(status || "").toLowerCase()] || "default";
    },

    formatPaymentMethod(method) {
      const normalized = String(method || "").toUpperCase();
      if (normalized === "CASH") return "Tiền mặt";
      if (normalized === "VNPAY") return "VNPay";
      return method || "Không rõ";
    },

    formatCurrency(value) {
      const num = Number(value || 0);
      return `${num.toLocaleString("vi-VN")}₫`;
    },

    formatDate(dateStr) {
      if (!dateStr) return "N/A";
      const date = new Date(dateStr);
      return date.toLocaleDateString("vi-VN", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
      });
    },

    buildSparkline(values, width = 420, height = 180) {
      const series = Array.isArray(values) && values.length > 0 ? values : [20, 32, 24, 38, 30, 46, 34];
      const normalizedSeries = series.length === 1 ? [series[0], series[0]] : series;
      const padX = 18;
      const padY = 18;
      const max = Math.max(...normalizedSeries);
      const min = Math.min(...normalizedSeries);
      const range = max - min || 1;
      const usableWidth = width - padX * 2;
      const usableHeight = height - padY * 2;
      const step = usableWidth / Math.max(normalizedSeries.length - 1, 1);

      const points = normalizedSeries.map((value, index) => {
        const x = padX + index * step;
        const y = height - padY - ((value - min) / range) * usableHeight;
        return { x, y };
      });

      const pointString = points.map((point) => `${point.x},${point.y}`).join(" ");
      const areaPath = [
        `M ${points[0].x} ${height - padY}`,
        `L ${points.map((point) => `${point.x} ${point.y}`).join(" L ")}`,
        `L ${points[points.length - 1].x} ${height - padY}`,
        "Z",
      ].join(" ");

      return {
        points: pointString,
        area: areaPath,
      };
    },
  },
};
</script>

<style scoped>
.dashboard-page {
  min-height: 100%;
  background:
    radial-gradient(circle at top right, rgba(var(--v-theme-primary), 0.04), transparent 30%),
    linear-gradient(180deg, rgba(var(--v-theme-surface), 1) 0%, rgba(var(--v-theme-surface), 0.98) 100%);
}

.dashboard-shell {
  max-width: 1720px;
  margin: 0 auto;
  padding: 18px !important;
}

.dashboard-hero {
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  min-height: 120px;
  padding: 20px 22px;
  border: 1px solid rgba(var(--v-theme-primary), 0.12);
  background:
    radial-gradient(circle at 92% 12%, rgba(var(--v-theme-primary), 0.16), transparent 26%),
    radial-gradient(circle at 12% 84%, rgba(var(--v-theme-primary), 0.08), transparent 24%),
    linear-gradient(135deg, rgba(var(--v-theme-surface), 0.98) 0%, rgba(var(--v-theme-primary), 0.05) 100%);
  box-shadow: 0 16px 36px rgba(15, 23, 42, 0.08);
}

.dashboard-hero__orb {
  position: absolute;
  border-radius: 999px;
  pointer-events: none;
  filter: blur(2px);
  opacity: 0.85;
}

.dashboard-hero__orb--one {
  width: 240px;
  height: 240px;
  top: -110px;
  right: -90px;
  background: radial-gradient(circle, rgba(var(--v-theme-primary), 0.18) 0%, transparent 70%);
}

.dashboard-hero__orb--two {
  width: 200px;
  height: 200px;
  left: -90px;
  bottom: -110px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.12) 0%, transparent 70%);
}

.dashboard-hero__copy {
  position: relative;
  z-index: 1;
  flex: 1 1 520px;
  min-width: 0;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.18em;
  font-size: 11px;
  font-weight: 800;
  color: rgb(var(--v-theme-primary));
  margin-bottom: 8px;
}

.dashboard-title {
  margin: 0 0 8px;
  font-size: clamp(28px, 2.6vw, 42px);
  line-height: 1.08;
  font-weight: 900;
  color: rgb(var(--v-theme-on-surface));
}

.dashboard-subtitle {
  margin: 0;
  max-width: 720px;
  font-size: 15px;
  line-height: 1.5;
  color: rgba(var(--v-theme-on-surface), 0.72);
}

.dashboard-hero__actions {
  position: relative;
  z-index: 1;
  flex: 0 1 360px;
  min-width: 280px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10px;
}

.hero-chip {
  min-height: 42px;
  font-weight: 700;
  border: 1px solid rgba(var(--v-theme-primary), 0.12);
  background: rgba(var(--v-theme-primary), 0.08);
}

.hero-actions__buttons {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.hero-button {
  width: 100%;
  min-width: 0;
  height: 42px !important;
  border-radius: 14px !important;
  font-weight: 700 !important;
  letter-spacing: 0.01em !important;
}

.hero-actions__meta {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  font-size: 13px;
  font-weight: 700;
  color: rgba(var(--v-theme-on-surface), 0.68);
}

.hero-actions__dot {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: rgb(var(--v-theme-primary));
  box-shadow: 0 0 0 6px rgba(var(--v-theme-primary), 0.12);
}

.dashboard-metrics {
  margin-top: 14px;
}

.metric-card {
  min-height: 108px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 14px;
  border: 1px solid rgba(var(--v-theme-on-surface), 0.06);
  background: linear-gradient(180deg, rgba(var(--v-theme-surface), 0.98) 0%, rgba(var(--v-theme-primary), 0.02) 100%);
  box-shadow: 0 12px 26px rgba(15, 23, 42, 0.06);
}

.metric-card__icon {
  flex: 0 0 auto;
  box-shadow: 0 14px 26px rgba(59, 130, 246, 0.18);
}

.metric-card__body {
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.metric-card__label {
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: rgba(var(--v-theme-on-surface), 0.56);
}

.metric-card__value {
  margin-top: 4px;
  font-size: 28px;
  line-height: 1.05;
  font-weight: 900;
  color: rgb(var(--v-theme-on-surface));
}

.metric-card__chip {
  align-self: flex-start;
  margin-top: 6px;
}

.dashboard-widgets {
  margin-top: 14px;
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(0, 0.95fr) minmax(0, 0.9fr);
  gap: 14px;
  align-items: stretch;
}

.widget-card {
  min-height: 304px;
  display: flex;
  flex-direction: column;
  border: 1px solid rgba(var(--v-theme-on-surface), 0.06);
  background: rgba(var(--v-theme-surface), 0.98);
  box-shadow: 0 14px 30px rgba(15, 23, 42, 0.07);
}

.widget-header {
  padding: 18px 20px 12px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.widget-eyebrow {
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: rgba(var(--v-theme-on-surface), 0.56);
}

.widget-title {
  margin: 4px 0 0;
  font-size: 21px;
  line-height: 1.1;
  font-weight: 900;
  color: rgb(var(--v-theme-on-surface));
}

.revenue-widget {
  flex: 1;
  padding: 0 20px 18px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 1.12fr;
  gap: 14px;
  align-items: center;
}

.revenue-widget__copy {
  min-width: 0;
}

.revenue-widget__label {
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: rgba(var(--v-theme-on-surface), 0.56);
}

.revenue-widget__value {
  margin-top: 8px;
  font-size: clamp(32px, 2.6vw, 44px);
  line-height: 1.02;
  font-weight: 900;
  color: rgb(var(--v-theme-on-surface));
}

.revenue-widget__chip {
  margin-top: 10px;
}

.revenue-widget__snapshot {
  margin-top: 12px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.snapshot-item {
  padding: 12px 12px 11px;
  border-radius: 16px;
  border: 1px solid rgba(var(--v-theme-on-surface), 0.06);
  background: linear-gradient(135deg, rgba(var(--v-theme-surface), 0.98), rgba(var(--v-theme-primary), 0.03));
}

.snapshot-item__label {
  display: block;
  font-size: 10px;
  font-weight: 800;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: rgba(var(--v-theme-on-surface), 0.56);
}

.snapshot-item__value {
  display: block;
  margin-top: 6px;
  font-size: 17px;
  line-height: 1.1;
  font-weight: 900;
  color: rgb(var(--v-theme-on-surface));
}

.revenue-widget__chart {
  min-height: 180px;
  border-radius: 22px;
  overflow: hidden;
  border: 1px solid rgba(var(--v-theme-on-surface), 0.06);
  background: linear-gradient(180deg, rgba(var(--v-theme-primary), 0.05), rgba(var(--v-theme-surface), 0.96));
}

.revenue-sparkline {
  width: 100%;
  height: 100%;
  display: block;
}

.widget-state {
  flex: 1;
  min-height: 162px;
  padding: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  text-align: center;
  color: rgba(var(--v-theme-on-surface), 0.68);
}

.widget-state--empty {
  flex-direction: column;
}

.orders-list {
  flex: 1;
  padding: 0 18px 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.order-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 16px;
  border: 1px solid rgba(var(--v-theme-on-surface), 0.06);
  background: linear-gradient(135deg, rgba(var(--v-theme-surface), 0.98), rgba(var(--v-theme-primary), 0.02));
}

.order-row__left,
.order-row__right {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-row__code {
  font-size: 14px;
  font-weight: 900;
  line-height: 1.1;
  color: rgb(var(--v-theme-on-surface));
}

.order-row__meta {
  font-size: 12px;
  color: rgba(var(--v-theme-on-surface), 0.62);
}

.order-row__amount {
  font-size: 13px;
  font-weight: 900;
  color: rgb(var(--v-theme-on-surface));
  text-align: right;
}

.widget-cta {
  margin: 0 18px 18px;
  height: 42px !important;
  border-radius: 14px !important;
  font-weight: 800 !important;
}

.status-grid {
  flex: 1;
  padding: 0 18px 14px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.status-card {
  padding: 11px 12px 10px;
  border-radius: 16px;
  border: 1px solid rgba(var(--v-theme-on-surface), 0.06);
  background: linear-gradient(135deg, rgba(var(--v-theme-surface), 0.98), rgba(var(--v-theme-primary), 0.02));
}

.status-card__label {
  display: block;
  font-size: 10px;
  font-weight: 800;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: rgba(var(--v-theme-on-surface), 0.56);
}

.status-card__value {
  display: block;
  margin-top: 6px;
  font-size: 16px;
  line-height: 1.1;
  font-weight: 900;
  color: rgb(var(--v-theme-on-surface));
}

.status-card__bar {
  margin-top: 8px;
  overflow: hidden;
  border-radius: 999px;
}

.status-footer {
  padding: 0 18px 18px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.status-footer__note {
  font-size: 13px;
  line-height: 1.45;
  color: rgba(var(--v-theme-on-surface), 0.68);
}

.status-footer__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

@media (max-width: 1280px) {
  .dashboard-widgets {
    grid-template-columns: 1fr 1fr;
  }

  .widget-card--status {
    grid-column: 1 / -1;
  }
}

@media (max-width: 960px) {
  .dashboard-shell {
    padding: 16px !important;
  }

  .dashboard-hero {
    padding: 18px;
    align-items: flex-start;
    flex-direction: column;
  }

  .dashboard-hero__actions {
    width: 100%;
    align-items: flex-start;
  }

  .hero-actions__meta {
    justify-content: flex-start;
  }

  .dashboard-widgets {
    grid-template-columns: 1fr;
  }

  .widget-card--status {
    grid-column: auto;
  }

  .revenue-widget {
    grid-template-columns: 1fr;
  }

  .revenue-widget__chart {
    min-height: 160px;
  }
}

@media (max-width: 640px) {
  .dashboard-title {
    font-size: 26px;
  }

  .hero-actions__buttons {
    grid-template-columns: 1fr;
  }

  .metric-card {
    min-height: 100px;
  }

  .widget-title {
    font-size: 18px;
  }

  .revenue-widget__snapshot {
    grid-template-columns: 1fr;
  }

  .status-grid {
    grid-template-columns: 1fr;
  }
}
</style>
