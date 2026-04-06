<template>
  <v-container class="py-8">
    <v-row justify="center">
      <v-col cols="12" md="8">
        <h1 class="text-h4 mb-6 d-flex align-center">
          <v-icon color="primary" class="mr-2">mdi-star</v-icon>
          Đánh giá đơn hàng
        </h1>

        <v-card v-if="loading" class="pa-8 text-center" elevation="8">
          <v-progress-circular indeterminate color="primary"></v-progress-circular>
          <p class="mt-4">Đang tải...</p>
        </v-card>

        <v-card v-else-if="order" elevation="8">
          <v-card-title>
            Đơn hàng #{{ orderId }}
          </v-card-title>
          <v-divider></v-divider>

          <v-card-text class="pa-6">
            <div class="mb-6">
              <p><strong>Ngày:</strong> {{ formatDate(order.createdAt) }}</p>
              <p><strong>Tổng:</strong> {{ formatCurrency(order.finalAmount) }}</p>
            </div>

            <v-divider class="my-4"></v-divider>

            <div class="text-center mb-6">
              <h3 class="text-h6 mb-3">Đánh giá của bạn</h3>
              <v-rating
                v-model="rating"
                color="yellow-darken-2"
                hover
                size="48"
              ></v-rating>
              <p class="mt-2 text-subtitle-1">
                {{ getRatingText(rating) }}
              </p>
            </div>

            <v-textarea
              v-model="comment"
              label="Nhận xét"
              placeholder="Chia sẻ trải nghiệm..."
              variant="outlined"
              rows="5"
              counter="500"
              maxlength="500"
            ></v-textarea>
          </v-card-text>

          <v-divider></v-divider>

          <v-card-actions class="pa-4">
            <v-btn @click="$router.back()">Hủy</v-btn>
            <v-spacer></v-spacer>
            <v-btn
              color="primary"
              size="large"
              :loading="submitting"
              :disabled="!rating"
              @click="submitReview"
            >
              <v-icon left>mdi-send</v-icon>
              Gửi đánh giá
            </v-btn>
          </v-card-actions>
        </v-card>

        <v-alert v-else-if="submitted" type="success" prominent class="mb-6">
          <h3 class="text-h6 mb-2">Cảm ơn!</h3>
          <p class="mb-4">Đánh giá của bạn rất quan trọng.</p>
          <v-btn color="white" variant="outlined" @click="$router.push('/my-order')">
            Xem đơn hàng
          </v-btn>
        </v-alert>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import api from "@/axios";

const route = useRoute();
const orderId = route.params.orderId;

const order = ref(null);
const loading = ref(true);
const submitting = ref(false);
const submitted = ref(false);
const rating = ref(5);
const comment = ref("");

onMounted(() => {
  loadOrder();
});

async function loadOrder() {
  loading.value = true;
  try {
    const response = await api.get(`/orders/${orderId}`);
    order.value = response.data.data;
  } catch (error) {
    console.error("Failed to load order:", error);
    alert("Không thể tải đơn hàng");
  } finally {
    loading.value = false;
  }
}

async function submitReview() {
  submitting.value = true;
  try {
    await api.post("/reviews", {
      orderId,
      rating: rating.value,
      comment: comment.value,
    });
    submitted.value = true;
    alert("Đánh giá đã được gửi!");
  } catch (error) {
    console.error("Submit failed:", error);
    alert(error.response?.data?.message || "Gửi thất bại");
  } finally {
    submitting.value = false;
  }
}

function getRatingText(val) {
  const texts = {
    1: "Rất tệ",
    2: "Tệ",
    3: "Bình thường",
    4: "Tốt",
    5: "Rất tốt",
  };
  return texts[val] || "";
}

function formatDate(dateTime) {
  if (!dateTime) return "";
  return new Date(dateTime).toLocaleDateString("vi-VN");
}

function formatCurrency(amount) {
  if (!amount) return "0₫";
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount);
}
</script>
