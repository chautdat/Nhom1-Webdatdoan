<template>
  <div class="profile-page">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-content">
        <h1>
          <i class="fas fa-user-cog"></i>
          Thông Tin Tài Khoản
        </h1>
        <p>Quản lý thông tin cá nhân và cài đặt tài khoản</p>
      </div>
    </div>

    <div class="profile-container">
      <!-- Sidebar -->
      <div class="profile-sidebar">
        <div class="user-card">
          <div class="user-avatar">
            <i class="fas fa-user-circle"></i>
          </div>
          <h3>{{ user.user_name || "User" }}</h3>
          <p>{{ user.user_email || "email@example.com" }}</p>
        </div>

        <nav class="sidebar-menu">
          <button
            class="menu-item"
            :class="{ active: activeTab === 'info' }"
            @click="activeTab = 'info'"
          >
            <i class="fas fa-user"></i>
            <span>Thông tin cá nhân</span>
          </button>
          <button
            class="menu-item"
            :class="{ active: activeTab === 'password' }"
            @click="activeTab = 'password'"
          >
            <i class="fas fa-lock"></i>
            <span>Đổi mật khẩu</span>
          </button>
          <button
            class="menu-item"
            :class="{ active: activeTab === 'address' }"
            @click="activeTab = 'address'"
          >
            <i class="fas fa-map-marker-alt"></i>
            <span>Địa chỉ giao hàng</span>
          </button>
          <router-link to="/my-order" class="menu-item">
            <i class="fas fa-clipboard-list"></i>
            <span>Đơn hàng của tôi</span>
          </router-link>
        </nav>
      </div>

      <!-- Content -->
      <div class="profile-content">
        <!-- Personal Info Tab -->
        <div v-if="activeTab === 'info'" class="content-section">
          <div class="section-header">
            <h2>Thông Tin Cá Nhân</h2>
            <p>Cập nhật thông tin cá nhân của bạn</p>
          </div>

          <form @submit.prevent="updateProfile" class="profile-form">
            <div class="form-group">
              <label>
                <i class="fas fa-user"></i>
                Họ và tên
              </label>
              <input
                type="text"
                v-model="profileForm.name"
                placeholder="Nhập họ và tên"
                required
              />
            </div>

            <div class="form-group">
              <label>
                <i class="fas fa-envelope"></i>
                Email
              </label>
              <input
                type="email"
                v-model="profileForm.email"
                placeholder="Nhập email"
                required
              />
            </div>

            <div class="form-group">
              <label>
                <i class="fas fa-phone"></i>
                Số điện thoại
              </label>
              <input
                type="tel"
                v-model="profileForm.phone"
                placeholder="Nhập số điện thoại"
              />
            </div>

            <div class="form-actions">
              <button type="submit" class="btn-primary">
                <i class="fas fa-save"></i>
                Lưu thay đổi
              </button>
              <button
                type="button"
                class="btn-secondary"
                @click="resetProfileForm"
              >
                <i class="fas fa-undo"></i>
                Hủy bỏ
              </button>
            </div>
          </form>
        </div>

        <!-- Change Password Tab -->
        <div v-if="activeTab === 'password'" class="content-section">
          <div class="section-header">
            <h2>Đổi Mật Khẩu</h2>
            <p>Đảm bảo tài khoản của bạn luôn an toàn</p>
          </div>

          <form @submit.prevent="changePassword" class="profile-form">
            <div class="form-group">
              <label>
                <i class="fas fa-lock"></i>
                Mật khẩu hiện tại
              </label>
              <input
                :type="showOldPassword ? 'text' : 'password'"
                v-model="passwordForm.oldPassword"
                placeholder="Nhập mật khẩu hiện tại"
                required
              />
              <button
                type="button"
                class="toggle-password"
                @click="showOldPassword = !showOldPassword"
              >
                <i
                  :class="showOldPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"
                ></i>
              </button>
            </div>

            <div class="form-group">
              <label>
                <i class="fas fa-key"></i>
                Mật khẩu mới
              </label>
              <input
                :type="showNewPassword ? 'text' : 'password'"
                v-model="passwordForm.newPassword"
                placeholder="Nhập mật khẩu mới"
                required
              />
              <button
                type="button"
                class="toggle-password"
                @click="showNewPassword = !showNewPassword"
              >
                <i
                  :class="showNewPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"
                ></i>
              </button>
            </div>

            <div class="form-group">
              <label>
                <i class="fas fa-check-circle"></i>
                Xác nhận mật khẩu mới
              </label>
              <input
                :type="showConfirmPassword ? 'text' : 'password'"
                v-model="passwordForm.confirmPassword"
                placeholder="Nhập lại mật khẩu mới"
                required
              />
              <button
                type="button"
                class="toggle-password"
                @click="showConfirmPassword = !showConfirmPassword"
              >
                <i
                  :class="
                    showConfirmPassword ? 'fas fa-eye-slash' : 'fas fa-eye'
                  "
                ></i>
              </button>
            </div>

            <div class="password-requirements">
              <h4>Mật khẩu cần có:</h4>
              <ul>
                <li :class="{ valid: passwordForm.newPassword.length >= 8 }">
                  <i class="fas fa-check-circle"></i>
                  Ít nhất 8 ký tự
                </li>
                <li :class="{ valid: /[A-Z]/.test(passwordForm.newPassword) }">
                  <i class="fas fa-check-circle"></i>
                  Ít nhất 1 chữ hoa
                </li>
                <li :class="{ valid: /[0-9]/.test(passwordForm.newPassword) }">
                  <i class="fas fa-check-circle"></i>
                  Ít nhất 1 số
                </li>
              </ul>
            </div>

            <div class="form-actions">
              <button type="submit" class="btn-primary">
                <i class="fas fa-lock"></i>
                Đổi mật khẩu
              </button>
              <button
                type="button"
                class="btn-secondary"
                @click="resetPasswordForm"
              >
                <i class="fas fa-undo"></i>
                Hủy bỏ
              </button>
            </div>
          </form>
        </div>

        <!-- Address Tab -->
        <div v-if="activeTab === 'address'" class="content-section">
          <div class="section-header">
            <h2>Địa Chỉ Giao Hàng</h2>
            <p>Quản lý địa chỉ giao hàng của bạn</p>
            <button class="btn-add-address" @click="showAddressForm = true">
              <i class="fas fa-plus"></i>
              Thêm địa chỉ mới
            </button>
          </div>

          <!-- Address List -->
          <div class="address-list" v-if="addresses.length > 0">
          <div
            v-for="(address, index) in addresses"
            :key="address.id || index"
            class="address-card"
            :class="{ default: address.isDefault }"
          >
            <div class="address-header">
              <h3>{{ address.recipientName }}</h3>
              <span class="default-badge" v-if="address.isDefault"
                >Mặc định</span
              >
            </div>
            <p class="address-phone">
              <i class="fas fa-phone"></i>
              {{ address.phone }}
            </p>
            <p class="address-detail">
              <i class="fas fa-map-marker-alt"></i>
              {{ formatAddress(address) }}
            </p>
            <div class="address-actions">
              <button class="btn-icon" @click="editAddress(index)">
                <i class="fas fa-edit"></i>
                Sửa
              </button>
              <button class="btn-icon" @click="deleteAddress(index)">
                <i class="fas fa-trash"></i>
                Xóa
              </button>
              <button
                class="btn-icon"
                v-if="!address.isDefault"
                @click="setDefaultAddress(index)"
              >
                <i class="fas fa-star"></i>
                Đặt mặc định
              </button>
            </div>
          </div>
          </div>

          <!-- Empty State -->
          <div v-else class="empty-state">
            <i class="fas fa-map-marked-alt"></i>
            <h3>Chưa có địa chỉ nào</h3>
            <p>Thêm địa chỉ giao hàng để đặt hàng nhanh hơn</p>
          </div>

          <!-- Add/Edit Address Form -->
          <div
            class="address-form-overlay"
            v-if="showAddressForm"
            @click.self="showAddressForm = false"
          >
            <div class="address-form-modal">
              <div class="modal-header">
                <h3>
                  {{
                    editingAddressIndex !== null
                      ? "Sửa địa chỉ"
                      : "Thêm địa chỉ mới"
                  }}
                </h3>
                <button class="close-btn" @click="closeAddressForm">
                  <i class="fas fa-times"></i>
                </button>
              </div>

              <form @submit.prevent="saveAddress" class="profile-form">
                <div class="form-group">
                  <label>
                    <i class="fas fa-user"></i>
                    Họ và tên
                  </label>
                  <input
                    type="text"
                    v-model="addressForm.recipientName"
                    placeholder="Nhập họ và tên"
                    required
                  />
                </div>

                <div class="form-group">
                  <label>
                    <i class="fas fa-phone"></i>
                    Số điện thoại
                  </label>
                  <input
                    type="tel"
                    v-model="addressForm.phone"
                    placeholder="Nhập số điện thoại"
                    required
                  />
                </div>

                <div class="form-group">
                  <label>
                    <i class="fas fa-map"></i>
                    Địa chỉ chi tiết
                  </label>
                  <input
                    type="text"
                    v-model="addressForm.addressLine"
                    placeholder="Số nhà, tên đường"
                    required
                  />
                </div>

                <div class="form-row">
                  <div class="form-group">
                    <label>Quận/Huyện</label>
                    <input
                      type="text"
                      v-model="addressForm.district"
                      placeholder="Quận/Huyện"
                    />
                  </div>

                  <div class="form-group">
                    <label>Tỉnh/Thành phố</label>
                    <input
                      type="text"
                      v-model="addressForm.city"
                      placeholder="Tỉnh/Thành phố"
                    />
                  </div>
                </div>

                <div class="form-group">
                  <label>Phường/Xã</label>
                  <input
                    type="text"
                    v-model="addressForm.ward"
                    placeholder="Phường/Xã"
                  />
                </div>

                <div class="form-group">
                  <label>Ghi chú</label>
                  <textarea
                    v-model="addressForm.note"
                    placeholder="Ghi chú giao hàng"
                    rows="2"
                  ></textarea>
                </div>

                <div class="form-checkbox">
                  <input
                    type="checkbox"
                    id="setDefault"
                    v-model="addressForm.isDefault"
                  />
                  <label for="setDefault">Đặt làm địa chỉ mặc định</label>
                </div>

                <div class="form-actions">
                  <button type="submit" class="btn-primary">
                    <i class="fas fa-save"></i>
                    Lưu địa chỉ
                  </button>
                  <button
                    type="button"
                    class="btn-secondary"
                    @click="closeAddressForm"
                  >
                    <i class="fas fa-times"></i>
                    Hủy bỏ
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapMutations } from "vuex";
import Swal from "sweetalert2";
import api from "@/axios";
import storage from "@/utils/storage";

