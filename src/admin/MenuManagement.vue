<template>
  <div class="menu-admin">
    <!-- Header -->
    <header class="header">
      <div class="header-left">
        <h1 class="title">🍽️ Menu Management</h1>
        <p class="subtitle">Quản lý thực đơn nhà hàng của bạn</p>
      </div>

      <div class="stats">
        <div class="stat-box">
          <div class="stat-icon">📊</div>
          <div class="stat-info">
            <p class="stat-number">{{ products.length }}</p>
            <span class="stat-label">Tổng món</span>
          </div>
        </div>

        <div class="stat-box pink">
          <div class="stat-icon">📁</div>
          <div class="stat-info">
            <p class="stat-number">{{ categories.length }}</p>
            <span class="stat-label">Danh mục</span>
          </div>
        </div>
      </div>
    </header>

    <!-- Error Banner -->
    <div v-if="errorMsg" class="error-banner">⚠️ {{ errorMsg }}</div>

    <!-- Main Content -->
    <div class="main-grid">
      <!-- FORM LEFT -->
      <div class="form-card">
        <div class="card-header">
          <h2 class="card-title">
            {{ editing ? "✏️ Chỉnh sửa món ăn" : "➕ Thêm món ăn mới" }}
          </h2>
        </div>

        <form class="form" @submit.prevent="saveProduct">
          <!-- Product Name -->
          <div class="form-group">
            <label class="form-label">
              Tên món ăn <span class="required">*</span>
            </label>
            <input
              class="form-input"
              v-model="product.productName"
              required
              placeholder="Vd: Phở Bò, Cơm Gà Xối Mỡ..."
            />
          </div>

          <!-- Price & Category Row -->
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">
                Giá tiền (VNĐ) <span class="required">*</span>
              </label>
              <input
                class="form-input"
                type="number"
                v-model="product.price"
                required
                min="0"
                step="1000"
                placeholder="50000"
              />
            </div>

            <div class="form-group">
              <label class="form-label">
                Danh mục <span class="required">*</span>
              </label>
              <select
                class="form-select"
                v-model.number="product.categoryId"
                required
              >
                <option value="" disabled>-- Chọn danh mục --</option>
                <option
                  v-for="cat in categories"
                  :key="cat.categoryId"
                  :value="cat.categoryId"
                >
                  {{ cat.categoryName }}
                </option>
              </select>
            </div>
          </div>

          <!-- Short Description -->
          <div class="form-group">
            <label class="form-label">Mô tả ngắn</label>
            <textarea
              class="form-textarea short"
              v-model="product.shortDescription"
              placeholder="Mô tả ngắn gọn về món ăn (tối đa 200 ký tự)..."
              rows="2"
              maxlength="200"
            ></textarea>
            <div class="char-count">
              {{ (product.shortDescription || "").length }}/200
            </div>
          </div>

          <!-- Full Description -->
          <div class="form-group">
            <label class="form-label">Mô tả chi tiết</label>
            <textarea
              class="form-textarea"
              v-model="product.description"
              placeholder="Mô tả đầy đủ về nguyên liệu, hương vị, cách chế biến..."
              rows="3"
            ></textarea>
          </div>

          <!-- Image Upload -->
          <div class="form-group">
            <label class="form-label">Hình ảnh món ăn</label>

            <div v-if="previewUrl" class="preview-box">
              <img :src="previewUrl" alt="Preview" class="preview-img" />
              <button
                type="button"
                class="btn-remove"
                @click="removeImage"
                title="Xóa ảnh"
              >
                ✕
              </button>
            </div>

            <div v-else class="upload-box">
              <input
                ref="fileInput"
                type="file"
                accept="image/*"
                @change="handleFileChange"
                style="display: none"
              />
              <button
                type="button"
                class="btn-upload"
                @click="$refs.fileInput.click()"
              >
                📤 Tải lên hình ảnh
              </button>
              <p class="upload-hint">PNG, JPG, WEBP (Max 5MB)</p>
            </div>
          </div>

          <!-- Form Actions -->
          <div class="form-actions">
            <button type="submit" class="btn btn-primary" :disabled="loading">
              {{
                loading
                  ? "⏳ Đang lưu..."
                  : editing
                  ? "💾 Cập nhật"
                  : "➕ Thêm món"
              }}
            </button>
            <button
              v-if="editing"
              type="button"
              class="btn btn-secondary"
              @click="cancelEdit"
            >
              🚫 Hủy
            </button>
          </div>
        </form>
      </div>

      <!-- TABLE RIGHT -->
      <div class="table-card">
        <div class="card-header">
          <h2 class="card-title">📋 Danh sách món ăn</h2>
          <span class="count-badge">{{ filteredProducts.length }}</span>
        </div>

        <!-- Search -->
        <div class="search-box">
          <input
            type="text"
            class="search-input"
            v-model="searchQuery"
            placeholder="🔍 Tìm kiếm món ăn..."
          />
        </div>

        <!-- Data Table -->
        <div class="table-wrapper">
          <table class="data-table">
            <thead>
              <tr>
                <th class="col-image">Ảnh</th>
                <th class="col-name">Tên món</th>
                <th class="col-price">Giá</th>
                <th class="col-category">Danh mục</th>
                <th class="col-status">Trạng thái</th>
                <th class="col-actions">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="loading" class="loading-row">
                <td colspan="6">
                  <div class="loading-state">
                    <div class="spinner"></div>
                    <p>Đang tải dữ liệu...</p>
                  </div>
                </td>
              </tr>

              <tr v-else-if="!filteredProducts.length" class="empty-row">
                <td colspan="6">
                  <div class="empty-state">
                    <div class="empty-icon">🍽️</div>
                    <p class="empty-text">Không tìm thấy món ăn nào</p>
                  </div>
                </td>
              </tr>

              <tr
                v-else
                v-for="p in filteredProducts"
                :key="p.productId"
                class="data-row"
              >
                <td class="col-image">
                  <img
                    :src="getImageUrl(p.imageUrl)"
                    :alt="p.productName"
                    class="product-image"
                  />
                </td>

                <td class="col-name">
                  <div class="product-info">
                    <p class="product-name">{{ p.productName }}</p>
                    <p v-if="p.shortDescription" class="product-desc">
                      {{ truncateText(p.shortDescription, 45) }}
                    </p>
                  </div>
                </td>

                <td class="col-price">
                  <span class="product-price">{{ formatPrice(p.price) }}</span>
                </td>

                <td class="col-category">
                  <span class="category-badge" :class="getCategoryClass(p)">
                    {{ getCategoryName(p) }}
                  </span>
                </td>

                <td class="col-status">
                  <label class="toggle-switch">
                    <input
                      type="checkbox"
                      :checked="p.isAvailable !== false"
                      @change="toggleAvailability(p)"
                    />
                    <span class="toggle-slider"></span>
                    <span class="toggle-label">
                      {{ p.isAvailable !== false ? "Còn hàng" : "Hết hàng" }}
                    </span>
                  </label>
                </td>

                <td class="col-actions">
                  <button
                    class="btn-action btn-edit"
                    @click="editProduct(p)"
                    title="Sửa"
                  >
                    ✏️ Sửa
                  </button>
                  <button
                    class="btn-action btn-delete"
                    @click="deleteProduct(p.productId)"
                    title="Xóa"
                  >
                    🗑️ Xóa
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from "@/axios";
import Swal from "sweetalert2";

