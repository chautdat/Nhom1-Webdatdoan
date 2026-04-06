import axios from "axios";
import storage from "@/utils/storage";

const api = axios.create({
  baseURL: "/api",
  headers: { "Content-Type": "application/json" },
  withCredentials: true,
  timeout: 10000,
});

// REQUEST INTERCEPTOR
api.interceptors.request.use(
  (config) => {
    const token = storage.getToken();
    if (token) config.headers["Authorization"] = `Bearer ${token}`;
    return config;
  },
  (error) => Promise.reject(error)
);

// RESPONSE INTERCEPTOR
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;

    if (status === 401) {
      storage.clearAuth();
      window.location.href = "/#/login";
    }

    if (status === 403) {
      alert("Bạn không có quyền truy cập!");
    }

    return Promise.reject(error);
  }
);

export default api;
