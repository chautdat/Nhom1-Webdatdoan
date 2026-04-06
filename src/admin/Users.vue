<template>
  <div class="users-page">
    <!-- Error Banner -->
    <div v-if="errorMsg" class="error-banner">
      <strong>⚠️ Lỗi:</strong> {{ errorMsg }}
      <button @click="errorMsg = ''" class="close-error">✕</button>
    </div>

    <!-- Success Banner -->
    <div v-if="successMsg" class="success-banner">
      <strong>✅ Thành công:</strong> {{ successMsg }}
      <button @click="successMsg = ''" class="close-success">✕</button>
    </div>

    <!-- Header -->
    <div class="header-section">
      <div class="header-content">
        <h2 class="page-title">👥 Users Management</h2>
        <p class="page-subtitle">Manage all registered users</p>
      </div>
      <div class="header-actions">
        <div class="search-box">
          <i class="fas fa-search"></i>
          <input
            type="text"
            v-model="searchQuery"
            placeholder="Search users..."
            @input="handleSearch"
          />
        </div>
        <select
          v-model="roleFilter"
          @change="applyFilters"
          class="filter-select"
        >
          <option value="">All Roles</option>
          <option value="ROLE_CUSTOMER">Customers</option>
          <option value="ROLE_ADMIN">Admins</option>
        </select>
        <select
          v-model="statusFilter"
          @change="applyFilters"
          class="filter-select"
        >
          <option value="">All Status</option>
          <option value="active">Active</option>
          <option value="inactive">Inactive</option>
          <option value="banned">Banned</option>
        </select>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon total">
          <i class="fas fa-users"></i>
        </div>
        <div class="stat-info">
          <span class="stat-number">{{ totalUsers }}</span>
          <span class="stat-label">Total Users</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon active">
          <i class="fas fa-user-check"></i>
        </div>
        <div class="stat-info">
          <span class="stat-number">{{ activeUsers }}</span>
          <span class="stat-label">Active</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon customers">
          <i class="fas fa-user-friends"></i>
        </div>
        <div class="stat-info">
          <span class="stat-number">{{ customerCount }}</span>
          <span class="stat-label">Customers</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon admins">
          <i class="fas fa-user-shield"></i>
        </div>
        <div class="stat-info">
          <span class="stat-number">{{ adminCount }}</span>
          <span class="stat-label">Admins</span>
        </div>
      </div>
    </div>

    <!-- Users Table -->
    <div class="table-section">
      <div class="table-container">
        <table class="users-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>User</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Role</th>
              <th>Status</th>
              <th>Last Login</th>
              <th>Created</th>
              <th>Actions</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="loading">
              <td colspan="9" style="text-align: center; padding: 40px">
                <i class="fas fa-spinner fa-spin" style="font-size: 32px"></i>
                <p style="margin-top: 16px">Đang tải dữ liệu...</p>
              </td>
            </tr>

            <tr v-else-if="!filteredUsers.length">
              <td colspan="9" style="text-align: center; padding: 40px">
                <i
                  class="fas fa-user-slash"
                  style="font-size: 48px; color: #cbd5e1"
                ></i>
                <p style="margin-top: 16px; color: #64748b">
                  Không tìm thấy user nào
                </p>
              </td>
            </tr>

            <tr
              v-else
              v-for="user in filteredUsers"
              :key="user.userId"
              class="table-row"
            >
              <td>
                <span class="user-id">#{{ user.userId }}</span>
              </td>
              <td>
                <div class="user-info">
                  <div class="user-avatar">
                    <img
                      v-if="user.avatarUrl"
                      :src="user.avatarUrl"
                      :alt="user.fullName"
                    />
                    <span v-else>{{ getInitials(user.fullName) }}</span>
                  </div>
                  <div class="user-details">
                    <div class="user-name">
                      {{ user.fullName || user.username }}
                    </div>
                    <div class="user-username">@{{ user.username }}</div>
                  </div>
                </div>
              </td>
              <td>{{ user.email }}</td>
              <td>{{ user.phone || "N/A" }}</td>
              <td>
                <span class="badge" :class="getRoleBadgeClass(user.role)">
                  {{ getRoleLabel(user.role) }}
                </span>
              </td>
              <td>
                <span class="badge" :class="getStatusBadgeClass(user.status)">
                  {{ getStatusLabel(user.status) }}
                </span>
              </td>
              <td>{{ formatDate(user.lastLoginAt) }}</td>
              <td>{{ formatDate(user.createdAt) }}</td>
              <td>
                <div class="action-buttons">
                  <button
                    class="btn-action btn-view"
                    @click="viewUser(user)"
                    title="View Details"
                  >
                    <i class="fas fa-eye"></i>
                  </button>
                  <button
                    class="btn-action btn-edit"
                    @click="editUser(user)"
                    title="Edit User"
                  >
                    <i class="fas fa-edit"></i>
                  </button>
                  <button
                    v-if="user.status !== 'banned'"
                    class="btn-action btn-ban"
                    @click="banUser(user)"
                    title="Ban User"
                  >
                    <i class="fas fa-ban"></i>
                  </button>
                  <button
                    v-else
                    class="btn-action btn-unban"
                    @click="unbanUser(user)"
                    title="Unban User"
                  >
                    <i class="fas fa-check-circle"></i>
                  </button>
                  <button
                    v-if="user.role !== 'ROLE_ADMIN'"
                    class="btn-action btn-delete"
                    @click="deleteUser(user)"
                    title="Delete User"
                  >
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="pagination">
        <button :disabled="page === 0" @click="changePage(page - 1)">
          « Prev
        </button>
        <span>Page {{ page + 1 }} / {{ totalPages }}</span>
        <button
          :disabled="page + 1 >= totalPages"
          @click="changePage(page + 1)"
        >
          Next »
        </button>
      </div>
    </div>

    <!-- View User Modal -->
    <div
      v-if="showViewModal && selectedUser"
      class="modal-overlay"
      @click.self="closeViewModal"
    >
      <div class="modal-content">
        <div class="modal-header">
          <h3>👤 User Details</h3>
          <button class="modal-close" @click="closeViewModal">✕</button>
        </div>

        <div class="modal-body">
          <div class="user-profile">
            <div class="profile-avatar">
              <img
                v-if="selectedUser.avatarUrl"
                :src="selectedUser.avatarUrl"
              />
              <span v-else>{{ getInitials(selectedUser.fullName) }}</span>
            </div>
            <h3>{{ selectedUser.fullName }}</h3>
            <p>@{{ selectedUser.username }}</p>
          </div>

          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">User ID:</span>
              <span class="detail-value">#{{ selectedUser.userId }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Email:</span>
              <span class="detail-value">{{ selectedUser.email }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Phone:</span>
              <span class="detail-value">{{
                selectedUser.phone || "N/A"
              }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Role:</span>
              <span class="badge" :class="getRoleBadgeClass(selectedUser.role)">
                {{ getRoleLabel(selectedUser.role) }}
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Status:</span>
              <span
                class="badge"
                :class="getStatusBadgeClass(selectedUser.status)"
              >
                {{ getStatusLabel(selectedUser.status) }}
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Last Login:</span>
              <span class="detail-value">{{
                formatDate(selectedUser.lastLoginAt)
              }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Created At:</span>
              <span class="detail-value">{{
                formatDate(selectedUser.createdAt)
              }}</span>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn btn-close" @click="closeViewModal">Close</button>
        </div>
      </div>
    </div>

    <!-- Edit User Modal -->
    <div
      v-if="showEditModal && selectedUser"
      class="modal-overlay"
      @click.self="closeEditModal"
    >
      <div class="modal-content">
        <div class="modal-header">
          <h3>✏️ Edit User</h3>
          <button class="modal-close" @click="closeEditModal">✕</button>
        </div>

        <div class="modal-body">
          <form @submit.prevent="saveUserChanges">
            <div class="form-group">
              <label>Full Name</label>
              <input
                v-model="editForm.fullName"
                type="text"
                class="form-input"
                placeholder="Enter full name"
                required
              />
            </div>

            <div class="form-group">
              <label>Email</label>
              <input
                v-model="editForm.email"
                type="email"
                class="form-input"
                placeholder="Enter email"
                disabled
              />
              <small class="form-hint">Email cannot be changed</small>
            </div>

            <div class="form-group">
              <label>Phone</label>
              <input
                v-model="editForm.phone"
                type="tel"
                class="form-input"
                placeholder="Enter phone number"
              />
            </div>

            <div class="form-group">
              <label>Status</label>
              <select v-model="editForm.status" class="form-input" required>
                <option value="active">Active</option>
                <option value="inactive">Inactive</option>
                <option value="banned">Banned</option>
              </select>
            </div>

            <div class="form-group">
              <label>Avatar URL</label>
              <input
                v-model="editForm.avatarUrl"
                type="text"
                class="form-input"
                placeholder="Enter avatar URL"
              />
            </div>
          </form>
        </div>

        <div class="modal-footer">
          <button type="button" @click="closeEditModal" class="btn btn-cancel">
            Cancel
          </button>
          <button type="button" @click="saveUserChanges" class="btn btn-save">
            💾 Save Changes
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import storage from "@/utils/storage";
import api from "@/axios";
import Swal from "sweetalert2";

export default {
  name: "UsersManagement",

  data() {
    return {
      users: [],
      filteredUsers: [],
      loading: false,
      errorMsg: "",
      successMsg: "",
      searchQuery: "",
      roleFilter: "",
      statusFilter: "",
      page: 0,
      size: 10,
      totalPages: 0,
      totalElements: 0,
      showViewModal: false,
      showEditModal: false,
      selectedUser: null,
      editForm: {
        userId: null,
        fullName: "",
        email: "",
        phone: "",
        status: "",
        avatarUrl: "",
      },
    };
  },

  computed: {
    totalUsers() {
      return this.users.length;
    },
    activeUsers() {
      return this.users.filter((u) => u.status === "active").length;
    },
    customerCount() {
      return this.users.filter((u) => u.role === "ROLE_CUSTOMER").length;
    },
    adminCount() {
      return this.users.filter((u) => u.role === "ROLE_ADMIN").length;
    },
  },

  mounted() {
    this.fetchUsers();
  },

  methods: {
    // ================== FETCH USERS ==================
    async fetchUsers() {
      try {
        this.loading = true;
        this.errorMsg = "";

        const token = storage.getToken();
        if (!token) {
          this.$router.push("/login");
          return;
        }

        console.log("📡 Fetching users...");

        const res = await api.get("/users", {
          headers: { Authorization: `Bearer ${token}` },
          params: { page: this.page, size: this.size },
        });

        console.log("✅ Users response:", res.data);

        if (res.data?.success && res.data.data) {
          if (Array.isArray(res.data.data)) {
            this.users = res.data.data;
          } else if (res.data.data.content) {
            this.users = res.data.data.content;
            this.totalPages = res.data.data.totalPages || 0;
            this.totalElements = res.data.data.totalElements || 0;
          }
        } else if (Array.isArray(res.data)) {
          this.users = res.data;
        } else if (res.data?.content) {
          this.users = res.data.content;
          this.totalPages = res.data.totalPages || 0;
        }

        this.filteredUsers = [...this.users];
        console.log(`✅ Loaded ${this.users.length} users`);
      } catch (error) {
        console.error("❌ Error fetching users:", error);
        this.errorMsg =
          error.response?.data?.message || "Không thể tải danh sách users";

        if (error.response?.status === 401 || error.response?.status === 403) {
          storage.clearAll();
          this.$router.push("/login");
        }
      } finally {
        this.loading = false;
      }
    },

    // ================== SEARCH & FILTER ==================
    handleSearch() {
      this.applyFilters();
    },

    applyFilters() {
      let filtered = [...this.users];

      if (this.searchQuery.trim()) {
        const query = this.searchQuery.toLowerCase();
        filtered = filtered.filter(
          (user) =>
            user.fullName?.toLowerCase().includes(query) ||
            user.username?.toLowerCase().includes(query) ||
            user.email?.toLowerCase().includes(query) ||
            user.phone?.includes(query)
        );
      }

      if (this.roleFilter) {
        filtered = filtered.filter((user) => user.role === this.roleFilter);
      }

      if (this.statusFilter) {
        filtered = filtered.filter((user) => user.status === this.statusFilter);
      }

      this.filteredUsers = filtered;
    },

    // ================== VIEW USER ==================
    viewUser(user) {
      this.selectedUser = user;
      this.showViewModal = true;
    },

    closeViewModal() {
      this.showViewModal = false;
      this.selectedUser = null;
    },

    // ================== EDIT USER ==================
    editUser(user) {
      this.editForm = {
        userId: user.userId,
        fullName: user.fullName || "",
        email: user.email || "",
        phone: user.phone || "",
        status: user.status || "active",
        avatarUrl: user.avatarUrl || "",
      };

      this.selectedUser = user;
      this.showEditModal = true;
    },

    closeEditModal() {
      this.showEditModal = false;
      this.selectedUser = null;
      this.editForm = {
        userId: null,
        fullName: "",
        email: "",
        phone: "",
        status: "",
        avatarUrl: "",
      };
    },

    async saveUserChanges() {
      try {
        const token = storage.getToken();

        console.log("💾 Saving user changes:", this.editForm);

        // Update profile (fullName, phone, avatarUrl)
        await api.put(
          `/users/profile/${this.editForm.userId}`,
          {
            fullName: this.editForm.fullName,
            phone: this.editForm.phone,
            avatarUrl: this.editForm.avatarUrl,
          },
          { headers: { Authorization: `Bearer ${token}` } }
        );

        // Update status if changed
        const originalUser = this.users.find(
          (u) => u.userId === this.editForm.userId
        );
        if (originalUser && originalUser.status !== this.editForm.status) {
          await api.patch(
            `/users/${this.editForm.userId}/status`,
            { status: this.editForm.status },
            { headers: { Authorization: `Bearer ${token}` } }
          );
        }

        // Success
        await Swal.fire({
          title: "Đã lưu!",
          text: "Thông tin user đã được cập nhật",
          icon: "success",
          confirmButtonColor: "#22c55e",
          timer: 2000,
        });

        this.successMsg = `✅ Đã cập nhật user ${this.editForm.fullName}`;

        this.closeEditModal();
        this.fetchUsers();

        setTimeout(() => {
          this.successMsg = "";
        }, 3000);
      } catch (error) {
        console.error("❌ Save user error:", error);

        const errorMessage =
          error.response?.data?.message ||
          error.response?.data?.error ||
          "Không thể cập nhật user";

        this.errorMsg = errorMessage;

        Swal.fire({
          title: "Lỗi!",
          text: errorMessage,
          icon: "error",
          confirmButtonColor: "#ef4444",
        });
      }
    },

    // ================== BAN/UNBAN USER ==================
    async banUser(user) {
      const result = await Swal.fire({
        title: "Ban người dùng?",
        text: `Bạn có chắc muốn ban user "${user.fullName}"?`,
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#ef4444",
        cancelButtonColor: "#6b7280",
        confirmButtonText: "Ban",
        cancelButtonText: "Hủy",
      });
      if (!result.isConfirmed) return;

      try {
        const token = storage.getToken();
        await api.patch(
          `/users/${user.userId}/status`,
          { status: "banned" },
          { headers: { Authorization: `Bearer ${token}` } }
        );

        this.successMsg = `✅ Đã ban user ${user.fullName}`;
        this.fetchUsers();

        setTimeout(() => {
          this.successMsg = "";
        }, 3000);
      } catch (error) {
        console.error("❌ Ban user error:", error);
        this.errorMsg = error.response?.data?.message || "Không thể ban user";
      }
    },

    async unbanUser(user) {
      const result = await Swal.fire({
        title: "Mở khóa người dùng?",
        text: `Bạn có chắc muốn unban user "${user.fullName}"?`,
        icon: "question",
        showCancelButton: true,
        confirmButtonColor: "#22c55e",
        cancelButtonColor: "#6b7280",
        confirmButtonText: "Unban",
        cancelButtonText: "Hủy",
      });
      if (!result.isConfirmed) return;

      try {
        const token = storage.getToken();
        await api.patch(
          `/users/${user.userId}/status`,
          { status: "active" },
          { headers: { Authorization: `Bearer ${token}` } }
        );

        this.successMsg = `✅ Đã unban user ${user.fullName}`;
        this.fetchUsers();

        setTimeout(() => {
          this.successMsg = "";
        }, 3000);
      } catch (error) {
        console.error("❌ Unban user error:", error);
        this.errorMsg = error.response?.data?.message || "Không thể unban user";
      }
    },

    // ================== DELETE USER ==================
    async deleteUser(user) {
      const result = await Swal.fire({
        title: "Xóa người dùng?",
        html: `
          <p>Bạn có chắc muốn xóa user <strong>"${user.fullName}"</strong>?</p>
          <p style="color: #ef4444; font-weight: 600; margin-top: 12px;">
            ⚠️ Thao tác này không thể hoàn tác!
          </p>
        `,
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#ef4444",
        cancelButtonColor: "#6b7280",
        confirmButtonText: "🗑️ Xóa",
        cancelButtonText: "Hủy",
      });

      if (!result.isConfirmed) return;

      try {
        const token = storage.getToken();

        console.log(`🗑️ Deleting user ${user.userId}...`);

        await api.delete(`/users/${user.userId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        await Swal.fire({
          title: "Đã xóa!",
          text: `User ${user.fullName} đã được xóa thành công`,
          icon: "success",
          confirmButtonColor: "#22c55e",
          timer: 2000,
        });

        this.successMsg = `✅ Đã xóa user ${user.fullName}`;
        this.fetchUsers();

        setTimeout(() => {
          this.successMsg = "";
        }, 3000);
      } catch (error) {
        console.error("❌ Delete user error:", error);

        const errorMessage =
          error.response?.data?.message ||
          error.response?.data?.error ||
          "Không thể xóa user";

        this.errorMsg = errorMessage;

        Swal.fire({
          title: "Lỗi!",
          text: errorMessage,
          icon: "error",
          confirmButtonColor: "#ef4444",
        });
      }
    },

    // ================== PAGINATION ==================
    changePage(newPage) {
      if (newPage < 0 || newPage >= this.totalPages) return;
      this.page = newPage;
      this.fetchUsers();
    },

    // ================== HELPERS ==================
    getInitials(name) {
      if (!name) return "?";
      return name
        .split(" ")
        .map((n) => n[0])
        .join("")
        .toUpperCase()
        .substring(0, 2);
    },

    getRoleLabel(role) {
      return role === "ROLE_ADMIN" ? "Admin" : "Customer";
    },

    getRoleBadgeClass(role) {
      return role === "ROLE_ADMIN" ? "badge-admin" : "badge-customer";
    },

    getStatusLabel(status) {
      const labels = {
        active: "Active",
        inactive: "Inactive",
        banned: "Banned",
      };
      return labels[status] || status;
    },

    getStatusBadgeClass(status) {
      return `badge-${status}`;
    },

    formatDate(date) {
      if (!date) return "N/A";
      return new Date(date).toLocaleString("vi-VN");
    },
  },
};
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.users-page {
  padding: 30px;
  background: linear-gradient(135deg, #e0f2fe 0%, #dbeafe 100%);
  min-height: 100vh;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}

/* Banners */
.error-banner,
.success-banner {
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  animation: slideDown 0.3s ease;
}

.error-banner {
  background: #fee;
  color: #c33;
  border: 2px solid #fcc;
}

.success-banner {
  background: #efe;
  color: #2a7;
  border: 2px solid #cfc;
}

.close-error,
.close-success {
  background: transparent;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: inherit;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Header */
.header-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 30px;
  flex-wrap: wrap;
  gap: 20px;
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  color: #1e3a8a;
  margin-bottom: 8px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.page-subtitle {
  font-size: 16px;
  color: #3b82f6;
  font-weight: 500;
}

.header-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 10px;
  background: white;
  padding: 10px 16px;
  border-radius: 10px;
  border: 2px solid #e0e7ff;
  min-width: 250px;
}

.search-box i {
  color: #6b7280;
}

.search-box input {
  border: none;
  outline: none;
  font-size: 14px;
  flex: 1;
}

.filter-select {
  padding: 10px 16px;
  border-radius: 10px;
  border: 2px solid #e0e7ff;
  background: white;
  font-size: 14px;
  cursor: pointer;
}

/* Stats Cards */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.active {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.stat-icon.customers {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.stat-icon.admins {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  display: block;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

/* Table */
.table-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.table-container {
  overflow-x: auto;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
}

.users-table thead {
  background: #f8fafc;
}

.users-table th {
  padding: 16px;
  text-align: left;
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-bottom: 2px solid #e2e8f0;
}

.users-table td {
  padding: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.users-table tbody tr:hover {
  background: #f8fafc;
}

.user-id {
  font-weight: 700;
  color: #3b82f6;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 700;
  font-size: 16px;
  flex-shrink: 0;
  overflow: hidden;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-name {
  font-weight: 600;
  color: #1e293b;
  font-size: 14px;
}

.user-username {
  font-size: 12px;
  color: #64748b;
}

.badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.badge-admin {
  background: #fef3c7;
  color: #92400e;
}

.badge-customer {
  background: #dbeafe;
  color: #1e40af;
}

.badge-active {
  background: #d1fae5;
  color: #065f46;
}

.badge-inactive {
  background: #f3f4f6;
  color: #6b7280;
}

.badge-banned {
  background: #fee2e2;
  color: #991b1b;
}

.action-buttons {
  display: flex;
  gap: 6px;
}

.btn-action {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.btn-view {
  background: #dbeafe;
  color: #1e40af;
}

.btn-edit {
  background: #fef3c7;
  color: #92400e;
}

.btn-ban {
  background: #fecaca;
  color: #991b1b;
}

.btn-unban {
  background: #d1fae5;
  color: #065f46;
}

.btn-delete {
  background: #fee2e2;
  color: #b91c1c;
}

.btn-action:hover {
  transform: scale(1.1);
  filter: brightness(0.95);
}

/* Pagination */
.pagination {
  padding: 20px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
}

.pagination button {
  padding: 8px 16px;
  border-radius: 8px;
  border: none;
  background: #3b82f6;
  color: white;
  cursor: pointer;
  font-weight: 600;
}

.pagination button:disabled {
  background: #cbd5e1;
  cursor: not-allowed;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 600px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    transform: translateY(50px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 2px solid #f0f0f0;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  border-radius: 16px 16px 0 0;
}

.modal-header h3 {
  font-size: 22px;
  font-weight: 700;
}

.modal-close {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  font-size: 24px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease;
}

.modal-close:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: rotate(90deg);
}

.modal-body {
  padding: 24px;
}

.user-profile {
  text-align: center;
  padding: 20px 0;
  border-bottom: 2px solid #f1f5f9;
  margin-bottom: 20px;
}

.profile-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 700;
  font-size: 32px;
  margin: 0 auto 16px;
  overflow: hidden;
}

.profile-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-profile h3 {
  font-size: 24px;
  margin-bottom: 4px;
}

.user-profile p {
  color: #64748b;
  font-size: 14px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
  text-transform: uppercase;
}

.detail-value {
  font-size: 14px;
  color: #1e293b;
  font-weight: 500;
}

/* Edit Form */
.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.2s ease;
}

.form-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-input:disabled {
  background: #f1f5f9;
  cursor: not-allowed;
  color: #94a3b8;
}

.form-hint {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  color: #64748b;
  font-style: italic;
}

.modal-footer {
  padding: 20px 24px;
  border-top: 2px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn {
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
}

.btn-close,
.btn-cancel {
  background: #f1f5f9;
  color: #475569;
}

.btn-close:hover,
.btn-cancel:hover {
  background: #e2e8f0;
}

.btn-save {
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  color: white;
}

.btn-save:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(34, 197, 94, 0.4);
}

/* Responsive */
@media (max-width: 768px) {
  .users-page {
    padding: 20px;
  }

  .page-title {
    font-size: 28px;
  }

  .header-section {
    flex-direction: column;
  }

  .stats-cards {
    grid-template-columns: 1fr;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