export default {
  name: "Profile",

  data() {
    return {
      activeTab: "info",

      profileForm: {
        name: "",
        email: "",
        phone: "",
      },

      passwordForm: {
        oldPassword: "",
        newPassword: "",
        confirmPassword: "",
      },

      showOldPassword: false,
      showNewPassword: false,
      showConfirmPassword: false,

      addresses: [],

      addressForm: {
        recipientName: "",
        phone: "",
        addressLine: "",
        ward: "",
        district: "",
        city: "",
        isDefault: false,
        note: "",
      },

      showAddressForm: false,
      editingAddressIndex: null,
    };
  },

  computed: {
    ...mapState(["user"]),
  },

  mounted() {
    this.loadUserData();
    this.fetchAddresses();
  },

  methods: {
    ...mapMutations(["setUser"]),

    normalizeAddress(address = {}) {
      return {
        id: address.id || address.addressId,
        recipientName:
          address.recipientName ||
          address.fullName ||
          address.name ||
          address.recipient ||
          address.recipient_name,
        phone:
          address.phone ||
          address.phoneNumber ||
          address.phone_number ||
          address.recipientPhone ||
          address.mobile ||
          address.phoneNo ||
          address.phone_no,
        addressLine:
          address.addressLine ||
          address.address ||
          address.address_line ||
          address.street ||
          address.addressDetail ||
          address.address_detail ||
          address.detailAddress ||
          address.detail_address,
        ward: address.ward || address.wardName || address.ward_name,
        district:
          address.district || address.districtName || address.district_name,
        city:
          address.city ||
          address.cityName ||
          address.city_name ||
          address.province,
        isDefault:
          address.isDefault ||
          address.default ||
          address.defaultAddress ||
          address.default_address ||
          address.is_default ||
          false,
        note: address.note,
      };
    },

    normalizeAddresses(list = []) {
      return list.map((item) => this.normalizeAddress(item));
    },

    formatAddress(address) {
      if (!address) return "";
      const parts = [
        address.addressLine,
        address.ward,
        address.district,
        address.city,
      ].filter(Boolean);
      return parts.join(", ");
    },

    loadUserData() {
      if (this.user) {
        this.profileForm.name = this.user.fullName || this.user.user_name || "";
        this.profileForm.email = this.user.email || this.user.user_email || "";
        this.profileForm.phone = this.user.phone || this.user.user_phone || "";
      }
    },

    // ✅ ĐÃ FIX - Gọi /users/me thay vì /users/profile/{id}
    async updateProfile() {
      try {
        const token = storage.getToken();
        if (!token) throw new Error("Chưa đăng nhập");

        const payload = {
          fullName: this.profileForm.name,
          phone: this.profileForm.phone,
          avatarUrl: null,
        };

        // ✅ FIX: Gọi /users/me - Backend tự lấy user từ JWT token
        const res = await api.patch("/users/me", payload);

        const updated = res.data?.data || payload;
        this.setUser(updated);
        storage.setUser(updated);
        this.loadUserData();

        Swal.fire({
          title: "Thành công!",
          text: "Cập nhật thông tin thành công",
          icon: "success",
          confirmButtonColor: "#00b067",
          timer: 1500,
        });
      } catch (error) {
        console.error("Error updating profile:", error);
        Swal.fire({
          title: "Lỗi!",
          text:
            error.response?.data?.message ||
            error.response?.data?.error ||
            error.message ||
            "Có lỗi xảy ra khi cập nhật thông tin",
          icon: "error",
          confirmButtonColor: "#e74c3c",
        });
      }
    },

    resetProfileForm() {
      this.loadUserData();
    },

    async changePassword() {
      if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
        Swal.fire({
          title: "Lỗi!",
          text: "Mật khẩu mới không khớp",
          icon: "error",
          confirmButtonColor: "#e74c3c",
        });
        return;
      }

      if (this.passwordForm.newPassword.length < 8) {
        Swal.fire({
          title: "Lỗi!",
          text: "Mật khẩu phải có ít nhất 8 ký tự",
          icon: "error",
          confirmButtonColor: "#e74c3c",
        });
        return;
      }

      try {
        const token = storage.getToken();
        if (!token) throw new Error("Chưa đăng nhập");

        // ✅ FIX: Gọi /users/me/password
        await api.patch("/users/me/password", {
          currentPassword: this.passwordForm.oldPassword,
          newPassword: this.passwordForm.newPassword,
          confirmPassword: this.passwordForm.confirmPassword,
        });

        Swal.fire({
          title: "Thành công!",
          text: "Đổi mật khẩu thành công",
          icon: "success",
          confirmButtonColor: "#00b067",
          timer: 1500,
        });

        this.resetPasswordForm();
      } catch (error) {
        console.error("Error changing password:", error);
        Swal.fire({
          title: "Lỗi!",
          text:
            error.response?.data?.message ||
            error.message ||
            "Có lỗi xảy ra khi đổi mật khẩu",
          icon: "error",
          confirmButtonColor: "#e74c3c",
        });
      }
    },

    resetPasswordForm() {
      this.passwordForm = {
        oldPassword: "",
        newPassword: "",
        confirmPassword: "",
      };
      this.showOldPassword = false;
      this.showNewPassword = false;
      this.showConfirmPassword = false;
    },

    async fetchAddresses() {
      const token = storage.getToken();
      if (!token) return;

      const endpoints = ["/addresses", "/users/addresses"];

      for (const ep of endpoints) {
        try {
          const res = await api.get(ep, {
            headers: { Authorization: `Bearer ${token}` },
          });
          const raw = res.data?.data || res.data || [];
          this.addresses = this.normalizeAddresses(
            Array.isArray(raw) ? raw : raw.content || []
          );
          return;
        } catch (err) {
          console.error(`Error fetching addresses via ${ep}:`, err);
        }
      }

      Swal.fire({
        title: "Lỗi!",
        text: "Không thể tải danh sách địa chỉ. Vui lòng thử lại.",
        icon: "error",
        confirmButtonColor: "#e74c3c",
      });
    },

    editAddress(index) {
      this.editingAddressIndex = index;
      const addr = this.addresses[index];
      this.addressForm = {
        recipientName: addr.recipientName || "",
        phone: addr.phone || "",
        addressLine: addr.addressLine || "",
        ward: addr.ward || "",
        district: addr.district || "",
        city: addr.city || "",
        isDefault: !!addr.isDefault,
        note: addr.note || "",
        id: addr.id,
      };
      this.showAddressForm = true;
    },

    async deleteAddress(index) {
      const addr = this.addresses[index];
      const result = await Swal.fire({
        title: "Xác nhận xóa?",
        text: "Bạn có chắc muốn xóa địa chỉ này?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#e74c3c",
        cancelButtonColor: "#6c757d",
        confirmButtonText: "Xóa",
        cancelButtonText: "Hủy",
      });

      if (result.isConfirmed) {
        try {
          const token = storage.getToken();
          if (!token) throw new Error("Chưa đăng nhập");

          if (addr?.id) {
            const endpoints = [`/addresses/${addr.id}`, `/users/addresses/${addr.id}`];
            let ok = false;
            for (const ep of endpoints) {
              try {
                await api.delete(ep, {
                  headers: { Authorization: `Bearer ${token}` },
                });
                ok = true;
                break;
              } catch (e) {
                console.error(`DELETE ${ep} failed:`, e);
              }
            }
            if (!ok) throw new Error("Không thể xóa địa chỉ");
          }
          this.addresses.splice(index, 1);
          Swal.fire({
            title: "Đã xóa!",
            text: "Địa chỉ đã được xóa",
            icon: "success",
            confirmButtonColor: "#00b067",
            timer: 1200,
          });
        } catch (err) {
          console.error("Lỗi xóa địa chỉ:", err);
          Swal.fire({
            title: "Lỗi!",
            text: err.response?.data?.message || "Không thể xóa địa chỉ",
            icon: "error",
            confirmButtonColor: "#e74c3c",
          });
        }
      }
    },

    async setDefaultAddress(index) {
      try {
        const token = storage.getToken();
        if (!token) throw new Error("Chưa đăng nhập");
        const addr = this.addresses[index];
        if (!addr?.id) throw new Error("Không tìm thấy địa chỉ hợp lệ");

        await api.put(
          `/addresses/${addr.id}/set-default`,
          {},
          { headers: { Authorization: `Bearer ${token}` } }
        );
        this.addresses = this.addresses.map((a, i) => ({
          ...a,
          isDefault: i === index,
        }));
        Swal.fire({
          title: "Thành công!",
          text: "Đã đặt làm địa chỉ mặc định",
          icon: "success",
          confirmButtonColor: "#00b067",
          timer: 1200,
        });
      } catch (err) {
        console.error("Lỗi đặt mặc định:", err);
        Swal.fire({
          title: "Lỗi!",
          text: err.response?.data?.message || "Không thể đặt địa chỉ mặc định",
          icon: "error",
          confirmButtonColor: "#e74c3c",
        });
      }
    },

    async saveAddress() {
      try {
        const token = storage.getToken();
        if (!token) throw new Error("Chưa đăng nhập");

        if (
          !this.addressForm.recipientName ||
          !this.addressForm.phone ||
          !this.addressForm.addressLine
        ) {
          throw new Error("Vui lòng nhập đầy đủ họ tên, điện thoại và địa chỉ.");
        }

        // Nếu chọn mặc định, bỏ mặc định các địa chỉ khác trên client
        if (this.addressForm.isDefault) {
          this.addresses.forEach((addr) => (addr.isDefault = false));
        }

        const payload = {
          recipientName: this.addressForm.recipientName,
          phone: this.addressForm.phone,
          addressLine: this.addressForm.addressLine,
          ward: this.addressForm.ward,
          district: this.addressForm.district,
          city: this.addressForm.city,
          isDefault: this.addressForm.isDefault,
          note: this.addressForm.note,
        };

        let saved;
        if (this.editingAddressIndex !== null) {
          const existing = this.addresses[this.editingAddressIndex];
          const id = existing?.id;
          if (id) {
            const endpoints = [`/addresses/${id}`, `/users/addresses/${id}`];
            for (const ep of endpoints) {
              try {
                const res = await api.put(ep, payload, {
                  headers: { Authorization: `Bearer ${token}` },
                });
                saved = this.normalizeAddress(res.data?.data || { ...payload, id });
                break;
              } catch (err) {
                console.error(`PUT ${ep} failed:`, err);
              }
            }
            if (!saved) throw new Error("Không thể cập nhật địa chỉ");
          } else {
            saved = this.normalizeAddress({ ...payload, id });
          }
          this.addresses.splice(
            this.editingAddressIndex,
            1,
            this.normalizeAddress(saved)
          );
        } else {
          const endpoints = ["/addresses", "/users/addresses"];
          for (const ep of endpoints) {
            try {
              const res = await api.post(ep, payload, {
                headers: { Authorization: `Bearer ${token}` },
              });
              saved = this.normalizeAddress(res.data?.data || { ...payload });
              break;
            } catch (err) {
              console.error(`POST ${ep} failed:`, err);
            }
          }
          if (!saved) throw new Error("Không thể lưu địa chỉ");
          this.addresses.push(this.normalizeAddress(saved));
        }

        Swal.fire({
          title: "Thành công!",
          text: "Địa chỉ đã được lưu",
          icon: "success",
          confirmButtonColor: "#00b067",
          timer: 1200,
        });

        this.closeAddressForm();
      } catch (err) {
        console.error("Lỗi lưu địa chỉ:", err);
        Swal.fire({
          title: "Lỗi!",
          text:
            err.response?.data?.message ||
            err.message ||
            "Không thể lưu địa chỉ",
          icon: "error",
          confirmButtonColor: "#e74c3c",
        });
      }
    },

    closeAddressForm() {
      this.showAddressForm = false;
      this.editingAddressIndex = null;
      this.addressForm = {
        recipientName: "",
        phone: "",
        addressLine: "",
        ward: "",
        district: "",
        city: "",
        isDefault: false,
        note: "",
      };
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

.profile-page {
  min-height: 100vh;
  background: #f8f9fa;
}

/* Page Header */
.page-header {
  background: linear-gradient(135deg, #00b067 0%, #00d97e 100%);
  padding: 60px 5%;
  color: white;
}

.header-content h1 {
  font-size: 36px;
  font-weight: 900;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-content p {
  font-size: 18px;
  opacity: 0.9;
}

/* Profile Container */
.profile-container {
  max-width: 1400px;
  margin: -40px auto 60px;
  padding: 0 5%;
  display: flex;
  gap: 32px;
}

/* Sidebar */
.profile-sidebar {
  width: 300px;
  flex-shrink: 0;
}

.user-card {
  background: white;
  border-radius: 20px;
  padding: 32px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.user-avatar {
  width: 100px;
  height: 100px;
  margin: 0 auto 20px;
  color: #00b067;
  font-size: 100px;
}

.user-card h3 {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.user-card p {
  font-size: 14px;
  color: #666;
}

.sidebar-menu {
  background: white;
  border-radius: 20px;
  padding: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 14px 16px;
  border: none;
  background: transparent;
  border-radius: 12px;
  color: #666;
  font-size: 15px;
  font-weight: 600;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 8px;
}

.menu-item i {
  font-size: 18px;
  width: 20px;
}

.menu-item:hover,
.menu-item.active {
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  color: #00b067;
}

.menu-item:last-child {
  margin-bottom: 0;
}

/* Content */
.profile-content {
  flex: 1;
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.section-header {
  margin-bottom: 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.section-header h2 {
  font-size: 28px;
  font-weight: 800;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.section-header p {
  font-size: 15px;
  color: #666;
}

.btn-add-address {
  padding: 12px 24px;
  background: linear-gradient(135deg, #00b067 0%, #00d97e 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.btn-add-address:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 176, 103, 0.3);
}

/* Form */
.profile-form {
  max-width: 600px;
}

.form-group {
  margin-bottom: 24px;
  position: relative;
}

.form-group label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.form-group input {
  width: 100%;
  padding: 14px 16px;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  font-size: 15px;
  transition: all 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #00b067;
  box-shadow: 0 0 0 4px rgba(0, 176, 103, 0.1);
}

.toggle-password {
  position: absolute;
  right: 16px;
  top: 38px;
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  font-size: 18px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-checkbox {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 24px;
}

.form-checkbox input {
  width: 20px;
  height: 20px;
  cursor: pointer;
}

.form-actions {
  display: flex;
  gap: 12px;
}

.btn-primary,
.btn-secondary {
  padding: 14px 28px;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.btn-primary {
  background: linear-gradient(135deg, #00b067 0%, #00d97e 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(0, 176, 103, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 176, 103, 0.4);
}

.btn-secondary {
  background: #f8f9fa;
  color: #666;
}

.btn-secondary:hover {
  background: #e9ecef;
}

/* Password Requirements */
.password-requirements {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 24px;
}

.password-requirements h4 {
  font-size: 14px;
  font-weight: 700;
  color: #333;
  margin-bottom: 12px;
}

.password-requirements ul {
  list-style: none;
}

.password-requirements li {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}

.password-requirements li i {
  font-size: 16px;
}

.password-requirements li.valid {
  color: #00b067;
}

/* Address List */
.address-list {
  display: grid;
  gap: 20px;
}

.address-card {
  background: #f8f9fa;
  border: 2px solid #e9ecef;
  border-radius: 16px;
  padding: 24px;
  transition: all 0.3s ease;
}

.address-card.default {
  border-color: #00b067;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
}

.address-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.address-header h3 {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a1a;
}

.default-badge {
  background: #00b067;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 700;
}

.address-phone,
.address-detail {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.address-actions {
  display: flex;
  gap: 8px;
  margin-top: 16px;
}

.btn-icon {
  padding: 8px 16px;
  background: white;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s ease;
}

.btn-icon:hover {
  background: #00b067;
  color: white;
  border-color: #00b067;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-state i {
  font-size: 80px;
  color: #ddd;
  margin-bottom: 24px;
}

.empty-state h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 8px;
}

.empty-state p {
  font-size: 14px;
  color: #666;
}

/* Address Form Modal */
.address-form-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 20px;
}

.address-form-modal {
  background: white;
  border-radius: 20px;
  width: 100%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  padding: 32px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.modal-header h3 {
  font-size: 24px;
  font-weight: 800;
  color: #1a1a1a;
}

.close-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: #f8f9fa;
  color: #666;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: #e74c3c;
  color: white;
}

/* Responsive */
@media (max-width: 1024px) {
  .profile-container {
    flex-direction: column;
  }

  .profile-sidebar {
    width: 100%;
  }

  .sidebar-menu {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 40px 5%;
  }

  .header-content h1 {
    font-size: 28px;
  }

  .profile-content {
    padding: 24px;
  }

  .section-header h2 {
    font-size: 22px;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn-primary,
  .btn-secondary {
    width: 100%;
    justify-content: center;
  }
}
</style>
