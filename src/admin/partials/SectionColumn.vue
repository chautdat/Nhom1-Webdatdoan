<template>
  <v-card class="mb-4" elevation="6">
    <v-card-title :class="`text-${color}`">{{ title }} ({{ orders.length }})</v-card-title>
    <v-divider></v-divider>
    <v-card-text class="column-body">
      <div v-if="!orders.length" class="text-medium-emphasis text-center py-4">Không có đơn</div>
      <v-card v-for="order in orders" :key="order.orderId" class="mb-3" outlined>
        <v-card-title class="d-flex justify-space-between">
          <span class="font-weight-bold">#{{ order.orderId }}</span>
          <v-chip :color="color" variant="tonal" size="small">{{ order.status }}</v-chip>
        </v-card-title>
        <v-card-text>
          <div class="text-body-2 mb-1">👤 {{ order.userName || 'Khách' }}</div>
          <div class="text-body-2 mb-2">{{ order.items }} món • {{ timeElapsed(order.timestamp) }}</div>
          <v-progress-linear
            v-if="showProgress"
            color="primary"
            height="6"
            rounded
            :model-value="progressValue(order.timestamp)"
          ></v-progress-linear>
        </v-card-text>
        <v-card-actions>
          <v-btn
            v-if="nextStatus"
            size="small"
            :color="color === 'primary' ? 'success' : 'primary'"
            @click="$emit('move', order, nextStatus)"
          >
            {{ primaryAction }}
          </v-btn>
          <v-btn size="small" variant="text" color="error" @click="$emit('remove', order)">XÓA</v-btn>
        </v-card-actions>
      </v-card>
    </v-card-text>
  </v-card>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

defineProps({
  title: String,
  color: String,
  orders: { type: Array, default: () => [] },
  showProgress: { type: Boolean, default: false },
  primaryAction: { type: String, default: '' },
  nextStatus: { type: String, default: '' },
})

defineEmits(['move', 'remove'])

const timeElapsed = (ts) => {
  const diff = Date.now() - new Date(ts).getTime()
  const mins = Math.floor(diff / 60000)
  return `${mins} phút`
}

const progressValue = (ts) => {
  const diff = Date.now() - new Date(ts).getTime()
  return Math.min(100, Math.max(0, (diff / (15 * 60 * 1000)) * 100))
}
</script>

<style scoped>
.column-body {
  max-height: 70vh;
  overflow-y: auto;
}
</style>