export default {
  name: "MenuManagement",

  data() {
    return {
      products: [],
      categories: [],
      searchQuery: "",
      loading: false,
      errorMsg: "",

      editing: false,
      product: {
        productId: null,
        productName: "",
        price: "",
        categoryId: "",
        imageUrl: "",
        description: "",
        shortDescription: "",
        isAvailable: true,
      },

      previewUrl: null,
      selectedFile: null,
    };
  },

  computed: {
    filteredProducts() {
      const q = this.searchQuery.toLowerCase().trim();
      if (!q) return this.products;

      return this.products.filter((p) => {
        const name = (p.productName || "").toLowerCase();
        const desc = (p.shortDescription || p.description || "").toLowerCase();
        const cat = this.getCategoryName(p).toLowerCase();
        return name.includes(q) || desc.includes(q) || cat.includes(q);
      });
    },
  },

  mounted() {
    this.loadData();
  },

  methods: {
    async loadData() {
      this.loading = true;
      this.errorMsg = "";
      try {
        await Promise.all([this.loadProducts(), this.loadCategories()]);
      } catch (e) {
        console.error("❌ Error loading data:", e);
        this.errorMsg = "Không thể tải dữ liệu!";
      } finally {
        this.loading = false;
      }
    },

    async loadProducts() {
      try {
        const res = await api.get("/products", {
          params: { includeUnavailable: true, page: 0, size: 200 },
        });
        this.products = res.data?.data?.content || res.data?.data || [];
        console.log("✅ Loaded products:", this.products.length);
      } catch (e) {
        console.error("❌ Error loading products:", e);
        throw e;
      }
    },

    async loadCategories() {
      try {
        const res = await api.get("/categories");
        this.categories = res.data?.data || [];
        console.log("✅ Loaded categories:", this.categories.length);
      } catch (e) {
        console.error("❌ Error loading categories:", e);
        throw e;
      }
    },

    handleFileChange(event) {
      const file = event.target.files?.[0];
      if (!file) return;

      if (file.size > 5 * 1024 * 1024) {
        alert("❌ File quá lớn! Vui lòng chọn ảnh < 5MB");
        return;
      }

      if (!file.type.startsWith("image/")) {
        alert("❌ Vui lòng chọn file ảnh!");
        return;
      }

      this.selectedFile = file;

      const reader = new FileReader();
      reader.onload = (e) => {
        this.previewUrl = e.target?.result || null;
      };
      reader.readAsDataURL(file);
    },

    removeImage() {
      this.previewUrl = null;
      this.selectedFile = null;
      this.product.imageUrl = "";
      if (this.$refs.fileInput) {
        this.$refs.fileInput.value = "";
      }
    },

    async saveProduct() {
      if (!this.product.productName?.trim()) {
        alert("⚠️ Vui lòng nhập tên món ăn!");
        return;
      }

      if (!this.product.price || this.product.price < 0) {
        alert("⚠️ Vui lòng nhập giá hợp lệ!");
        return;
      }

      if (!this.product.categoryId) {
        alert("⚠️ Vui lòng chọn danh mục!");
        return;
      }

      this.loading = true;
      this.errorMsg = "";

      try {
        let imageUrl = this.product.imageUrl;

        if (this.selectedFile) {
          const uploadRes = await this.uploadImage(this.selectedFile);
          imageUrl =
            uploadRes.path ||
            uploadRes.url ||
            uploadRes.filename ||
            uploadRes ||
            "";
        }

        const payload = {
          productName: this.product.productName.trim(),
          price: Number(this.product.price),
          categoryId: Number(this.product.categoryId),
          imageUrl: imageUrl || "",
          description: this.product.description?.trim() || "",
          shortDescription: this.product.shortDescription?.trim() || "",
          isAvailable: this.product.isAvailable !== false,
        };

        console.log("📤 Payload:", payload);

        if (this.editing && this.product.productId) {
          await api.put(`/products/${this.product.productId}`, payload);
          console.log("✅ Product updated");
          await Swal.fire({
            icon: "success",
            title: "Thành công!",
            text: "Món ăn đã được cập nhật",
            timer: 1500,
            showConfirmButton: false,
          });
        } else {
          await api.post("/products", payload);
          console.log("✅ Product created");
          await Swal.fire({
            icon: "success",
            title: "Thành công!",
            text: "Món ăn đã được thêm vào menu",
            timer: 1500,
            showConfirmButton: false,
          });
        }

        await this.loadProducts();
        this.resetForm();
      } catch (e) {
        console.error("❌ Error saving product:", e);
        this.errorMsg = `Lỗi: ${e.response?.data?.message || e.message}`;
        await Swal.fire({
          icon: "error",
          title: "Lỗi!",
          text: this.errorMsg,
        });
      } finally {
        this.loading = false;
      }
    },

    async uploadImage(file) {
      const formData = new FormData();
      formData.append("file", file);

      try {
        const res = await api.post("/upload", formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });
        console.log("✅ Image uploaded:", res.data);
        return res.data?.data || res.data || {};
      } catch (e) {
        console.error("❌ Error uploading image:", e);
        throw new Error("Không thể upload ảnh!");
      }
    },

    editProduct(product) {
      this.editing = true;

      this.product = {
        productId: product.productId,
        productName: product.productName || "",
        price: product.price || "",
        categoryId: product.categoryId || product.category?.categoryId || "",
        imageUrl: product.imageUrl || "",
        description: product.description || "",
        shortDescription: product.shortDescription || "",
        isAvailable: product.isAvailable !== false,
      };

      if (product.imageUrl) {
        this.previewUrl = this.getImageUrl(product.imageUrl);
      }

      window.scrollTo({ top: 0, behavior: "smooth" });
    },

    async deleteProduct(id) {
      const result = await Swal.fire({
        title: "Xóa món ăn?",
        text: "Thao tác này không thể hoàn tác.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#ef4444",
        cancelButtonColor: "#6b7280",
        confirmButtonText: "Xóa",
        cancelButtonText: "Hủy",
      });
      if (!result.isConfirmed) return;

      this.loading = true;
      try {
        await api.delete(`/products/${id}`);
        await this.loadProducts();
        await Swal.fire({
          icon: "success",
          title: "Đã xóa",
          text: "Món ăn đã được xóa.",
          timer: 1500,
          showConfirmButton: false,
        });
      } catch (e) {
        console.error("❌ Error deleting product:", e);
        await Swal.fire({
          icon: "error",
          title: "Lỗi",
          text:
            e.response?.data?.message || e.message || "Không thể xóa món ăn",
        });
      } finally {
        this.loading = false;
      }
    },

    async toggleAvailability(product) {
      const newStatus = !(product.isAvailable !== false);

      try {
        const payload = {
          productName: product.productName,
          price: Number(product.price),
          categoryId: Number(
            product.categoryId || product.category?.categoryId
          ),
          imageUrl: product.imageUrl || "",
          description: product.description || "",
          shortDescription: product.shortDescription || "",
          isAvailable: newStatus,
        };

        await api.put(`/products/${product.productId}`, payload);

        const idx = this.products.findIndex(
          (p) => p.productId === product.productId
        );
        if (idx !== -1) {
          this.products[idx].isAvailable = newStatus;
        }

        console.log(`✅ Toggled availability for ${product.productName}`);
      } catch (e) {
        console.error("❌ Error toggling availability:", e);
        await Swal.fire({
          icon: "error",
          title: "Không thể cập nhật trạng thái",
          text: e.response?.data?.message || e.message,
        });
      }
    },

    cancelEdit() {
      Swal.fire({
        title: "Hủy chỉnh sửa?",
        text: "Các thay đổi chưa lưu sẽ bị bỏ qua.",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Hủy",
        cancelButtonText: "Tiếp tục chỉnh sửa",
      }).then((res) => {
        if (res.isConfirmed) {
          this.resetForm();
        }
      });
    },

    resetForm() {
      this.editing = false;
      this.previewUrl = null;
      this.selectedFile = null;

      this.product = {
        productId: null,
        productName: "",
        price: "",
        categoryId: "",
        imageUrl: "",
        description: "",
        shortDescription: "",
        isAvailable: true,
      };

      if (this.$refs.fileInput) {
        this.$refs.fileInput.value = "";
      }
    },

    getImageUrl(url) {
      if (!url) return "/images/no-image.png";

      const backendUrl = "http://localhost:3000";

      if (url.startsWith("http")) return url;
      if (url.startsWith("/uploads")) return `${backendUrl}${url}`;
      return `${backendUrl}/uploads/${url}`;
    },

    getCategoryName(product) {
      if (product.category?.categoryName) return product.category.categoryName;

      const catId = product.categoryId || product.category?.categoryId;
      const found = this.categories.find((c) => c.categoryId === catId);
      if (found) return found.categoryName;

      const map = {
        1: "Món chính",
        2: "Món phụ",
        3: "Đồ uống",
        4: "Tráng miệng",
        5: "Topping",
      };
      return map[catId] || "Khác";
    },

    getCategoryClass(product) {
      const catId = product.categoryId || product.category?.categoryId;
      return `cat-${catId || 0}`;
    },

    formatPrice(value) {
      return Number(value).toLocaleString("vi-VN") + "đ";
    },

    truncateText(text, maxLength) {
      if (!text) return "";
      if (text.length <= maxLength) return text;
      return text.substring(0, maxLength) + "...";
    },
  },
};
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.menu-admin {
  padding: 1.8rem 4%;
  max-width: 1600px;
  margin: 0 auto;
  min-height: 100vh;
  background: linear-gradient(135deg, #f4f7fb 0%, #e1e9f7 100%);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    "Helvetica Neue", Arial, sans-serif;
  font-size: 1.2rem !important;
}

.error-banner {
  background: #fee2e2;
  color: #b91c1c;
  padding: 10px 16px;
  border: 1px solid #fecaca;
  border-radius: 10px;
  margin-bottom: 1.2rem;
  font-weight: 600;
  font-size: 0.95rem;
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
  font-size: 0.95rem;
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
  font-size: 2.1rem;
  font-weight: 800;
  color: #2563eb;
  margin: 0 0 0.4rem 0;
  letter-spacing: -0.5px;
}

.subtitle {
  font-size: 1rem;
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
  min-width: 140px;
  transition: all 0.3s;
  border: 1px solid rgba(59, 130, 246, 0.15);
}

.stat-box:hover {
  background: rgba(59, 130, 246, 0.12);
  transform: translateY(-2px);
  box-shadow: 0 3px 10px rgba(59, 130, 246, 0.15);
}

.stat-box.pink {
  background: rgba(14, 165, 233, 0.08);
  border: 1px solid rgba(14, 165, 233, 0.15);
}

.stat-icon {
  font-size: 2.3rem;
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
  font-size: 0.88rem;
  color: #64748b;
  font-weight: 600;
}

.main-grid {
  display: grid;
  grid-template-columns: 480px 1fr;
  gap: 2.4rem;
  align-items: start;
}

.form-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  position: sticky;
  top: 1.5rem;
}

