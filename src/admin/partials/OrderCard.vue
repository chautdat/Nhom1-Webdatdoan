<template>
  <div class="order-card" :class="`order-${status.toLowerCase()}`">
    <v-card elevation="1" class="full-height">
      <v-card-text class="pa-4">
        <!-- Order Header -->
        <div class="d-flex justify-space-between align-center mb-3">
          <div>
            <div class="font-weight-bold text-h6">
              #{{ order.orderNumber || order.orderId }}
            </div>
            <div class="text-caption text-medium-emphasis">
              {{ formatTime(order.createdAt) }}
            </div>
          </div>
          <v-chip
            :color="getStatusColor(status)"
            text-color="white"
            size="small"
          >
            {{ getStatusLabel(status) }}
          </v-chip>
        </div>

        <!-- Customer Info -->
        <div class="mb-3 pb-3" style="border-bottom: 1px solid #e0e0e0">
          <div class="text-caption text-medium-emphasis">👤 Khách hàng</div>
          <div class="font-weight-medium">{{ order.recipientName }}</div>
          <div class="text-caption">{{ order.phone }}</div>
        </div>

        <!-- Items -->
        <div class="mb-3 pb-3" style="border-bottom: 1px solid #e0e0e0">
          <div class="text-caption text-medium-emphasis">
            🍽️ Món ăn ({{ order.itemCount }})
          </div>
          <div v-if="order.items && order.items.length > 0">
            <div
              v-for="(item, index) in order.items.slice(0, 3)"
              :key="index"
              class="text-caption"
            >
              • {{ item.name }} x{{ item.quantity || 1 }}
            </div>
            <div
              v-if="order.items.length > 3"
              class="text-caption text-primary font-weight-bold"
            >
              +{{ order.items.length - 3 }} more items
            </div>
          </div>
          <div v-else class="text-caption">Không có thông tin chi tiết</div>
        </div>

        <!-- Distance Info -->
        <div
          v-if="order.distance"
          class="mb-3 pb-3"
          style="border-bottom: 1px solid #e0e0e0"
        >
          <div class="text-caption text-medium-emphasis">📍 Khoảng cách</div>
          <div class="font-weight-bold text-primary">
            {{ formatDistance(order.distance) }} km
          </div>
        </div>

        <!-- Actions -->
        <div class="d-flex gap-2">
          <v-btn
            v-if="status === 'PENDING' || status === 'CONFIRMED'"
            color="primary"
            size="small"
            block
            @click="$emit('start-cooking')"
          >
            🔥 Bắt đầu nấu
          </v-btn>
          <v-btn
            v-else-if="status === 'PREPARING'"
            color="success"
            size="small"
            block
            @click="$emit('complete')"
          >
            ✅ Hoàn thành
          </v-btn>
          <v-btn v-else color="info" size="small" block disabled>
            ✓ Sẵn sàng
          </v-btn>
          <v-btn
            color="error"
            variant="tonal"
            size="small"
            icon
            @click="$emit('remove')"
          >
            <v-icon>mdi-trash-can</v-icon>
          </v-btn>
        </div>
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
export default {
  name: "OrderCard",

  props: {
    order: {
      type: Object,
      required: true,
    },
    status: {
      type: String,
      required: true,
    },
  },

  emits: ["start-cooking", "complete", "remove"],

  methods: {
    formatTime(date) {
      if (!date) return "";
      const d = new Date(date);
      return d.toLocaleTimeString("vi-VN", {
        hour: "2-digit",
        minute: "2-digit",
      });
    },

    getStatusLabel(status) {
      const map = {
        PENDING: "⏳ Chờ xử lý",
        CONFIRMED: "✓ Xác nhận",
        PREPARING: "🔥 Đang nấu",
        READY: "✅ Sẵn sàng",
      };
      return map[status] || status;
    },

    getStatusColor(status) {
      const map = {
        PENDING: "warning",
        CONFIRMED: "info",
        PREPARING: "primary",
        READY: "success",
      };
      return map[status] || "default";
    },

    formatDistance(distance) {
      if (!distance || distance === 0) return "N/A";
      return parseFloat(distance).toFixed(1);
    },
  },
};
</script>

<style scoped>
.order-card {
  transition: all 0.3s ease;
}

.order-pending,
.order-confirmed {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.8;
  }
}

.full-height {
  height: 100%;
}
</style>
