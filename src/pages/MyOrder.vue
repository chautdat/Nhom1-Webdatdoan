<template>
  <div class="my-order-page">
    <!-- Modern Header -->
    <header class="page-header">
      <div class="container">
        <div class="header-content">
          <div class="header-left">
            <div class="icon-wrapper">
              <i class="fas fa-receipt"></i>
            </div>
            <div class="header-text">
              <h1>Đơn Hàng Của Tôi</h1>
              <p>Theo dõi và quản lý đơn hàng của bạn</p>
            </div>
          </div>
          <div class="header-right">
            <div class="stat-badge">
              <span class="stat-number">{{ orders.length }}</span>
              <span class="stat-label">Tổng đơn</span>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- Filters Section -->
    <section class="filters-section">
      <div class="container">
        <div class="filters-card">
          <div class="filter-group">
            <label>
              <i class="fas fa-calendar"></i>
              Lọc theo ngày
            </label>
            <div class="date-picker-wrapper">
              <input
                type="date"
                v-model="selectedDate"
                class="date-input"
                placeholder="Chọn ngày"
              />
              <button
                v-if="selectedDate"
                @click="selectedDate = ''"
                class="clear-btn"
                type="button"
              >
                <i class="fas fa-times"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Loading State -->
    <div v-if="isLoading" class="loading-state">
      <div class="spinner"></div>
      <p>Đang tải đơn hàng...</p>
    </div>

    <!-- Orders Grid -->
    <section v-else class="orders-section">
      <div class="container">
        <!-- Orders List -->
        <div v-if="displayedOrders.length > 0" class="orders-grid">
          <div
            v-for="order in displayedOrders"
            :key="order.orderId"
            class="order-card"
          >
            <!-- Card Header -->
            <div class="card-header">
              <div class="order-number" :title="order.orderNumber || order.orderCode">
                <i class="fas fa-hashtag"></i>
                {{ getShortOrderNumber(order) }}
              </div>
              <div class="status-badges">
                <span
                  :class="[
                    'badge',
                    'badge-' + normalizeStatus(order.orderStatus),
                  ]"
                >
                  {{ getOrderStatusText(order.orderStatus) }}
                </span>
                <span
                  :class="[
                    'badge',
                    'badge-payment-' + normalizeStatus(order.paymentStatus),
                  ]"
                >
                  {{ getPaymentStatusText(order.paymentStatus) }}
                </span>
              </div>
            </div>

            <!-- Order Meta Info -->
            <div class="order-meta">
              <div class="meta-item">
                <i class="fas fa-clock"></i>
                <span>{{ formatDate(order.createdAt) }}</span>
              </div>
              <div class="meta-item">
                <i class="fas fa-credit-card"></i>
                <span>{{ getPaymentMethodText(order.paymentMethod) }}</span>
              </div>
            </div>

            <!-- Delivery Info -->
            <div class="delivery-info">
              <div class="info-row">
                <i class="fas fa-map-marker-alt"></i>
                <span>{{ getAddress(order) }}</span>
              </div>
              <div class="info-row">
                <i class="fas fa-phone"></i>
                <span>{{ order.phone }}</span>
              </div>
            </div>

            <!-- ========== ORDER TRACKING MAP (NEW!) ========== -->
            <div v-if="showMap(order)" class="order-tracking-section">
              <h4 class="section-title">
                <i class="fas fa-map-marked-alt"></i>
                Theo Dõi Đơn Hàng
              </h4>

              <!-- Map Container (Leaflet + OSM) -->
              <div :id="'map-' + order.orderId" class="map-container"></div>

              <!-- Delivery Stats -->
              <div class="delivery-stats">
                <div class="stat-card">
                  <div class="stat-icon">
                    <i class="fas fa-route"></i>
                  </div>
                  <div class="stat-content">
                    <p class="stat-label">Khoảng cách</p>
                    <p class="stat-value">{{ order.distance || "N/A" }} km</p>
                  </div>
                </div>

                <div class="stat-card">
                  <div class="stat-icon">
                    <i class="fas fa-clock"></i>
                  </div>
                  <div class="stat-content">
                    <p class="stat-label">Thời gian</p>
                    <p class="stat-value">
                      ~{{ order.duration || "N/A" }} phút
                    </p>
                  </div>
                </div>

                <div class="stat-card">
                  <div class="stat-icon">
                    <i class="fas fa-shipping-fast"></i>
                  </div>
                  <div class="stat-content">
                    <p class="stat-label">Phí ship</p>
                    <p class="stat-value">
                      {{ formatVND(getShipping(order)) }}đ
                    </p>
                  </div>
                </div>
              </div>

              <!-- ETA Countdown (if delivering) -->
              <div
                v-if="order.estimatedArrival && isDelivering(order)"
                class="eta-section"
              >
                <div class="eta-content">
                  <span class="eta-label">🚚 Dự kiến giao:</span>
                  <span class="eta-time">{{
                    formatTime(order.estimatedArrival)
                  }}</span>
                </div>
                <div class="eta-progress">
                  <div
                    class="eta-bar"
                    :style="{ width: getDeliveryProgress(order) + '%' }"
                  ></div>
                </div>
              </div>
            </div>
            <!-- ========== END ORDER TRACKING MAP ========== -->

            <!-- Order Items -->
            <div v-if="getItems(order).length" class="items-section">
              <h4 class="section-title">
                <i class="fas fa-box"></i>
                Sản phẩm ({{ getItems(order).length }})
              </h4>
              <div class="items-list">
                <div
                  v-for="item in getItems(order)"
                  :key="item.orderItemId || item.id"
                  class="item-row"
                >
                  <img :src="getImageUrl(item.productImage)" alt="" />
                  <div class="item-info">
                    <h5>{{ item.productName }}</h5>
                    <span class="item-qty">x{{ item.quantity }}</span>
                  </div>
                  <div class="item-price">{{ formatVND(item.subtotal) }}đ</div>
                </div>
              </div>
            </div>

            <!-- Progress Tracker -->
            <div class="progress-tracker">
              <div class="progress-line">
                <div
                  class="progress-bar"
                  :style="{ width: getProgressWidth(order.orderStatus) + '%' }"
                ></div>
              </div>
              <div class="progress-steps">
                <div
                  v-for="step in progressSteps"
                  :key="step.status"
                  :class="[
                    'step',
                    { active: isStepActive(order.orderStatus, step.status) },
                  ]"
                >
                  <div class="step-icon">
                    <i :class="step.icon"></i>
                  </div>
                  <span class="step-label">{{ step.label }}</span>
                </div>
              </div>
            </div>

            <!-- Payment Warning -->
            <div v-if="showPaymentActions(order)" class="payment-alert">
              <div class="alert-content">
                <i class="fas fa-exclamation-circle"></i>
                <span>Thanh toán online chưa hoàn tất</span>
                <span v-if="getRemainingTime(order)" class="time-left">
                  {{ getRemainingTime(order) }}
                </span>
              </div>
              <div class="alert-actions">
                <button
                  v-if="canRetryPayment(order)"
                  @click="retryPayment(order)"
                  class="btn btn-primary"
                  :disabled="actionLoading"
                >
                  <i class="fas fa-redo"></i>
                  Thanh toán lại
                </button>
                <button
                  v-if="canConvertToCOD(order)"
                  @click="convertToCOD(order)"
                  class="btn btn-secondary"
                  :disabled="actionLoading"
                >
                  <i class="fas fa-money-bill"></i>
                  Chuyển COD
                </button>
                <button
                  v-if="canUserCancel(order)"
                  @click="cancelOrder(order)"
                  class="btn btn-danger"
                  :disabled="actionLoading"
                >
                  <i class="fas fa-times"></i>
                  Hủy đơn
                </button>
              </div>
            </div>

            <!-- Order Summary -->
            <div class="order-summary">
              <div class="summary-row">
                <span>Tạm tính</span>
                <span>{{ formatVND(getSubtotal(order)) }}đ</span>
              </div>
              <div class="summary-row">
                <span>Phí vận chuyển</span>
                <span>{{ formatVND(getShipping(order)) }}đ</span>
              </div>
              <div class="summary-row total">
                <span>Tổng cộng</span>
                <strong>{{ formatVND(getFinalAmount(order)) }}đ</strong>
              </div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else class="empty-state">
          <div class="empty-icon">
            <i class="fas fa-shopping-bag"></i>
          </div>
          <h3>
            {{
              selectedDate
                ? "Không có đơn hàng trong ngày này"
                : "Chưa có đơn hàng nào"
            }}
          </h3>
          <p>
            {{
              selectedDate
                ? "Thử chọn ngày khác hoặc bỏ lọc"
                : "Hãy bắt đầu đặt món ngay!"
            }}
          </p>
          <div class="empty-actions">
            <button
              v-if="selectedDate"
              @click="selectedDate = ''"
              class="btn btn-secondary"
            >
              <i class="fas fa-filter"></i>
              Bỏ lọc
            </button>
            <router-link to="/menu" class="btn btn-primary">
              <i class="fas fa-utensils"></i>
              Đặt món ngay
            </router-link>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
