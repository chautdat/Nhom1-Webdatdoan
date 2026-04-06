<template>
  <div class="category-management">
    <!-- Header Section -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1>
            <span class="icon">🏷️</span>
            Quản Lý Danh Mục
          </h1>
          <p class="subtitle">Tổ chức và quản lý danh mục sản phẩm</p>
        </div>

        <!-- Quick Stats -->
        <div class="quick-stats">
          <div class="stat-item blue">
            <div class="stat-icon">📁</div>
            <div class="stat-content">
              <div class="stat-value">{{ categories.length }}</div>
              <div class="stat-label">Tổng Danh Mục</div>
            </div>
          </div>

          <div class="stat-item green">
            <div class="stat-icon">🍽️</div>
            <div class="stat-content">
              <div class="stat-value">{{ totalProducts }}</div>
              <div class="stat-label">Tổng Món Ăn</div>
            </div>
          </div>

          <div class="stat-item purple">
            <div class="stat-icon">⭐</div>
            <div class="stat-content">
              <div class="stat-value">
                {{ mostPopularCategory?.categoryName || "N/A" }}
              </div>
              <div class="stat-label">Danh Mục Nhiều Nhất</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Alert Messages -->
    <transition name="slide-fade">
      <div v-if="errorMsg" class="alert alert-error">
        <span class="alert-icon">⚠️</span>
        <span class="alert-message">{{ errorMsg }}</span>
        <button class="alert-close" @click="errorMsg = ''">✕</button>
      </div>
    </transition>

    <transition name="slide-fade">
      <div v-if="successMsg" class="alert alert-success">
        <span class="alert-icon">✅</span>
        <span class="alert-message">{{ successMsg }}</span>
        <button class="alert-close" @click="successMsg = ''">✕</button>
      </div>
    </transition>

    <!-- Toolbar -->
    <div class="toolbar">
      <!-- Search -->
      <div class="search-box">
        <span class="search-icon">🔍</span>
        <input
          v-model="searchQuery"
          type="text"
          class="search-input"
          placeholder="Tìm kiếm danh mục..."
        />
        <button
          v-if="searchQuery"
          class="clear-search"
          @click="searchQuery = ''"
        >
          ✕
        </button>
      </div>

      <!-- Actions -->
      <div class="toolbar-actions">
        <!-- View Toggle -->
        <div class="view-toggle">
          <button
            @click="viewMode = 'cards'"
            :class="{ active: viewMode === 'cards' }"
            title="Card View"
          >
            ▦
          </button>
          <button
            @click="viewMode = 'table'"
            :class="{ active: viewMode === 'table' }"
            title="Table View"
          >
            ☰
          </button>
        </div>

        <button
          class="btn-refresh"
          @click="fetchCategories"
          :disabled="loading"
        >
          <span :class="{ rotating: loading }">🔄</span>
        </button>

        <button class="btn-add" @click="openAddModal">
          <span class="icon">➕</span>
          Thêm Danh Mục
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>Đang tải danh mục...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="filteredCategories.length === 0" class="empty-state">
      <div class="empty-icon">📁</div>
      <h3>
        {{ searchQuery ? "Không Tìm Thấy Danh Mục" : "Chưa Có Danh Mục" }}
      </h3>
      <p>
        {{
          searchQuery
            ? "Thử từ khóa khác"
            : "Tạo danh mục đầu tiên cho nhà hàng"
        }}
      </p>
      <button v-if="!searchQuery" class="btn-add" @click="openAddModal">
        <span class="icon">➕</span>
        Tạo Danh Mục Đầu Tiên
      </button>
    </div>

    <!-- Content Display -->
    <div v-else>
      <!-- Cards View -->
      <div v-if="viewMode === 'cards'" class="cards-container">
        <div class="categories-grid">
          <div
            v-for="category in filteredCategories"
            :key="category.categoryId"
            class="category-card"
          >
            <!-- Card Header -->
            <div class="card-header">
              <div
                class="category-icon"
                :style="{ background: getCategoryColor(category) }"
              >
                <span class="icon-text">{{ getCategoryIcon(category) }}</span>
              </div>
              <div class="card-actions">
                <button
                  class="action-btn edit"
                  @click="openEditModal(category)"
                  title="Sửa"
                >
                  ✏️
                </button>
                <button
                  class="action-btn delete"
                  @click="deleteCategory(category)"
                  title="Xóa"
                >
                  🗑️
                </button>
              </div>
            </div>

            <!-- Card Body -->
            <div class="card-body">
              <h3 class="category-name">{{ category.categoryName }}</h3>
              <p v-if="category.description" class="category-description">
                {{ truncateText(category.description, 80) }}
              </p>

              <!-- Meta Info -->
              <div class="category-meta">
                <div class="meta-item">
                  <span class="meta-icon">🍽️</span>
                  <span class="meta-value"
                    >{{ category.itemCount || 0 }} món</span
                  >
                </div>
                <div class="meta-item">
                  <span class="meta-icon">📊</span>
                  <span class="meta-value">{{ getPercentage(category) }}%</span>
                </div>
              </div>

              <!-- Progress Bar -->
              <div class="progress-bar">
                <div
                  class="progress-fill"
                  :style="{
                    width: getPercentage(category) + '%',
                    background: getCategoryColor(category),
                  }"
                ></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Table View -->
      <div v-else class="table-container">
        <table class="categories-table">
          <thead>
            <tr>
              <th class="sortable" @click="sortTable('categoryName')">
                <div class="th-content">
                  Tên Danh Mục
                  <span class="sort-icon">{{
                    getSortIcon("categoryName")
                  }}</span>
                </div>
              </th>
              <th>Mô Tả</th>
              <th class="sortable text-right" @click="sortTable('itemCount')">
                <div class="th-content">
                  Số Món
                  <span class="sort-icon">{{ getSortIcon("itemCount") }}</span>
                </div>
              </th>
              <th class="text-center">Phần Trăm</th>
              <th class="text-center">Thao Tác</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="category in filteredCategories"
              :key="category.categoryId"
              class="table-row"
            >
              <td>
                <div class="name-cell">
                  <div
                    class="table-icon"
                    :style="{ background: getCategoryColor(category) }"
                  >
                    {{ getCategoryIcon(category) }}
                  </div>
                  <span class="name-text">{{ category.categoryName }}</span>
                </div>
              </td>

              <td>
                <span class="description-text">
                  {{ truncateText(category.description, 60) || "—" }}
                </span>
              </td>

              <td class="text-right">
                <span class="count-badge">{{ category.itemCount || 0 }}</span>
              </td>

              <td class="text-center">
                <div class="percentage-cell">
                  <span class="percentage-value"
                    >{{ getPercentage(category) }}%</span
                  >
                  <div class="mini-progress">
                    <div
                      class="mini-progress-fill"
                      :style="{
                        width: getPercentage(category) + '%',
                        background: getCategoryColor(category),
                      }"
                    ></div>
                  </div>
                </div>
              </td>

              <td class="text-center">
                <div class="table-actions">
                  <button
                    class="action-btn edit"
                    @click="openEditModal(category)"
                    title="Sửa"
                  >
                    ✏️ Sửa
                  </button>
                  <button
                    class="action-btn delete"
                    @click="deleteCategory(category)"
                    title="Xóa"
                  >
                    🗑️ Xóa
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Add/Edit Modal -->
    <transition name="modal">
      <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
        <div class="modal-container">
          <div class="modal-header">
            <h2>
              <span class="icon">{{ isEditMode ? "✏️" : "➕" }}</span>
              {{ isEditMode ? "Chỉnh Sửa Danh Mục" : "Thêm Danh Mục Mới" }}
            </h2>
            <button class="modal-close" @click="closeModal">✕</button>
          </div>

          <div class="modal-body">
            <!-- Category Name -->
            <div class="form-group">
              <label class="form-label">
                <span class="label-text">Tên Danh Mục</span>
                <span class="required">*</span>
              </label>
              <input
                v-model="formData.categoryName"
                type="text"
                class="form-input"
                placeholder="Vd: Món Chính, Tráng Miệng, Đồ Uống..."
                maxlength="100"
                required
              />
            </div>

            <!-- Description -->
            <div class="form-group">
              <label class="form-label">
                <span class="label-text">Mô Tả</span>
                <span class="optional">(không bắt buộc)</span>
              </label>
              <textarea
                v-model="formData.description"
                class="form-textarea"
                rows="4"
                maxlength="500"
                placeholder="Mô tả ngắn gọn về danh mục này..."
              ></textarea>
              <div class="char-counter">
                {{ (formData.description || "").length }}/500
              </div>
            </div>

            <!-- Preview -->
            <div class="form-group">
              <label class="form-label">
                <span class="label-text">Xem Trước</span>
              </label>
              <div class="preview-card">
                <h4 class="preview-text">
                  {{ formData.categoryName || "Tên danh mục" }}
                </h4>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button class="btn btn-secondary" @click="closeModal">
              <span class="btn-icon">🚫</span>
              <span class="btn-text">Hủy</span>
            </button>
            <button
              class="btn btn-primary"
              @click="saveCategory"
              :disabled="!formData.categoryName || saving"
            >
              <span class="btn-icon">{{ isEditMode ? "💾" : "➕" }}</span>
              <span class="btn-text">
                {{
                  saving ? "Đang lưu..." : isEditMode ? "Cập Nhật" : "Tạo Mới"
                }}
              </span>
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import api from "@/axios";
import storage from "@/utils/storage";
import Swal from "sweetalert2";

