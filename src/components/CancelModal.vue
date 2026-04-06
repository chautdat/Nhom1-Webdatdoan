<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h2>❌ Hủy đơn hàng</h2>
        <button class="close-btn" @click="$emit('close')">✕</button>
      </div>

      <div class="modal-body">
        <p>Bạn có chắc chắn muốn hủy đơn hàng <strong>{{ order.orderCode || order.orderNumber }}</strong>?</p>

        <div class="form-group">
          <label>Lý do hủy đơn: <span class="required">*</span></label>
          <select v-model="reason" required>
            <option value="">-- Chọn lý do --</option>
            <option value="Đổi ý không muốn mua nữa">Đổi ý không muốn mua nữa</option>
            <option value="Tìm được sản phẩm khác tốt hơn">Tìm được sản phẩm khác tốt hơn</option>
            <option value="Thời gian giao hàng quá lâu">Thời gian giao hàng quá lâu</option>
            <option value="Đặt nhầm sản phẩm">Đặt nhầm sản phẩm</option>
            <option value="Khác">Khác</option>
          </select>
        </div>

        <div class="form-group">
          <label>Ghi chú thêm:</label>
          <textarea v-model="additionalNotes" rows="3" placeholder="Nhập thông tin thêm (không bắt buộc)"></textarea>
        </div>
      </div>

      <div class="modal-footer">
        <button class="btn btn-outline" @click="$emit('close')">Đóng</button>
        <button class="btn btn-danger" @click="confirmCancel" :disabled="!reason || loading">
          <span v-if="loading">⏳ Đang xử lý...</span>
          <span v-else>Xác nhận hủy</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import api from "@/axios";
import storage from "@/utils/storage";

export default {
  name: "CancelModal",
  props: {
    order: { type: Object, required: true },
  },

  data() {
    return {
      reason: "",
      additionalNotes: "",
      loading: false,
    };
  },

  methods: {
    async confirmCancel() {
      if (!this.reason) {
        alert("Vui lòng chọn lý do hủy đơn");
        return;
      }

      try {
        this.loading = true;
        const token = storage.getToken();

        await api.post(
          `/orders/${this.order.orderId}/cancel`,
          { reason: this.reason, additionalNotes: this.additionalNotes },
          { headers: token ? { Authorization: `Bearer ${token}` } : {} }
        );

        alert("✅ Hủy đơn hàng thành công!");
        this.$emit("cancelled");
      } catch (err) {
        console.error("❌ Error cancelling order:", err);
        alert(err.response?.data?.message || "Không thể hủy đơn hàng!");
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped>
.modal-overlay { position: fixed; inset: 0; background: rgba(0, 0, 0, 0.5); display: flex; justify-content: center; align-items: center; z-index: 10000; }
.modal-content { background: white; border-radius: 16px; max-width: 500px; width: 90%; max-height: 90vh; overflow-y: auto; box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3); }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 1.5rem; border-bottom: 1px solid #e5e7eb; }
.modal-header h2 { margin: 0; font-size: 1.25rem; }
.close-btn { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #6b7280; }
.close-btn:hover { color: #111827; }
.modal-body { padding: 1.5rem; }
.modal-body p { margin-bottom: 1.5rem; color: #374151; }
.form-group { margin-bottom: 1rem; }
.form-group label { display: block; margin-bottom: 0.5rem; font-weight: 600; color: #374151; }
.required { color: #ef4444; }
.form-group select, .form-group textarea { width: 100%; padding: 0.75rem; border: 2px solid #e5e7eb; border-radius: 8px; font-size: 0.9375rem; transition: border-color 0.2s; }
.form-group select:focus, .form-group textarea:focus { outline: none; border-color: #00b067; }
.modal-footer { display: flex; justify-content: flex-end; gap: 0.75rem; padding: 1.5rem; border-top: 1px solid #e5e7eb; }
.btn { padding: 0.75rem 1.5rem; border-radius: 8px; border: none; cursor: pointer; font-weight: 600; transition: all 0.2s; }
.btn-outline { background: white; border: 2px solid #e5e7eb; color: #374151; }
.btn-outline:hover { border-color: #00b067; }
.btn-danger { background: #ef4444; color: white; }
.btn-danger:hover:not(:disabled) { background: #dc2626; }
.btn-danger:disabled { opacity: 0.5; cursor: not-allowed; }
</style>