/* eslint-disable no-undef */
import api from "@/axios";
import { mapState } from "vuex";
import storage from "@/utils/storage";
import Swal from "sweetalert2";

export default {
  name: "MyOrder",

  data() {
    return {
      orders: [],
      isLoading: false,
      actionLoading: false,
      refreshTimer: null,
      autoRefreshMs: 7000,
      countdownTimer: null,
      selectedDate: "",

      // ========== MAPS (OSM/Leaflet) ==========
      mapsLoaded: false,
      maps: {}, // Store map instances
      mapRoutes: {}, // Store route layers for each map

      // Restaurant location (fixed)
      restaurantLocation: {
        lat: 10.855232,
        lng: 106.78578,
        address: "Khu Công Nghệ Cao, Phường Hiệp Phú, TP Thủ Đức",
      },

      progressSteps: [
        { status: "pending", icon: "fas fa-clock", label: "Chờ xác nhận" },
        {
          status: "confirmed",
          icon: "fas fa-check-circle",
          label: "Đã xác nhận",
        },
        { status: "preparing", icon: "fas fa-fire", label: "Đang chuẩn bị" },
        {
          status: "shipping",
          icon: "fas fa-shipping-fast",
          label: "Đang giao",
        },
        {
          status: "delivered",
          icon: "fas fa-check-double",
          label: "Hoàn thành",
        },
      ],
    };
  },

  computed: {
    ...mapState(["user"]),

    displayedOrders() {
      if (!this.selectedDate) return this.orders;
      return this.orders.filter((o) => this.isSameDate(o.createdAt));
    },
  },

  mounted() {
    this.loadOrders();
    this.ensureLeaflet(); // Load Leaflet
    this.startAutoRefresh();
    this.countdownTimer = setInterval(() => {
      this.$forceUpdate();
    }, 1000);
  },

  beforeUnmount() {
    this.stopAutoRefresh();
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer);
      this.countdownTimer = null;
    }
  },

  methods: {
    // ========== LEAFLET (OSM) MAP METHODS ==========

    async ensureLeaflet() {
      if (this.mapsLoaded && window.L) return true;

      const css = document.createElement("link");
      css.rel = "stylesheet";
      css.href = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.css";
      document.head.appendChild(css);

      await new Promise((resolve, reject) => {
        const script = document.createElement("script");
        script.src = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.js";
        script.async = true;
        script.onload = () => resolve();
        script.onerror = () => reject();
        document.body.appendChild(script);
      });

      this.mapsLoaded = true;
      return true;
    },

    async initAllMaps() {
      if (!this.displayedOrders.length) return;
      await this.ensureLeaflet();
      for (const order of this.displayedOrders) {
        if (this.showMap(order)) {
          await this.initMap(order);
        }
      }
    },

    showMap(order) {
      // Only show map if order has delivery coordinates and is not cancelled
      return (
        order.deliveryLat &&
        order.deliveryLng &&
        this.normalizeStatus(order.orderStatus) !== "cancelled"
      );
    },

    async initMap(order) {
      if (!this.mapsLoaded || !window.L) return;

      const mapId = "map-" + order.orderId;
      const mapElement = document.getElementById(mapId);
      if (!mapElement) return;

      const L = window.L;
      const delivery = [Number(order.deliveryLat), Number(order.deliveryLng)];
      const restaurant = [
        Number(this.restaurantLocation.lat),
        Number(this.restaurantLocation.lng),
      ];

      let map = this.maps[order.orderId];
      if (!map) {
        map = L.map(mapId, {
          zoomControl: true,
          scrollWheelZoom: true,
        });
        L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
          maxZoom: 19,
          attribution: "© OpenStreetMap contributors",
        }).addTo(map);
        this.maps[order.orderId] = map;
      }

      if (!map._pdqLayers) {
        map._pdqLayers = L.layerGroup().addTo(map);
      }
      map._pdqLayers.clearLayers();

      const restaurantMarker = L.circleMarker(restaurant, {
        radius: 10,
        color: "#00b067",
        fillColor: "#00b067",
        fillOpacity: 0.9,
      }).bindTooltip("PDQ Restaurant");
      const deliveryMarker = L.circleMarker(delivery, {
        radius: 10,
        color: "#3b82f6",
        fillColor: "#3b82f6",
        fillOpacity: 0.9,
      }).bindTooltip("Địa chỉ giao hàng");

      map._pdqLayers.addLayer(restaurantMarker);
      map._pdqLayers.addLayer(deliveryMarker);

      // Clear old route
      if (this.mapRoutes[order.orderId]) {
        map.removeLayer(this.mapRoutes[order.orderId]);
        delete this.mapRoutes[order.orderId];
      }

      // Fit bounds and draw route via OSRM
      const bounds = L.latLngBounds([restaurant, delivery]);
      map.fitBounds(bounds, { padding: [20, 20] });

      const osrmUrl = `https://router.project-osrm.org/route/v1/driving/${restaurant[1]},${restaurant[0]};${delivery[1]},${delivery[0]}?overview=full&geometries=geojson`;
      try {
        const res = await fetch(osrmUrl);
        const data = await res.json();
        const coords = data?.routes?.[0]?.geometry?.coordinates || [];
        if (coords.length) {
          const latlngs = coords.map((c) => [c[1], c[0]]);
          const poly = L.polyline(latlngs, {
            color: "#00b067",
            weight: 5,
            opacity: 0.8,
          }).addTo(map);
          this.mapRoutes[order.orderId] = poly;
          bounds.extend(poly.getBounds());
          map.fitBounds(bounds, { padding: [20, 20] });
        }
      } catch (err) {
        console.error("OSRM route error:", err);
      }
    },

    isDelivering(order) {
      const status = this.normalizeStatus(order.orderStatus);
      return status === "shipping" || status === "delivering";
    },

    formatTime(datetime) {
      if (!datetime) return "N/A";
      return new Date(datetime).toLocaleTimeString("vi-VN", {
        hour: "2-digit",
        minute: "2-digit",
      });
    },

    getDeliveryProgress(order) {
      // Calculate progress based on time
      if (!order.createdAt || !order.estimatedArrival) return 0;

      const created = new Date(order.createdAt);
      const estimated = new Date(order.estimatedArrival);
      const now = new Date();

      const total = estimated - created;
      const elapsed = now - created;

      if (total <= 0) return 0;

      const progress = (elapsed / total) * 100;
      return Math.min(Math.max(progress, 0), 100);
    },

    // ========== END MAP METHODS ==========

    startAutoRefresh() {
      this.stopAutoRefresh();
      this.refreshTimer = setInterval(() => {
        this.loadOrders();
      }, this.autoRefreshMs);
    },

    stopAutoRefresh() {
      if (this.refreshTimer) {
        clearInterval(this.refreshTimer);
        this.refreshTimer = null;
      }
    },

    async loadOrders() {
      if (!this.user) return this.$router.push("/login");

      const showSpinner = this.orders.length === 0;
      if (showSpinner) this.isLoading = true;

      try {
        const res = await api.get("/orders/my-orders");

        const apiData = res.data;
        if (apiData.success) {
          const data = apiData.data;
          const rawOrders = Array.isArray(data)
            ? data
            : apiData.data?.content || [];
          this.orders = rawOrders.map((o) => this.normalizeOrder(o));

          // Initialize maps after orders loaded
          this.$nextTick(() => {
            if (this.mapsLoaded) {
              this.initAllMaps();
            }
          });
        } else {
          this.orders = [];
        }
      } catch (err) {
        console.error("Lỗi tải đơn:", err);
        if (err.response?.status === 401) {
          storage.clearAll();
          this.$store.commit("setUser", null);
          return this.$router.push("/login");
        }
      } finally {
        if (showSpinner) this.isLoading = false;
      }
    },

    isSameDate(date) {
      if (!date || !this.selectedDate) return false;
      const d = new Date(date);
      const t = new Date(this.selectedDate);
      return (
        d.getFullYear() === t.getFullYear() &&
        d.getMonth() === t.getMonth() &&
        d.getDate() === t.getDate()
      );
    },

    getImageUrl(path) {
      if (!path) return "/images/notfound.png";
      const base = "http://localhost:3000";
      if (path.startsWith("http")) return path;
      if (path.startsWith("/uploads")) return base + path;
      return base + "/uploads/" + path;
    },

    formatVND(v) {
      return new Intl.NumberFormat("vi-VN").format(v || 0);
    },

    formatDate(date) {
      if (!date) return "N/A";
      return new Date(date).toLocaleString("vi-VN");
    },

    getShortOrderNumber(order) {
      const code = String(
        order?.orderNumber || order?.orderCode || order?.orderId || "",
      );
      if (!code) return "N/A";
      if (code.length <= 4) return code;
      return code.slice(-4);
    },

    normalizeOrder(order) {
      if (!order) return order;
      return {
        ...order,
        orderStatus: (order.orderStatus || order.status || "")
          .toString()
          .toLowerCase(),
        paymentStatus: (order.paymentStatus || "").toString().toLowerCase(),
        paymentMethod: (order.paymentMethod || "").toString().toLowerCase(),
      };
    },

    normalizeStatus(s) {
      return (s || "").toString().toLowerCase();
    },

    getOrderStatusText(s) {
      const key = this.normalizeStatus(s);
      const map = {
        pending: "Chờ xác nhận",
        confirmed: "Đã xác nhận",
        preparing: "Đang chuẩn bị",
        shipping: "Đang giao",
        delivering: "Đang giao",
        delivered: "Đã giao",
        cancelled: "Đã huỷ",
      };
      return map[key] || s || "Không xác định";
    },

    getPaymentStatusText(s) {
      const key = this.normalizeStatus(s);
      const map = {
        pending: "Chưa thanh toán",
        unpaid: "Chưa thanh toán",
        paid: "Đã thanh toán",
        failed: "Thất bại",
      };
      return map[key] || s || "Không xác định";
    },

    getPaymentMethodText(m) {
      const key = this.normalizeStatus(m);
      const map = { cash: "Tiền mặt", vnpay: "VNPay" };
      return map[key] || m || "Khác";
    },

    getItems(order) {
      if (Array.isArray(order?.items) && order.items.length) return order.items;
      if (Array.isArray(order?.orderItems) && order.orderItems.length)
        return order.orderItems;
      return [];
    },

    getProgressWidth(status) {
      const key = this.normalizeStatus(status);
      const steps = {
        pending: 0,
        confirmed: 25,
        preparing: 50,
        shipping: 75,
        delivering: 75,
        delivered: 100,
        cancelled: 0,
      };
      return steps[key] || 0;
    },

    isStepActive(current, step) {
      const orderStatus = this.normalizeStatus(current);
      const target = this.normalizeStatus(step);
      const arr = [
        "pending",
        "confirmed",
        "preparing",
        "shipping",
        "delivering",
        "delivered",
      ];
      return arr.indexOf(orderStatus) >= arr.indexOf(target);
    },

    getAddress(order) {
      return order.deliveryAddress || order.addressLine || "";
    },

    getSubtotal(order) {
      const candidates = [
        order?.subtotal,
        order?.subTotal,
        order?.totalPrice,
        order?.total,
        order?.amount,
        order?.itemsTotal,
        order?.itemsSubtotal,
        order?.productsTotal,
        order?.totalAmount && order?.shippingFee != null
          ? order.totalAmount - (order.shippingFee || 0)
          : null,
      ].map((v) => Number(v));

      const fromFields = candidates.find((v) => !Number.isNaN(v) && v > 0);
      if (fromFields) return fromFields;

      const items = this.getItems(order);
      if (Array.isArray(items)) {
        const sum = items.reduce((acc, it) => {
          const subtotal = Number(it?.subtotal);
          const priceQty = Number(it?.price) * Number(it?.quantity || 0);
          const itemVal =
            !Number.isNaN(subtotal) && subtotal > 0
              ? subtotal
              : !Number.isNaN(priceQty) && priceQty > 0
              ? priceQty
              : 0;
          return acc + itemVal;
        }, 0);
        if (sum > 0) return sum;
      }

      return 0;
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

    getFinalAmount(order) {
      const shipping = this.getShipping(order);
      const subtotal = this.getSubtotal(order);

      const primaryList = [
        order.finalAmount,
        order.totalAmount,
        order.grandTotal,
        order.totalCost,
        order.total,
      ].map((v) => Number(v));
      const primary = primaryList.find(
        (v) => !Number.isNaN(v) && Number.isFinite(v) && v >= 0,
      );

      const subtotalPlusShip = subtotal + shipping;

      if (
        primary !== undefined &&
        primary !== null &&
        primary >= Math.max(subtotalPlusShip * 0.5, 0)
      ) {
        return primary;
      }

      if (subtotalPlusShip > 0) return subtotalPlusShip;

      return primary || 0;
    },

    canRetryPayment(order) {
      return (
        this.normalizeStatus(order.paymentStatus) === "pending" &&
        this.normalizeStatus(order.paymentMethod) === "vnpay" &&
        (order.retryCount || 0) < (order.maxRetries || 0) &&
        this.getRemainingTime(order) !== null
      );
    },

    canConvertToCOD(order) {
      return (
        this.normalizeStatus(order.paymentStatus) === "pending" &&
        this.normalizeStatus(order.paymentMethod) === "vnpay"
      );
    },

    showPaymentActions(order) {
      const status = this.normalizeStatus(order.orderStatus);
      if (status === "cancelled") return false;
      return this.canRetryPayment(order) || this.canConvertToCOD(order);
    },

    getRemainingTime(order) {
      if (!order.paymentExpiresAt) return null;
      const expiresAt = new Date(order.paymentExpiresAt);
      const now = new Date();
      const diff = expiresAt - now;
      if (diff <= 0) return null;
      const minutes = Math.floor(diff / 60000);
      const seconds = Math.floor((diff % 60000) / 1000);
      return `${minutes}:${seconds < 10 ? "0" : ""}${seconds}`;
    },

    canUserCancel(order) {
      const status = this.normalizeStatus(order.orderStatus);
      if (status !== "pending") return false;
      if (!order.createdAt) return false;
      const created = new Date(order.createdAt);
      const now = new Date();
      const diffMs = now - created;
      return diffMs <= 5 * 60 * 1000;
    },

    async cancelOrder(order) {
      if (!this.canUserCancel(order)) {
        return Swal.fire({
          icon: "info",
          title: "Không thể hủy",
          text: "Chỉ hủy được đơn trong 5 phút sau khi đặt và khi đang chờ xác nhận.",
          confirmButtonColor: "#00b067",
        });
      }

      const confirm = await Swal.fire({
        title: "Hủy đơn hàng?",
        text: "Bạn có chắc muốn hủy đơn này?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#ef4444",
        cancelButtonColor: "#6b7280",
        confirmButtonText: "Hủy đơn",
        cancelButtonText: "Không",
        input: "textarea",
        inputPlaceholder: "Lý do hủy (không bắt buộc)",
      });

      if (!confirm.isConfirmed) return;

      try {
        this.actionLoading = true;
        const token = storage.getToken();
        await api.post(
          `/orders/${order.orderId}/cancel`,
          { reason: confirm.value || "User cancelled" },
          { headers: { Authorization: `Bearer ${token}` } },
        );

        Swal.fire({
          icon: "success",
          title: "Đã hủy đơn",
          showConfirmButton: false,
          timer: 1500,
        });
        this.loadOrders();
      } catch (err) {
        console.error("Cancel order error:", err);
        Swal.fire(
          "Lỗi",
          err?.response?.data?.message || "Không thể hủy đơn",
          "error",
        );
      } finally {
        this.actionLoading = false;
      }
    },

    async retryPayment(order) {
      try {
        this.actionLoading = true;
        const token = storage.getToken();

        const result = await Swal.fire({
          title: "Chọn phương thức thanh toán",
          width: 520,
          input: "select",
          inputOptions: {
            VNPAY: "VNPay",
          },
          inputValue: this.normalizeStatus(order.paymentMethod).toUpperCase(),
          showCancelButton: true,
          confirmButtonText: "Tiếp tục",
          cancelButtonText: "Hủy",
        });

        if (!result.isConfirmed) return;

        const res = await api.post(
          `/orders/${order.orderId}/retry-payment`,
          { paymentMethod: result.value },
          { headers: { Authorization: `Bearer ${token}` } },
        );

        if (res.data?.data?.paymentUrl) {
          window.location.href = res.data.data.paymentUrl;
        } else {
          Swal.fire("Đã chuyển COD", "", "success");
          this.loadOrders();
        }
      } catch (err) {
        console.error("Retry payment error:", err);
        Swal.fire(
          "Lỗi",
          err?.response?.data?.message || "Không thể thanh toán lại",
          "error",
        );
      } finally {
        this.actionLoading = false;
      }
    },

    async convertToCOD(order) {
      const confirm = await Swal.fire({
        title: "Chuyển sang thanh toán tiền mặt?",
        text: "Đơn sẽ thanh toán khi nhận hàng.",
        icon: "question",
        showCancelButton: true,
        confirmButtonColor: "#00b067",
        cancelButtonColor: "#6b7280",
        confirmButtonText: "Xác nhận",
        cancelButtonText: "Hủy",
      });

      if (!confirm.isConfirmed) return;

      try {
        this.actionLoading = true;
        const token = storage.getToken();
        await api.post(
          `/orders/${order.orderId}/convert-to-cod`,
          {},
          { headers: { Authorization: `Bearer ${token}` } },
        );

        Swal.fire({
          icon: "success",
          title: "Đã chuyển sang COD",
          showConfirmButton: false,
          timer: 1500,
        });
        this.loadOrders();
      } catch (err) {
        console.error("Convert COD error:", err);
        Swal.fire(
          "Lỗi",
          err?.response?.data?.message || "Không thể chuyển sang COD",
          "error",
        );
      } finally {
        this.actionLoading = false;
      }
    },
  },
};
</script>

