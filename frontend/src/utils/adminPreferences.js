const STORAGE_KEYS = {
  theme: "theme",
  sidebarCollapsed: "admin_sidebar_collapsed",
  pendingRefreshMs: "admin_pending_refresh_ms",
  ordersPageSize: "admin_orders_page_size",
};

const DEFAULT_ADMIN_PREFERENCES = {
  theme: "light",
  sidebarCollapsed: false,
  pendingRefreshMs: 30000,
  ordersPageSize: 10,
};

const ADMIN_PREFERENCES_EVENT = "admin-preferences-updated";

const hasWindow = typeof window !== "undefined";
const hasLocalStorage = hasWindow && typeof window.localStorage !== "undefined";

function readValue(key, fallback = null) {
  if (!hasLocalStorage) return fallback;
  try {
    const value = window.localStorage.getItem(key);
    return value === null ? fallback : value;
  } catch (error) {
    console.error(`Admin prefs read error for key "${key}":`, error);
    return fallback;
  }
}

function writeValue(key, value) {
  if (!hasLocalStorage) return;
  try {
    if (value === null || value === undefined) {
      window.localStorage.removeItem(key);
      return;
    }
    window.localStorage.setItem(key, String(value));
  } catch (error) {
    console.error(`Admin prefs write error for key "${key}":`, error);
  }
}

function normalizeTheme(value) {
  return String(value || "").toLowerCase() === "dark" ? "dark" : "light";
}

function normalizeBoolean(value, fallback) {
  if (value === null || value === undefined || value === "") return fallback;
  if (typeof value === "boolean") return value;

  const normalized = String(value).toLowerCase();
  if (["1", "true", "yes", "on"].includes(normalized)) return true;
  if (["0", "false", "no", "off"].includes(normalized)) return false;
  return fallback;
}

function normalizeNumber(value, fallback) {
  const parsed = Number(value);
  return Number.isFinite(parsed) && parsed >= 0 ? parsed : fallback;
}

function normalizePreferences(prefs = {}) {
  return {
    theme: normalizeTheme(prefs.theme ?? DEFAULT_ADMIN_PREFERENCES.theme),
    sidebarCollapsed: normalizeBoolean(
      prefs.sidebarCollapsed,
      DEFAULT_ADMIN_PREFERENCES.sidebarCollapsed,
    ),
    pendingRefreshMs: normalizeNumber(
      prefs.pendingRefreshMs,
      DEFAULT_ADMIN_PREFERENCES.pendingRefreshMs,
    ),
    ordersPageSize: normalizeNumber(
      prefs.ordersPageSize,
      DEFAULT_ADMIN_PREFERENCES.ordersPageSize,
    ),
  };
}

function loadAdminPreferences() {
  return normalizePreferences({
    theme: readValue(STORAGE_KEYS.theme, DEFAULT_ADMIN_PREFERENCES.theme),
    sidebarCollapsed: readValue(
      STORAGE_KEYS.sidebarCollapsed,
      DEFAULT_ADMIN_PREFERENCES.sidebarCollapsed,
    ),
    pendingRefreshMs: readValue(
      STORAGE_KEYS.pendingRefreshMs,
      DEFAULT_ADMIN_PREFERENCES.pendingRefreshMs,
    ),
    ordersPageSize: readValue(
      STORAGE_KEYS.ordersPageSize,
      DEFAULT_ADMIN_PREFERENCES.ordersPageSize,
    ),
  });
}

function dispatchAdminPreferencesChange(prefs) {
  if (!hasWindow) return;
  window.dispatchEvent(
    new CustomEvent(ADMIN_PREFERENCES_EVENT, {
      detail: normalizePreferences(prefs),
    }),
  );
}

function saveAdminPreferences(nextPrefs = {}) {
  const current = loadAdminPreferences();
  const prefs = normalizePreferences({ ...current, ...nextPrefs });

  writeValue(STORAGE_KEYS.theme, prefs.theme);
  writeValue(STORAGE_KEYS.sidebarCollapsed, prefs.sidebarCollapsed ? "1" : "0");
  writeValue(STORAGE_KEYS.pendingRefreshMs, prefs.pendingRefreshMs);
  writeValue(STORAGE_KEYS.ordersPageSize, prefs.ordersPageSize);

  dispatchAdminPreferencesChange(prefs);
  return prefs;
}

function resetAdminPreferences() {
  return saveAdminPreferences(DEFAULT_ADMIN_PREFERENCES);
}

function onAdminPreferencesChange(handler) {
  if (!hasWindow) return () => {};
  window.addEventListener(ADMIN_PREFERENCES_EVENT, handler);
  return () => window.removeEventListener(ADMIN_PREFERENCES_EVENT, handler);
}

export {
  ADMIN_PREFERENCES_EVENT,
  DEFAULT_ADMIN_PREFERENCES,
  STORAGE_KEYS,
  dispatchAdminPreferencesChange,
  loadAdminPreferences,
  normalizePreferences,
  onAdminPreferencesChange,
  resetAdminPreferences,
  saveAdminPreferences,
};