export default {
  name: "CategoryManagement",

  data() {
    return {
      categories: [],
      loading: false,
      saving: false,
      errorMsg: "",
      successMsg: "",

      // View & Search
      viewMode: "cards", // 'cards' or 'table'
      searchQuery: "",

      // Sorting
      sortField: null,
      sortDirection: "asc",

      // Modal
      showModal: false,
      isEditMode: false,
      formData: {
        categoryId: null,
        categoryName: "",
        description: "",
      },
    };
  },

  computed: {
    totalProducts() {
      return this.categories.reduce(
        (sum, cat) => sum + (cat.itemCount || 0),
        0
      );
    },

    mostPopularCategory() {
      if (!this.categories.length) return null;
      return [...this.categories].sort(
        (a, b) => (b.itemCount || 0) - (a.itemCount || 0)
      )[0];
    },

    filteredCategories() {
      let filtered = [...this.categories];

      // Search
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase();
        filtered = filtered.filter(
          (cat) =>
            (cat.categoryName || "").toLowerCase().includes(query) ||
            (cat.description || "").toLowerCase().includes(query)
        );
      }

      // Sort
      if (this.sortField) {
        filtered.sort((a, b) => {
          let aVal = a[this.sortField] || 0;
          let bVal = b[this.sortField] || 0;

          if (typeof aVal === "string") {
            aVal = aVal.toLowerCase();
            bVal = bVal.toLowerCase();
          }

          const comparison = aVal > bVal ? 1 : aVal < bVal ? -1 : 0;
          return this.sortDirection === "asc" ? comparison : -comparison;
        });
      }

      return filtered;
    },
  },

  mounted() {
    this.fetchCategories();
  },

  methods: {
    async fetchCategories() {
      try {
        this.loading = true;
        this.errorMsg = "";

        const token = storage.getToken();
        if (!token) {
          this.$router.push("/login");
          return;
        }

        const res = await api.get("/categories", {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (res.data?.success && res.data.data) {
          this.categories = res.data.data;
        } else if (Array.isArray(res.data)) {
          this.categories = res.data;
        } else if (res.data?.data && Array.isArray(res.data.data)) {
          this.categories = res.data.data;
        } else {
          this.categories = [];
        }
      } catch (error) {
        console.error("Error fetching categories:", error);
        this.errorMsg =
          error.response?.data?.message || "Không thể tải danh sách danh mục";

        if (error.response?.status === 401 || error.response?.status === 403) {
          storage.clearAuth();
          this.$router.push("/login");
        }
      } finally {
        this.loading = false;
      }
    },

    openAddModal() {
      this.isEditMode = false;
      this.formData = {
        categoryId: null,
        categoryName: "",
        description: "",
      };
      this.showModal = true;
    },

    openEditModal(category) {
      this.isEditMode = true;
      this.formData = {
        categoryId: category.categoryId,
        categoryName: category.categoryName,
        description: category.description || "",
      };
      this.showModal = true;
    },

    closeModal() {
      this.showModal = false;
      this.formData = {
        categoryId: null,
        categoryName: "",
        description: "",
      };
    },

    async saveCategory() {
      if (!this.formData.categoryName.trim()) {
        this.errorMsg = "Tên danh mục không được để trống!";
        return;
      }

      try {
        this.saving = true;

        const token = storage.getToken();
        if (!token) {
          this.$router.push("/login");
          return;
        }

        const payload = {
          categoryName: this.formData.categoryName.trim(),
          description: this.formData.description.trim() || null,
        };

        if (this.isEditMode) {
          await api.put(`/categories/${this.formData.categoryId}`, payload, {
            headers: { Authorization: `Bearer ${token}` },
          });
          this.successMsg = "Cập nhật danh mục thành công!";
        } else {
          await api.post("/categories", payload, {
            headers: { Authorization: `Bearer ${token}` },
          });
          this.successMsg = "Tạo danh mục thành công!";
        }

        this.closeModal();
        this.fetchCategories();

        setTimeout(() => {
          this.successMsg = "";
        }, 3000);
      } catch (error) {
        console.error("Error saving category:", error);
        this.errorMsg =
          error.response?.data?.message || "Không thể lưu danh mục";
      } finally {
        this.saving = false;
      }
    },

    async deleteCategory(category) {
      const result = await Swal.fire({
        title: "Xóa danh mục?",
        html: `Xác nhận xóa danh mục <b>"${category.categoryName}"</b>?<br/><small>Lưu ý: Các món trong danh mục này sẽ không bị xóa.</small>`,
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#ef4444",
        cancelButtonColor: "#6b7280",
        confirmButtonText: "Xóa",
        cancelButtonText: "Hủy",
      });

      if (!result.isConfirmed) return;

      try {
        const token = storage.getToken();
        if (!token) {
          this.$router.push("/login");
          return;
        }

        await api.delete(`/categories/${category.categoryId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        this.successMsg = "Xóa danh mục thành công!";
        this.fetchCategories();

        setTimeout(() => {
          this.successMsg = "";
        }, 3000);
      } catch (error) {
        console.error("Error deleting category:", error);
        this.errorMsg =
          error.response?.data?.message || "Không thể xóa danh mục";
      }
    },

    sortTable(field) {
      if (this.sortField === field) {
        this.sortDirection = this.sortDirection === "asc" ? "desc" : "asc";
      } else {
        this.sortField = field;
        this.sortDirection = "asc";
      }
    },

    getSortIcon(field) {
      if (this.sortField !== field) return "⇅";
      return this.sortDirection === "asc" ? "↑" : "↓";
    },

    getCategoryIcon(category) {
      const name = (category.categoryName || "").toLowerCase();

      if (name.includes("món chính") || name.includes("main")) return "🍖";
      if (name.includes("món phụ") || name.includes("side")) return "🥗";
      if (name.includes("tráng miệng") || name.includes("dessert")) return "🍰";
      if (
        name.includes("đồ uống") ||
        name.includes("drink") ||
        name.includes("nước")
      )
        return "🥤";
      if (name.includes("món chay") || name.includes("vegetarian")) return "🥬";
      if (name.includes("topping")) return "🧀";
      if (name.includes("khai vị") || name.includes("appetizer")) return "🥟";
      if (name.includes("súp") || name.includes("soup")) return "🍜";
      return "🍽️";
    },

    getCategoryColor(category) {
      const name = (category.categoryName || "").toLowerCase();

      if (name.includes("món chính") || name.includes("main"))
        return "linear-gradient(135deg, #f59e0b 0%, #d97706 100%)";
      if (name.includes("món phụ") || name.includes("side"))
        return "linear-gradient(135deg, #10b981 0%, #059669 100%)";
      if (name.includes("tráng miệng") || name.includes("dessert"))
        return "linear-gradient(135deg, #ec4899 0%, #db2777 100%)";
      if (name.includes("đồ uống") || name.includes("drink"))
        return "linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)";
      if (name.includes("món chay") || name.includes("vegetarian"))
        return "linear-gradient(135deg, #22c55e 0%, #16a34a 100%)";
      if (name.includes("topping"))
        return "linear-gradient(135deg, #a855f7 0%, #9333ea 100%)";
      if (name.includes("khai vị"))
        return "linear-gradient(135deg, #14b8a6 0%, #0d9488 100%)";
      if (name.includes("súp"))
        return "linear-gradient(135deg, #f97316 0%, #ea580c 100%)";

      return "linear-gradient(135deg, #667eea 0%, #764ba2 100%)";
    },

    getPercentage(category) {
      if (!this.totalProducts) return 0;
      return Math.round(((category.itemCount || 0) / this.totalProducts) * 100);
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

.category-management {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  padding: 30px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    "Helvetica Neue", Arial, sans-serif;
}

/* ============ HEADER ============ */
.page-header {
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 30px;
  overflow: hidden;
}

.header-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 30px;
  color: white;
}

.title-section h1 {
  margin: 0 0 8px 0;
  font-size: 32px;
  font-weight: 800;
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-section h1 .icon {
  font-size: 36px;
}

.subtitle {
  margin: 0;
  opacity: 0.95;
  font-size: 15px;
  font-weight: 500;
}

.quick-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
  margin-top: 24px;
}

.stat-item {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s;
  border: 2px solid rgba(255, 255, 255, 0.2);
}

.stat-item:hover {
  transform: translateY(-4px);
  background: rgba(255, 255, 255, 0.25);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  font-size: 36px;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-value {
  display: block;
  font-size: 24px;
  font-weight: 800;
  line-height: 1;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.stat-label {
  display: block;
  font-size: 13px;
  opacity: 0.9;
  font-weight: 500;
}

/* ============ ALERTS ============ */
.alert {
  padding: 16px 24px;
  border-radius: 12px;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.alert-error {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  color: #991b1b;
  border-left: 4px solid #dc2626;
}

.alert-success {
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
  color: #065f46;
  border-left: 4px solid #10b981;
}

.alert-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.alert-message {
  flex: 1;
  font-weight: 600;
}

.alert-close {
  background: transparent;
  border: 2px solid currentColor;
  color: inherit;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 18px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.alert-close:hover {
  transform: scale(1.1);
  background: rgba(0, 0, 0, 0.05);
}

.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s ease;
}

.slide-fade-enter-from {
  transform: translateY(-20px);
  opacity: 0;
}

.slide-fade-leave-to {
  transform: translateY(-20px);
  opacity: 0;
}

/* ============ TOOLBAR ============ */
.toolbar {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  align-items: center;
}

.search-box {
  position: relative;
  flex: 1;
  min-width: 250px;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 16px;
  font-size: 18px;
}

.search-input {
  width: 100%;
  padding: 12px 16px 12px 48px;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  transition: all 0.3s;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.clear-search {
  position: absolute;
  right: 12px;
  background: #e2e8f0;
  border: none;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.clear-search:hover {
  background: #cbd5e1;
}

.toolbar-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.view-toggle {
  display: flex;
  background: #f1f5f9;
  border-radius: 10px;
  padding: 4px;
  gap: 4px;
}

.view-toggle button {
  padding: 10px 16px;
  border: none;
  background: transparent;
  border-radius: 8px;
  cursor: pointer;
  font-size: 18px;
  transition: all 0.3s;
  color: #64748b;
}

.view-toggle button.active {
  background: white;
  color: #667eea;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.btn-refresh {
  padding: 12px 16px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-refresh:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-refresh:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.rotating {
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.btn-add {
  padding: 12px 24px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.btn-add:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.5);
}

/* ============ LOADING & EMPTY ============ */
.loading-container,
.empty-state {
  background: white;
  border-radius: 16px;
  padding: 80px 40px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #e2e8f0;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-container p {
  color: #64748b;
  font-size: 16px;
  font-weight: 600;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state h3 {
  margin: 0 0 8px 0;
  color: #1e293b;
  font-size: 20px;
}

.empty-state p {
  margin: 0 0 24px 0;
  color: #64748b;
  font-size: 15px;
}

/* ============ CARDS VIEW ============ */
.cards-container {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.category-card {
  background: white;
  border: 2px solid #f1f5f9;
  border-radius: 16px;
  padding: 20px;
  transition: all 0.3s;
  cursor: pointer;
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border-color: #e2e8f0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.category-icon {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.card-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 8px 12px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s;
}

.action-btn.edit {
  background: #dbeafe;
}

.action-btn.edit:hover {
  background: #bfdbfe;
  transform: scale(1.05);
}

.action-btn.delete {
  background: #fee2e2;
}

.action-btn.delete:hover {
  background: #fecaca;
  transform: scale(1.05);
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.category-name {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
}

.category-description {
  margin: 0;
  font-size: 14px;
  color: #64748b;
  line-height: 1.5;
}

.category-meta {
  display: flex;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #475569;
}

.meta-icon {
  font-size: 16px;
}

.progress-bar {
  height: 8px;
  background: #f1f5f9;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

/* ============ TABLE VIEW ============ */
.table-container {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.categories-table {
  width: 100%;
  border-collapse: collapse;
}

.categories-table thead {
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
}

.categories-table th {
  padding: 16px 20px;
  text-align: left;
  font-size: 12px;
  font-weight: 700;
  color: #475569;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-bottom: 2px solid #e2e8f0;
  white-space: nowrap;
}

.categories-table th.sortable {
  cursor: pointer;
  user-select: none;
  transition: background 0.2s;
}

.categories-table th.sortable:hover {
  background: #e2e8f0;
}

.th-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-icon {
  color: #94a3b8;
  font-size: 12px;
}

.text-right {
  text-align: right;
}

.text-center {
  text-align: center;
}

.categories-table td {
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
}

.table-row {
  transition: background 0.2s;
}

.table-row:hover {
  background: #f8fafc;
}

.name-cell {
  display: flex;
  align-items: center;
  gap: 16px;
}

.table-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.name-text {
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
}

.description-text {
  font-size: 14px;
  color: #64748b;
}

.count-badge {
  background: #dbeafe;
  color: #1e40af;
  padding: 6px 14px;
  border-radius: 12px;
  font-weight: 700;
  font-size: 14px;
}

.percentage-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.percentage-value {
  font-weight: 700;
  color: #667eea;
  font-size: 14px;
}

.mini-progress {
  width: 80px;
  height: 6px;
  background: #f1f5f9;
  border-radius: 3px;
  overflow: hidden;
}

.mini-progress-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.6s ease;
}

.table-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.table-actions .action-btn {
  padding: 8px 16px;
  font-size: 12px;
  font-weight: 700;
  white-space: nowrap;
}

/* ============ MODAL ============ */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-container {
  background: white;
  border-radius: 20px;
  width: 100%;
  max-width: 600px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 24px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 10px;
}

.modal-close {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  font-size: 24px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-close:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: rotate(90deg);
}

.modal-body {
  padding: 30px;
  overflow-y: auto;
  flex: 1;
}

.form-group {
  margin-bottom: 24px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
}

.label-text {
  font-size: 14px;
  font-weight: 700;
  color: #374151;
}

.required {
  color: #ef4444;
  font-weight: 700;
}

.optional {
  font-size: 12px;
  color: #9ca3af;
  font-weight: 500;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  font-family: inherit;
  transition: all 0.3s;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 100px;
}

.char-counter {
  text-align: right;
  font-size: 11px;
  color: #9ca3af;
  margin-top: 4px;
}

.preview-card {
  padding: 20px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 12px;
  border: 2px dashed #cbd5e1;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80px;
}

.preview-text {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  text-align: center;
}

.modal-footer {
  padding: 20px 30px;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f1f5f9;
  color: #475569;
  border: 2px solid #e2e8f0;
}

.btn-secondary:hover {
  background: #e2e8f0;
}

.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-container,
.modal-leave-to .modal-container {
  transform: scale(0.9) translateY(20px);
}

/* ============ RESPONSIVE ============ */
@media (max-width: 768px) {
  .category-management {
    padding: 16px;
  }

  .title-section h1 {
    font-size: 24px;
  }

  .quick-stats {
    grid-template-columns: 1fr;
  }

  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-box {
    width: 100%;
  }

  .toolbar-actions {
    flex-direction: column;
  }

  .btn-add {
    width: 100%;
    justify-content: center;
  }

  .categories-grid {
    grid-template-columns: 1fr;
  }

  .table-container {
    overflow-x: auto;
  }

  .categories-table {
    min-width: 700px;
  }
}

@media (max-width: 480px) {
  .stat-value {
    font-size: 20px;
  }
}
</style>
