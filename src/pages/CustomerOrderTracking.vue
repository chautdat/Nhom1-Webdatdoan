<template>
  <v-container class="py-8 tracking-page">
    <v-row justify="center">
      <v-col cols="12" md="8" lg="6">
        <v-card elevation="8" class="pa-4">
          <v-card-title class="d-flex align-center justify-space-between">
            <div>
              <div class="text-h6 font-weight-bold">Theo dõi đơn #{{ orderId }}</div>
              <div class="text-caption text-medium-emphasis">{{ formatDate(order?.createdAt) }}</div>
            </div>
            <v-chip :color="statusColor(order?.status)" text-color="white" size="small">
              {{ order?.status || 'PENDING' }}
            </v-chip>
          </v-card-title>
          <v-divider class="my-3"></v-divider>

          <v-timeline side="end" density="comfortable">
            <v-timeline-item
              v-for="step in steps"
              :key="step.value"
              :color="stepColor(step.value)"
              :icon="step.icon"
            >
              <template #opposite>
                <span class="text-caption">{{ step.label }}</span>
              </template>
              <div class="text-body-2" :class="{ 'font-weight-bold': step.value === order?.status }">
                {{ step.description }}
              </div>
              <v-progress-linear
                v-if="step.value === 'PREPARING' && order?.status === 'PREPARING'"
                color="primary"
                height="6"
                class="mt-2"
                rounded
                :model-value="preparingProgress"
              ></v-progress-linear>
            </v-timeline-item>
          </v-timeline>

          <v-card class="mt-4" variant="tonal">
            <v-card-text>
              <div class="d-flex justify-space-between mb-2">
                <span>Tổng tiền</span>
                <strong>{{ formatCurrency(order?.finalAmount) }}</strong>
              </div>
              <div class="d-flex justify-space-between mb-2">
                <span>Phí ship</span>
                <strong>{{ formatCurrency(order?.shippingFee) }}</strong>
              </div>
              <div class="d-flex justify-space-between mb-2">
                <span>Phương thức</span>
                <strong>{{ order?.paymentMethod || 'N/A' }}</strong>
              </div>
              <div class="d-flex justify-space-between mb-2">
                <span>Số món</span>
                <strong>{{ order?.items?.length || 0 }}</strong>
              </div>
            </v-card-text>
          </v-card>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import { useWebSocket } from '@/composables/useWebSocket'
import storage from '@/utils/storage'

const route = useRoute()
const orderId = route.params.id || route.params.orderId
const order = ref(null)
const steps = [
  { value: 'PENDING', label: 'Chờ xác nhận', description: 'Đơn đã tạo', icon: 'mdi-clock' },
  { value: 'CONFIRMED', label: 'Đã xác nhận', description: 'Nhà hàng đã nhận', icon: 'mdi-check' },
  { value: 'PREPARING', label: 'Đang nấu', description: 'Bếp đang chuẩn bị', icon: 'mdi-silverware-fork-knife' },
  { value: 'READY', label: 'Sẵn sàng', description: 'Đơn đã sẵn sàng', icon: 'mdi-food' },
  { value: 'DELIVERED', label: 'Đã giao', description: 'Hoàn tất', icon: 'mdi-truck-check' },
]

const preparingProgress = computed(() => {
  if (!order.value?.statusTimestamp) return 20
  const diff = Date.now() - new Date(order.value.statusTimestamp).getTime()
  const percent = Math.min(100, Math.max(0, (diff / (15 * 60 * 1000)) * 100))
  return percent
})

const API_URL = 'http://localhost:3000/api'
const user = storage.getUser?.() || null
const token = storage.getToken?.() || localStorage.getItem('token')
const { subscribe } = useWebSocket('http://localhost:3000/ws', {
  headers: token ? { Authorization: `Bearer ${token}` } : {},
})

const stepColor = (val) => {
  const map = {
    PENDING: 'warning',
    CONFIRMED: 'primary',
    PREPARING: 'primary',
    READY: 'success',
    DELIVERED: 'success',
    CANCELLED: 'error'
  }
  const doneIndex = steps.findIndex((s) => s.value === order.value?.status)
  const idx = steps.findIndex((s) => s.value === val)
  if (idx < doneIndex) return 'success'
  return map[val] || 'grey'
}

const statusColor = (status) => stepColor(status)

const formatDate = (date) => (date ? new Date(date).toLocaleString('vi-VN') : '')
const formatCurrency = (val) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val || 0)

const loadOrder = async () => {
  try {
    const token = storage.getToken?.() || localStorage.getItem('token')
    const res = await axios.get(`${API_URL}/orders/${orderId}`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {},
    })
    order.value = res.data.data || res.data
  } catch (err) {
    console.error('Load order failed', err)
  }
}

const handleUpdate = (msg) => {
  if (msg.orderId != orderId) return
  order.value = { ...order.value, ...msg }
  order.value.statusTimestamp = new Date()
}

onMounted(() => {
  loadOrder()
  const topic = user?.id ? `/user/${user.id}/queue/orders` : `/topic/orders/status`
  subscribe(topic, handleUpdate)
})
</script>

<style scoped>
.tracking-page {
  background: linear-gradient(135deg, #f5f8ff 0%, #eef3fb 100%);
  min-height: 100vh;
}
</style>