<style scoped>
/* === RESET & VARIABLES === */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.my-order-page {
  --primary: #00b067;
  --primary-dark: #009655;
  --danger: #ef4444;
  --warning: #f59e0b;
  --success: #10b981;
  --gray-50: #f9fafb;
  --gray-100: #f3f4f6;
  --gray-200: #e5e7eb;
  --gray-300: #d1d5db;
  --gray-400: #9ca3af;
  --gray-500: #6b7280;
  --gray-600: #4b5563;
  --gray-700: #374151;
  --gray-800: #1f2937;
  --gray-900: #111827;
  --radius: 16px;
  --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.05);
  --shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
  --shadow-lg: 0 10px 15px -3px rgb(0 0 0 / 0.1);
  --shadow-xl: 0 20px 25px -5px rgb(0 0 0 / 0.1);

  min-height: 100vh;
  background: linear-gradient(to bottom, #f9fafb 0%, #ffffff 100%);
  padding-bottom: 60px;
}

.container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 20px;
}

/* === HEADER === */
.page-header {
  background: linear-gradient(135deg, var(--primary) 0%, #00d97e 100%);
  padding: 40px 0;
  box-shadow: 0 4px 20px rgba(0, 176, 103, 0.15);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.icon-wrapper {
  width: 72px;
  height: 72px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: white;
}

.header-text h1 {
  font-size: 32px;
  font-weight: 800;
  color: white;
  margin-bottom: 6px;
}

.header-text p {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
}

.stat-badge {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  padding: 16px 28px;
  border-radius: 16px;
  text-align: center;
  color: white;
}

.stat-number {
  display: block;
  font-size: 36px;
  font-weight: 900;
  line-height: 1;
  margin-bottom: 6px;
}

.stat-label {
  font-size: 14px;
  font-weight: 600;
  opacity: 0.9;
}

/* === FILTERS === */
.filters-section {
  padding: 32px 0 0;
}

.filters-card {
  background: white;
  border-radius: var(--radius);
  padding: 20px 24px;
  box-shadow: var(--shadow);
  border: 1px solid var(--gray-200);
}

.filter-group {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-group label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: var(--gray-700);
  font-size: 15px;
}

.filter-group label i {
  color: var(--primary);
  font-size: 18px;
}

.date-picker-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-input {
  padding: 10px 16px;
  border: 2px solid var(--gray-200);
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  color: var(--gray-700);
  transition: all 0.2s;
  min-width: 180px;
}

.date-input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(0, 176, 103, 0.1);
}

.clear-btn {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  border: none;
  background: var(--gray-100);
  color: var(--gray-600);
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.clear-btn:hover {
  background: var(--danger);
  color: white;
}

/* === LOADING === */
.loading-state {
  padding: 100px 20px;
  text-align: center;
}

.spinner {
  width: 56px;
  height: 56px;
  border: 4px solid var(--gray-200);
  border-top-color: var(--primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-state p {
  font-size: 16px;
  color: var(--gray-500);
  font-weight: 500;
}

/* === ORDERS SECTION === */
.orders-section {
  padding: 32px 0;
}

.orders-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(520px, 1fr));
  gap: 24px;
}

/* === ORDER CARD === */
.order-card {
  background: white;
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: var(--shadow);
  border: 1px solid var(--gray-200);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.order-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
  border-color: var(--gray-300);
}

/* Card Header */
.card-header {
  background: linear-gradient(135deg, var(--primary) 0%, #00d97e 100%);
  padding: 20px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.order-number {
  display: flex;
  align-items: center;
  gap: 8px;
  color: white;
  font-weight: 700;
  font-size: 16px;
  letter-spacing: 0.3px;
}

.status-badges {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.badge {
  padding: 6px 14px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
}

/* Status Badge Colors */
.badge-pending {
  background: #fef3c7;
  color: #92400e;
}

.badge-confirmed {
  background: #dbeafe;
  color: #1e40af;
}

.badge-preparing {
  background: #fde68a;
  color: #78350f;
}

.badge-shipping {
  background: #bfdbfe;
  color: #1e3a8a;
}

.badge-delivered {
  background: #d1fae5;
  color: #065f46;
}

.badge-cancelled {
  background: #fecaca;
  color: #991b1b;
}

.badge-payment-pending {
  background: #fef3c7;
  color: #92400e;
}

.badge-payment-paid {
  background: #d1fae5;
  color: #065f46;
}

.badge-payment-failed {
  background: #fecaca;
  color: #991b1b;
}

/* Order Meta */
.order-meta {
  padding: 16px 24px;
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  background: var(--gray-50);
  border-bottom: 1px solid var(--gray-200);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--gray-600);
}

.meta-item i {
  color: var(--primary);
  font-size: 16px;
}

/* Delivery Info */
.delivery-info {
  padding: 16px 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  border-bottom: 1px solid var(--gray-200);
}

.info-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  font-size: 14px;
  color: var(--gray-700);
}

.info-row i {
  color: var(--primary);
  margin-top: 2px;
  font-size: 16px;
}

/* ========== ORDER TRACKING MAP (NEW!) ========== */
.order-tracking-section {
  padding: 20px 24px;
  border-bottom: 1px solid var(--gray-200);
  background: linear-gradient(to bottom, #f0fdf4 0%, #ffffff 100%);
}

.map-container {
  height: 350px;
  width: 100%;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: 2px solid var(--gray-200);
}

.delivery-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  background: white;
  border-radius: 12px;
  border: 1px solid var(--gray-200);
  transition: all 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 176, 103, 0.1);
  border-color: var(--primary);
}

.stat-icon {
  width: 42px;
  height: 42px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--primary) 0%, #00d97e 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  box-shadow: 0 4px 10px rgba(0, 176, 103, 0.2);
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 12px;
  color: var(--gray-600);
  font-weight: 500;
  margin-bottom: 2px;
}

.stat-value {
  font-size: 16px;
  font-weight: 700;
  color: var(--gray-800);
}

/* ETA Section */
.eta-section {
  padding: 14px;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-radius: 12px;
  border: 2px solid #fbbf24;
}

.eta-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.eta-label {
  font-size: 14px;
  font-weight: 600;
  color: #78350f;
}

.eta-time {
  font-size: 18px;
  font-weight: 800;
  color: #92400e;
  font-family: monospace;
}

.eta-progress {
  height: 8px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 10px;
  overflow: hidden;
}

.eta-bar {
  height: 100%;
  background: linear-gradient(90deg, #f59e0b 0%, #d97706 100%);
  transition: width 0.5s ease;
}
/* ========== END ORDER TRACKING MAP ========== */

/* Items Section */
.items-section {
  padding: 20px 24px;
  border-bottom: 1px solid var(--gray-200);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 700;
  color: var(--gray-800);
  margin-bottom: 16px;
}

.section-title i {
  color: var(--primary);
}

.items-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.item-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: var(--gray-50);
  border-radius: 12px;
  transition: all 0.2s;
}

.item-row:hover {
  background: var(--gray-100);
}

.item-row img {
  width: 56px;
  height: 56px;
  object-fit: cover;
  border-radius: 10px;
  border: 2px solid white;
  box-shadow: var(--shadow-sm);
}

.item-info {
  flex: 1;
}

.item-info h5 {
  font-size: 14px;
  font-weight: 600;
  color: var(--gray-800);
  margin-bottom: 4px;
}

.item-qty {
  font-size: 13px;
  color: var(--gray-500);
  font-weight: 500;
}

.item-price {
  font-size: 15px;
  font-weight: 700;
  color: var(--primary);
}

/* Progress Tracker */
.progress-tracker {
  padding: 24px;
  border-bottom: 1px solid var(--gray-200);
}

.progress-line {
  height: 6px;
  background: var(--gray-200);
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 24px;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, var(--primary) 0%, #00d97e 100%);
  transition: width 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.progress-steps {
  display: flex;
  justify-content: space-between;
  gap: 8px;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  opacity: 0.4;
  transition: all 0.3s;
}

.step.active {
  opacity: 1;
}

.step-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: var(--gray-200);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
  transition: all 0.3s;
}

.step.active .step-icon {
  background: linear-gradient(135deg, var(--primary) 0%, #00d97e 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(0, 176, 103, 0.3);
  transform: scale(1.05);
}

.step-icon i {
  font-size: 18px;
}

.step-label {
  font-size: 12px;
  font-weight: 600;
  text-align: center;
  color: var(--gray-600);
}

.step.active .step-label {
  color: var(--primary);
}

/* Payment Alert */
.payment-alert {
  margin: 20px 24px;
  padding: 16px;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border: 2px solid #fbbf24;
  border-radius: 12px;
}

.alert-content {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  font-weight: 600;
  color: #78350f;
}

.alert-content i {
  font-size: 20px;
  color: #f59e0b;
}

.time-left {
  margin-left: auto;
  font-family: monospace;
  font-size: 16px;
  color: var(--danger);
}

.alert-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

/* Order Summary */
.order-summary {
  padding: 20px 24px;
  background: linear-gradient(to bottom, white 0%, var(--gray-50) 100%);
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  font-size: 14px;
  color: var(--gray-600);
}

.summary-row.total {
  border-top: 2px solid var(--gray-200);
  margin-top: 8px;
  padding-top: 16px;
  font-size: 18px;
  color: var(--gray-900);
  font-weight: 700;
}

.summary-row.total strong {
  color: var(--primary);
  font-size: 22px;
}

/* Buttons */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 10px;
  border: none;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: linear-gradient(135deg, var(--primary) 0%, #00d97e 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(0, 176, 103, 0.2);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 176, 103, 0.3);
}

.btn-secondary {
  background: white;
  color: var(--gray-700);
  border: 2px solid var(--gray-300);
}

.btn-secondary:hover:not(:disabled) {
  border-color: var(--primary);
  color: var(--primary);
}

.btn-danger {
  background: transparent;
  color: var(--danger);
  border: 2px solid var(--danger);
}

.btn-danger:hover:not(:disabled) {
  background: var(--danger);
  color: white;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 100px 20px;
}

.empty-icon {
  font-size: 120px;
  color: var(--gray-300);
  margin-bottom: 24px;
  opacity: 0.7;
}

.empty-state h3 {
  font-size: 24px;
  font-weight: 700;
  color: var(--gray-800);
  margin-bottom: 12px;
}

.empty-state p {
  font-size: 16px;
  color: var(--gray-500);
  margin-bottom: 32px;
}

.empty-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
}

/* Responsive */
@media (max-width: 1024px) {
  .orders-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 28px 0;
  }

  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .icon-wrapper {
    width: 56px;
    height: 56px;
    font-size: 26px;
  }

  .header-text h1 {
    font-size: 24px;
  }

  .filter-group {
    flex-direction: column;
    align-items: stretch;
  }

  .date-picker-wrapper {
    justify-content: space-between;
  }

  .date-input {
    flex: 1;
  }

  .order-meta {
    flex-direction: column;
    gap: 12px;
  }

  .delivery-stats {
    grid-template-columns: 1fr;
  }

  .map-container {
    height: 280px;
  }

  .progress-steps {
    flex-wrap: wrap;
  }

  .alert-content {
    flex-wrap: wrap;
  }

  .time-left {
    margin-left: 0;
    width: 100%;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 12px;
  }

  .filters-card,
  .order-card {
    border-radius: 12px;
  }

  .btn {
    width: 100%;
  }

  .alert-actions {
    flex-direction: column;
  }

  .empty-icon {
    font-size: 80px;
  }
}
</style>