.card-header {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  padding: 1.5rem 1.8rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 1.35rem;
  font-weight: 700;
  color: white;
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
  font-size: 0.85rem;
  font-weight: 700;
  color: white;
}

.form {
  padding: 1.7rem;
}

.form-group {
  margin-bottom: 1.4rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.2rem;
}

.form-label {
  display: block;
  font-size: 1.05rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 0.5rem;
}

.required {
  color: #ef4444;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 0.85rem 1rem;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  font-size: 1.05rem;
  font-family: inherit;
  transition: all 0.3s;
  background: #fafbfc;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: #3b82f6;
  background: white;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.form-textarea.short {
  min-height: 55px;
}

.char-count {
  text-align: right;
  font-size: 0.8rem;
  color: #9ca3af;
  margin-top: 0.3rem;
}

.preview-box {
  position: relative;
  width: 100%;
  height: 185px;
  border-radius: 12px;
  overflow: hidden;
  background: #f9fafb;
  border: 2px dashed #d1d5db;
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.btn-remove {
  position: absolute;
  top: 0.6rem;
  right: 0.6rem;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: rgba(239, 68, 68, 0.95);
  color: white;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-remove:hover {
  background: #dc2626;
  transform: scale(1.1);
}

.upload-box {
  text-align: center;
  padding: 2rem 1.2rem;
  border: 2px dashed #cbd5e1;
  border-radius: 12px;
  background: #f8fafc;
  transition: all 0.3s;
}

.upload-box:hover {
  border-color: #3b82f6;
  background: #eff6ff;
}

.btn-upload {
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.btn-upload:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

.upload-hint {
  margin: 0.8rem 0 0 0;
  font-size: 0.8rem;
  color: #9ca3af;
}

.form-actions {
  display: flex;
  gap: 0.9rem;
  margin-top: 1.9rem;
}

.btn {
  flex: 1;
  padding: 0.9rem 1.4rem;
  border: none;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.btn-primary {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.btn-secondary {
  background: #f3f4f6;
  color: #374151;
  border: 2px solid #e5e7eb;
}

.btn-secondary:hover {
  background: #e5e7eb;
}

.table-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.search-box {
  padding: 1.3rem 1.6rem;
  border-bottom: 1px solid #f3f4f6;
}

.search-input {
  width: 100%;
  padding: 0.85rem 1rem;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  font-size: 0.95rem;
  background: #fafbfc;
  transition: all 0.3s;
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
  background: white;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
}

.table-wrapper {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.92rem;
}

.data-table thead {
  background: #f9fafb;
  border-bottom: 2px solid #e5e7eb;
}

.data-table th {
  padding: 1rem 0.85rem;
  text-align: left;
  font-size: 0.85rem;
  font-weight: 700;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  white-space: nowrap;
}

.col-image {
  width: 75px;
}

.col-name {
  width: auto;
  min-width: 220px;
}

.col-price {
  width: 115px;
}

.col-category {
  width: 140px;
}

.col-status {
  width: 135px;
}

.col-actions {
  width: 195px;
}

.data-table tbody tr {
  border-bottom: 1px solid #f3f4f6;
  transition: background 0.2s;
}

.data-table tbody tr:hover {
  background: #f9fafb;
}

.data-table td {
  padding: 1rem 0.85rem;
  vertical-align: middle;
}

.product-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 10px;
  border: 2px solid #f3f4f6;
}

.product-info {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
}

.product-name {
  font-size: 0.98rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
  line-height: 1.3;
}

.product-desc {
  font-size: 0.82rem;
  color: #9ca3af;
  margin: 0;
  line-height: 1.4;
}

.product-price {
  font-size: 1.05rem;
  font-weight: 700;
  color: #10b981;
  white-space: nowrap;
}

.category-badge {
  display: inline-block;
  padding: 0.35rem 0.75rem;
  border-radius: 10px;
  font-size: 0.82rem;
  font-weight: 600;
  white-space: nowrap;
  text-align: center;
}

.category-badge.cat-1 {
  background: #dbeafe;
  color: #1e40af;
}
.category-badge.cat-2 {
  background: #d1fae5;
  color: #065f46;
}
.category-badge.cat-3 {
  background: #fef3c7;
  color: #92400e;
}
.category-badge.cat-4 {
  background: #fce7f3;
  color: #9f1239;
}
.category-badge.cat-5 {
  background: #e0e7ff;
  color: #3730a3;
}

.toggle-switch {
  display: inline-flex;
  align-items: center;
  gap: 0.6rem;
  cursor: pointer;
  user-select: none;
}

.toggle-switch input {
  display: none;
}

.toggle-slider {
  position: relative;
  width: 44px;
  height: 24px;
  background: #d1d5db;
  border-radius: 12px;
  transition: background 0.3s;
  flex-shrink: 0;
}

.toggle-slider::after {
  content: "";
  position: absolute;
  top: 3px;
  left: 3px;
  width: 18px;
  height: 18px;
  background: white;
  border-radius: 50%;
  transition: transform 0.3s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.toggle-switch input:checked + .toggle-slider {
  background: #10b981;
}

.toggle-switch input:checked + .toggle-slider::after {
  transform: translateX(20px);
}

.toggle-label {
  font-size: 0.88rem;
  font-weight: 600;
  color: #4b5563;
  white-space: nowrap;
}

.col-actions {
  display: flex;
  gap: 0.7rem;
}

.btn-action {
  padding: 0.55rem 0.95rem;
  border: none;
  border-radius: 8px;
  font-size: 0.88rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  white-space: nowrap;
}

.btn-edit {
  background: #dbeafe;
  color: #1e40af;
}

.btn-edit:hover {
  background: #bfdbfe;
  transform: translateY(-2px);
}

.btn-delete {
  background: #fee2e2;
  color: #b91c1c;
}

.btn-delete:hover {
  background: #fecaca;
  transform: translateY(-2px);
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
  font-size: 1rem;
  font-weight: 600;
  margin: 0;
}

@media (min-width: 1500px) {
  .main-grid {
    grid-template-columns: 480px 1fr;
    gap: 2.4rem;
  }
}

@media (max-width: 1500px) and (min-width: 1300px) {
  .main-grid {
    grid-template-columns: 460px 1fr;
    gap: 2.2rem;
  }

  .menu-admin {
    padding: 1.6rem 3.5%;
  }
}

@media (max-width: 1300px) and (min-width: 1100px) {
  .main-grid {
    grid-template-columns: 440px 1fr;
    gap: 2rem;
  }

  .title {
    font-size: 2rem;
  }

  .form {
    padding: 1.5rem;
  }
}

@media (max-width: 1100px) {
  .main-grid {
    grid-template-columns: 1fr;
    gap: 1.8rem;
  }

  .form-card {
    position: static;
  }

  .header {
    flex-direction: column;
    align-items: flex-start;
  }

  .stats {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .menu-admin {
    padding: 1.2rem 4%;
  }

  .title {
    font-size: 1.8rem;
  }

  .subtitle {
    font-size: 0.9rem;
  }

  .stat-box {
    padding: 1rem 1.2rem;
    min-width: 130px;
  }

  .form {
    padding: 1.3rem;
  }

  .form-row {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .table-wrapper {
    overflow-x: scroll;
  }

  .data-table {
    min-width: 800px;
  }
}

@media (max-width: 480px) {
  .title {
    font-size: 1.6rem;
  }

  .stats {
    flex-direction: column;
    width: 100%;
  }

  .stat-box {
    width: 100%;
  }
}
</style>
