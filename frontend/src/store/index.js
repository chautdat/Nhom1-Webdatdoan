// src/store/index.js
import { createStore } from "vuex";
import api from "@/axios"; // ✅ Import custom axios
import storage, { STORAGE_KEYS } from "@/utils/storage";

const store = createStore({
  state() {
    return {
      allFoods: [],
      user: storage.getUser(),
      admin: storage.get(STORAGE_KEYS.isAdmin) === "1" ? true : false,
      token: storage.getToken(),
    };
  },

  mutations: {
    // ✅ Cập nhật danh sách món ăn
    setFoodsData(state, payload) {
      state.allFoods = payload;
    },

    // ✅ Cập nhật thông tin người dùng
    setUser(state, userData) {
      state.user = userData;
      if (!userData) {
        // xóa tất cả thông tin khi logout
        storage.clearAuth();
      } else {
        storage.setUser(userData);
      }
    },

    // ✅ Cập nhật token
    setToken(state, token) {
      state.token = token;
      if (token) {
        storage.setToken(token);
      } else {
        storage.remove(STORAGE_KEYS.token);
      }
    },

    // ✅ Cập nhật quyền admin
    setAdmin(state, isAdmin) {
      state.admin = isAdmin;
      if (isAdmin) {
        storage.setAdminFlag(true);
      } else {
        storage.remove(STORAGE_KEYS.isAdmin);
      }
    },
  },

  actions: {
    // ✅ Lấy danh sách món ăn
    async getFoodsData({ commit }) {
      try {
        const response = await api.get("/products");

        // Handle response
        if (response.data.success) {
          const foods = response.data.data.content || response.data.data;
          commit("setFoodsData", foods);
          return foods;
        }
      } catch (error) {
        console.error("❌ Lỗi khi lấy dữ liệu món ăn:", error);
        throw error;
      }
    },

    // ✅ Đăng nhập - FIX: Thêm commit setAdmin
    async login({ commit }, credentials) {
      try {
        const response = await api.post("/auth/login", {
          username: credentials.username || credentials.email,
          password: credentials.password,
        });

        if (response.data.success) {
          const { token, user } = response.data.data;

          // Save token
          commit("setToken", token);

          // Save user info
          commit("setUser", user);

          // ✅ FIX: Check admin và commit ngay
          const role = user.role || "";
          commit("setAdmin", role.toUpperCase().includes("ADMIN"));

          return response.data;
        }
      } catch (error) {
        console.error("❌ Lỗi đăng nhập:", error);
        throw error;
      }
    },

    // ✅ Đăng ký - FIX: Sử dụng .includes() thay vì ===
    async register({ commit }, userData) {
      try {
        const response = await api.post("/auth/register", userData);

        if (response.data.success) {
          const { token, user } = response.data.data;

          commit("setToken", token);
          commit("setUser", user);

          // ✅ FIX: Dùng .includes() như login
          const role = user.role || "";
          commit("setAdmin", role.toUpperCase().includes("ADMIN"));

          return response.data;
        }
      } catch (error) {
        console.error("❌ Lỗi đăng ký:", error);
        throw error;
      }
    },

    // ✅ Đăng xuất
    logout({ commit }) {
      commit("setToken", null);
      commit("setUser", null);
      commit("setAdmin", false);

      // refresh lại để Vuex load state mới từ localStorage
      setTimeout(() => {
        window.location.reload();
      }, 200);
    },
  },

  getters: {
    isAuthenticated: (state) => !!state.token,
    isAdmin: (state) => state.admin,
    currentUser: (state) => state.user,
    allFoods: (state) => state.allFoods,
  },
});

export default store;
