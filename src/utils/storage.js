const STORAGE_KEYS = {
  token: "token",
  refreshToken: "refreshToken",
  accessToken: "accessToken",
  user: "user",
  isAdmin: "is_admin",
  pendingOrderId: "pending_order_id",
  pendingOrderNumber: "pending_order_number",
};

const hasSessionStorage =
  typeof window !== "undefined" &&
  typeof window.sessionStorage !== "undefined";

const storage = {
  get(key, fallback = null) {
    if (!hasSessionStorage) return fallback;
    try {
      const value = window.sessionStorage.getItem(key);
      return value === null ? fallback : value;
    } catch (err) {
      console.error(`Storage get error for key "${key}":`, err);
      return fallback;
    }
  },

  set(key, value) {
    if (!hasSessionStorage) return;
    try {
      window.sessionStorage.setItem(key, value);
    } catch (err) {
      console.error(`Storage set error for key "${key}":`, err);
    }
  },

  remove(key) {
    if (!hasSessionStorage) return;
    try {
      window.sessionStorage.removeItem(key);
    } catch (err) {
      console.error(`Storage remove error for key "${key}":`, err);
    }
  },

  getJSON(key, fallback = null) {
    const raw = this.get(key);
    if (raw === null || raw === undefined) return fallback;
    try {
      return JSON.parse(raw);
    } catch (err) {
      console.error(`Storage parse error for key "${key}":`, err);
      return fallback;
    }
  },

  setJSON(key, value) {
    try {
      this.set(key, JSON.stringify(value));
    } catch (err) {
      console.error(`Storage JSON set error for key "${key}":`, err);
    }
  },

  clearAuth() {
    [
      STORAGE_KEYS.token,
      STORAGE_KEYS.refreshToken,
      STORAGE_KEYS.accessToken,
      STORAGE_KEYS.user,
      STORAGE_KEYS.isAdmin,
    ].forEach((key) => this.remove(key));
  },

  clearPendingOrder() {
    [
      STORAGE_KEYS.pendingOrderId,
      STORAGE_KEYS.pendingOrderNumber,
    ].forEach((key) => this.remove(key));
  },

  getToken() {
    return this.get(STORAGE_KEYS.token, null);
  },

  setToken(token) {
    if (token) {
      this.set(STORAGE_KEYS.token, token);
    } else {
      this.remove(STORAGE_KEYS.token);
    }
  },

  getUser() {
    return this.getJSON(STORAGE_KEYS.user, null);
  },

  setUser(user) {
    if (user) {
      this.setJSON(STORAGE_KEYS.user, user);
    } else {
      this.remove(STORAGE_KEYS.user);
    }
  },

  setAdminFlag(isAdmin) {
    if (isAdmin) {
      this.set(STORAGE_KEYS.isAdmin, "1");
    } else {
      this.remove(STORAGE_KEYS.isAdmin);
    }
  },
};

export { STORAGE_KEYS };
export default storage;
