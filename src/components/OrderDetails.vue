<template>
  <div class="order-details-container">
    <h2 class="title">📄 Chi tiết đơn hàng #{{ billId }}</h2>

    <!-- Loading -->
    <div v-if="loading" class="loading">Đang tải dữ liệu...</div>

    <!-- Error -->
    <div v-if="errorMsg" class="error">{{ errorMsg }}</div>

    <!-- Order Info -->
    <div v-if="order" class="order-info">
      <p><strong>Khách hàng:</strong> {{ order.customerName }}</p>
      <p><strong>Số điện thoại:</strong> {{ order.phone }}</p>
      <p><strong>Bàn:</strong> {{ order.tableName }}</p>
      <p><strong>Ngày tạo:</strong> {{ formatDate(order.createdAt) }}</p>
      <p><strong>Tổng tiền:</strong> {{ formatPrice(order.totalPrice) }}</p>
    </div>

    <!-- Details Table -->
    <div v-if="details.length" class="details-table">
      <table>
        <thead>
          <tr>
            <th>Món</th>
            <th>Giá</th>
            <th>SL</th>
            <th>Tạm tính</th>
            <th></th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="item in details" :key="item.detailId">
            <td>{{ item.productName }}</td>
            <td>{{ formatPrice(item.price) }}</td>
            <td>{{ item.quantity }}</td>
            <td>{{ formatPrice(item.price * item.quantity) }}</td>
            <td>
              <button class="btn-delete" @click="removeItem(item.detailId)">
                ❌ Xoá
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="!details.length && !loading" class="empty">
      Không có món nào trong đơn hàng.
    </div>
  </div>
</template>

<script>
import api from "@/axios"; // axios có token

export default {
  name: "OrderDetails",

  data() {
    return {
      billId: null,
      order: null,
      details: [],
      loading: true,
      errorMsg: "",
    };
  },

  created() {
    this.billId = this.$route.params.id;
    this.fetchOrder();
    this.fetchDetails();
  },

  methods: {
    // Format Giá
    formatPrice(price) {
      return Number(price).toLocaleString("vi-VN") + "đ";
    },

    // Format Ngày
    formatDate(date) {
      return new Date(date).toLocaleString("vi-VN");
    },

    // ==========================
    // 🔥 LẤY THÔNG TIN ĐƠN HÀNG
    // ==========================
    async fetchOrder() {
      try {
        const res = await api.get(`/bill/${this.billId}`);
        this.order = res.data.data;
      } catch (err) {
        this.errorMsg = "Không thể lấy thông tin hóa đơn!";
      }
    },

    // ==========================
    // 🔥 LẤY DANH SÁCH MÓN TRONG ĐƠN
    // ==========================
    async fetchDetails() {
      try {
        const res = await api.get(`/billdetail/${this.billId}`);
        this.details = res.data.data || [];
      } catch (err) {
        this.errorMsg = "Không thể lấy danh sách món!";
      } finally {
        this.loading = false;
      }
    },

    // ==========================
    // ❌ XOÁ MÓN TRONG ĐƠN
    // ==========================
    async removeItem(detailId) {
      if (!confirm("Bạn chắc chắn muốn xoá món này?")) return;

      try {
        await api.delete(`/billdetail/${detailId}`);
        alert("Đã xoá thành công!");
        this.fetchDetails(); // refresh lại bảng
      } catch (err) {
        alert("Không thể xoá món!");
      }
    },
  },
};
</script>

<style scoped>
.order-details-container {
  padding: 20px;
}

.title {
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 15px;
}

.loading {
  font-size: 18px;
  color: #555;
}

.error {
  padding: 10px;
  background: #ffdddd;
  border-left: 4px solid red;
  margin-bottom: 15px;
}

.order-info p {
  font-size: 16px;
  margin: 5px 0;
}

.details-table {
  margin-top: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th {
  background: #f5f5f5;
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ccc;
}

td {
  padding: 12px;
  border-bottom: 1px solid #eee;
}

.btn-delete {
  background: #ff4d4d;
  border: none;
  padding: 6px 12px;
  color: white;
  border-radius: 6px;
  cursor: pointer;
}

.btn-delete:hover {
  background: #e60000;
}

.empty {
  margin-top: 20px;
  font-size: 16px;
  color: #777;
}
</style>
