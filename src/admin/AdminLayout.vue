<template>
  <v-app>
    <!-- ===== SIDEBAR (NAVIGATION DRAWER) ===== -->
    <v-navigation-drawer
      v-model="drawer"
      app
      :rail="!mobile && rail"
      :permanent="!mobile"
      :temporary="mobile"
      color="surface"
      elevation="1"
      :width="mobile ? 280 : 244"
      :rail-width="64"
    >
      <!-- Logo Section -->
      <div class="pa-4 d-flex align-center" :class="{ 'justify-center': rail }">
        <v-avatar :size="rail ? 40 : 48" color="primary" class="elevation-2">
          <v-icon size="24" color="white">mdi-silverware-fork-knife</v-icon>
        </v-avatar>

        <div v-if="!rail" class="ml-3">
          <div class="logo-title">PDQ Restaurant</div>
          <div class="text-caption text-medium-emphasis">Admin Panel</div>
        </div>
      </div>

      <v-divider></v-divider>

      <!-- User Info Card (when not rail) -->
      <v-card v-if="!rail" flat color="primary" class="ma-3 mb-2" rounded="lg">
        <v-card-text class="pa-3">
          <div class="d-flex align-center">
            <v-avatar size="40" color="white">
              <span class="text-primary font-weight-bold">{{ userInitial }}</span>
            </v-avatar>
            <div class="ml-3 flex-grow-1" style="min-width: 0">
              <div class="text-subtitle-2 font-weight-bold text-white text-truncate">
                {{ userName }}
              </div>
              <div class="text-caption text-white text-truncate" style="opacity: 0.9">
                {{ userRole }}
              </div>
            </div>
          </div>
        </v-card-text>
      </v-card>

      <!-- Menu Items -->
      <v-list nav density="default" class="pa-2">
        <v-list-item
          :to="{ name: 'AdminDashboard' }"
          exact
          prepend-icon="mdi-view-dashboard"
          title="Dashboard"
          value="dashboard"
          color="primary"
          rounded="lg"
          class="menu-item mb-1"
        ></v-list-item>

        <v-list-item
          :to="{ name: 'AdminOrders' }"
          prepend-icon="mdi-cart"
          title="Orders"
          value="orders"
          color="primary"
          rounded="lg"
          class="menu-item mb-1"
        >
          <template v-slot:append v-if="!rail && pendingOrdersCount > 0">
            <v-chip size="small" color="error" variant="flat" class="font-weight-bold">
              {{ pendingOrdersCount }}
            </v-chip>
          </template>
        </v-list-item>

        <v-divider class="my-3"></v-divider>

        <v-list-item
          :to="{ name: 'AdminMenu' }"
          prepend-icon="mdi-food"
          title="Products"
          value="menu"
          color="primary"
          rounded="lg"
          class="menu-item mb-1"
        ></v-list-item>

        <v-list-item
          :to="{ name: 'AdminCategories' }"
          prepend-icon="mdi-shape"
          title="Categories"
          value="categories"
          color="primary"
          rounded="lg"
          class="menu-item mb-1"
        ></v-list-item>
        <v-divider class="my-3"></v-divider>

        <v-list-item
          :to="{ name: 'AdminUsers' }"
          prepend-icon="mdi-account-group"
          title="Users"
          value="users"
          color="primary"
          rounded="lg"
          class="menu-item mb-1"
        ></v-list-item>
      </v-list>

      <!-- Sidebar Footer -->
      <template v-slot:append>
        <div class="sidebar-footer">
          <!-- Collapse/Expand Button (Desktop only) -->
          <v-btn
            v-if="!mobile"
            icon
            variant="tonal"
            color="primary"
            @click.stop="toggleSidebarCollapse"
            class="sidebar-toggle-btn"
            :aria-label="rail ? 'Expand sidebar' : 'Collapse sidebar'"
            size="40"
          >
            <v-icon size="20">
              {{ rail ? 'mdi-chevron-right' : 'mdi-chevron-left' }}
            </v-icon>
          </v-btn>

          <!-- Logout Button -->
          <v-btn
            icon
            color="error"
            variant="flat"
            @click="openLogoutConfirm"
            class="sidebar-action-btn sidebar-logout-btn"
            aria-label="Logout"
            size="40"
          >
            <v-icon size="20">mdi-logout</v-icon>
          </v-btn>
        </div>
      </template>
    </v-navigation-drawer>

    <!-- ===== TOP APP BAR ===== -->
    <v-app-bar elevation="0" border="b" color="surface" height="64" class="px-6">
      <!-- Mobile Menu Toggle -->
      <v-app-bar-nav-icon v-if="mobile" @click="drawer = !drawer" class="mr-2"></v-app-bar-nav-icon>

      <!-- Breadcrumbs & Page Title -->
      <div class="d-flex flex-column">
        <v-breadcrumbs :items="breadcrumbs" density="compact" class="pa-0" style="min-height: 0">
          <template v-slot:divider>
            <v-icon size="14">mdi-chevron-right</v-icon>
          </template>
        </v-breadcrumbs>
        <div class="page-title">
          {{ pageTitle }}
        </div>
      </div>

      <v-spacer></v-spacer>

      <!-- Search Button (Optional) -->
      <v-btn icon variant="text" class="mr-2" size="large">
        <v-icon>mdi-magnify</v-icon>
      </v-btn>

      <!-- Dark Mode Toggle -->
      <v-btn icon variant="text" @click="toggleTheme" class="mr-2" size="large">
        <v-icon>
          {{ theme.global.name.value === 'dark' ? 'mdi-white-balance-sunny' : 'mdi-weather-night' }}
        </v-icon>
      </v-btn>

      <!-- Notifications -->
      <v-menu location="bottom end" :offset="8" origin="top end" transition="slide-y-transition">
        <template v-slot:activator="{ props }">
          <v-btn icon variant="text" class="mr-2" v-bind="props" size="large">
            <v-badge v-if="pendingOrdersCount > 0" :content="pendingOrdersCount" color="error" overlap>
              <v-icon>mdi-bell</v-icon>
            </v-badge>
            <v-icon v-else>mdi-bell-outline</v-icon>
          </v-btn>
        </template>

        <v-card width="320" elevation="8">
          <v-card-title class="d-flex align-center pa-4">
            <v-icon class="mr-2">mdi-bell</v-icon>
            <span class="notification-title">Notifications</span>
            <v-spacer></v-spacer>
            <v-chip size="small" color="error" v-if="pendingOrdersCount > 0">
              {{ pendingOrdersCount }}
            </v-chip>
          </v-card-title>
          <v-divider></v-divider>
          <v-list lines="two" density="compact">
            <v-list-item
              v-if="pendingOrdersCount > 0"
              prepend-icon="mdi-cart"
              :title="`${pendingOrdersCount} pending orders`"
              subtitle="Waiting for confirmation"
            >
              <template v-slot:append>
                <v-chip size="small" color="warning">New</v-chip>
              </template>
            </v-list-item>
            <v-list-item v-else>
              <div class="text-center pa-4 text-medium-emphasis">
                <v-icon size="48" class="mb-2">mdi-bell-off-outline</v-icon>
                <div>No new notifications</div>
              </div>
            </v-list-item>
          </v-list>
        </v-card>
      </v-menu>

      <!-- User Menu -->
      <v-menu location="bottom end" :offset="8" origin="top end" transition="slide-y-transition">
        <template v-slot:activator="{ props }">
          <v-btn variant="text" v-bind="props" class="px-2 user-menu-trigger">
            <v-avatar size="36" color="primary" class="mr-2">
              <span class="text-white font-weight-bold">{{ userInitial }}</span>
            </v-avatar>
            <div class="text-left d-none d-sm-block user-menu-trigger__text">
              <div class="user-menu-trigger__name">{{ userName }}</div>
            </div>
            <v-icon class="ml-2">mdi-chevron-down</v-icon>
          </v-btn>
        </template>

        <v-card width="224" elevation="12" class="user-menu-card">
          <div class="user-menu-card__header">
            <v-avatar size="40" color="primary" class="user-menu-avatar">
              <span class="text-white text-h6 font-weight-bold">{{ userInitial }}</span>
            </v-avatar>
            <div class="user-menu-card__meta">
              <div class="user-menu-name">{{ userName }}</div>
              <div class="user-menu-email">{{ userEmail }}</div>
            </div>
          </div>

          <v-divider></v-divider>

          <v-list density="compact" nav class="user-menu-list">
            <v-list-item
              prepend-icon="mdi-account"
              title="Profile"
              value="profile"
              @click="goToProfile"
              class="user-menu-action"
            ></v-list-item>
            <v-list-item
              prepend-icon="mdi-help-circle"
              title="Help & Support"
              value="help"
              class="user-menu-action"
            ></v-list-item>
          </v-list>

          <v-divider></v-divider>

          <div class="px-2 pb-2 pt-1">
            <v-btn
              block
              color="error"
              variant="flat"
              prepend-icon="mdi-logout"
              @click="openLogoutConfirm"
              class="user-menu-logout"
            >
              Logout
            </v-btn>
          </div>
        </v-card>
      </v-menu>
    </v-app-bar>

    <!-- ===== LOGOUT CONFIRM DIALOG ===== -->
    <v-dialog v-model="logoutConfirmDialog" max-width="420">
      <v-card rounded="lg">
        <v-card-title class="text-h6 font-weight-bold">
          Xác nhận đăng xuất
        </v-card-title>

        <v-card-text> Bạn có chắc muốn đăng xuất khỏi hệ thống? </v-card-text>

        <v-card-actions class="px-4 pb-4">
          <v-spacer></v-spacer>
          <v-btn variant="tonal" @click="closeLogoutConfirm">Hủy</v-btn>
          <v-btn color="error" variant="flat" prepend-icon="mdi-logout" @click="logout">
            Đăng xuất
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- ===== MAIN CONTENT ===== -->
    <v-main class="bg-grey-lighten-4">
      <v-container fluid class="pa-0 admin-page-shell">
        <!-- Page Transition -->
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </v-container>
    </v-main>

    <!-- ===== LOADING OVERLAY ===== -->
    <v-overlay v-model="isLoading" class="align-center justify-center" persistent>
      <v-progress-circular indeterminate size="64" color="primary"></v-progress-circular>
      <div class="text-h6 mt-4">Loading...</div>
    </v-overlay>
  </v-app>
</template>

<script>
import { ref, computed, onMounted, onBeforeUnmount, watch } from "vue";
import { useTheme, useDisplay } from "vuetify";
import { useRouter, useRoute } from "vue-router";
import api from "@/axios";
import storage from "@/utils/storage";
import {
  DEFAULT_ADMIN_PREFERENCES,
  loadAdminPreferences,
  onAdminPreferencesChange,
  saveAdminPreferences,
} from "@/utils/adminPreferences";

export default {
  name: "AdminLayout",

  setup() {
    const theme = useTheme();
    const { mobile } = useDisplay();
    const router = useRouter();
    const route = useRoute();

    // State
    const drawer = ref(true);
    const rail = ref(false);
    const isLoading = ref(false);
    const pendingOrdersCount = ref(0);
    const logoutConfirmDialog = ref(false);

    const preferences = ref(loadAdminPreferences());
    let pendingOrdersTimer = null;
    let stopListeningPreferences = null;

    // User Info
    const user = storage.getUser();
    const userName = computed(
      () => user?.fullName || user?.username || "Admin User",
    );
    const userEmail = computed(() => user?.email || "admin@pdq.com");
    const userRole = computed(() => {
      const role = user?.role || "ROLE_ADMIN";
      return role.replace("ROLE_", "");
    });
    const userInitial = computed(() => {
      return userName.value.charAt(0).toUpperCase();
    });

    const syncNavigationState = (prefs = preferences.value) => {
      if (mobile.value) {
        drawer.value = false;
        rail.value = false;
        return;
      }

      drawer.value = true;
      rail.value = Boolean(prefs.sidebarCollapsed);
    };

    // Page Title & Breadcrumbs
    const pageTitle = computed(() => {
      const name = route.name;
      const titles = {
        AdminDashboard: "Dashboard",
        AdminOrders: "Orders Management",
        AdminMenu: "Menu Management",
        AdminCategories: "Categories",
        AdminUsers: "User Management",
        AdminProfile: "Admin Profile",
      };
      return titles[name] || "Admin Panel";
    });

    const breadcrumbs = computed(() => {
      const items = [
        {
          title: "Home",
          disabled: false,
          to: { name: "AdminDashboard" },
        },
      ];

      items.push({
        title: pageTitle.value,
        disabled: true,
      });

      return items;
    });

    const startPendingOrdersPolling = (intervalMs) => {
      if (pendingOrdersTimer) {
        clearInterval(pendingOrdersTimer);
        pendingOrdersTimer = null;
      }

      const resolvedInterval = Number(intervalMs) || DEFAULT_ADMIN_PREFERENCES.pendingRefreshMs;
      if (resolvedInterval <= 0) {
        return;
      }

      pendingOrdersTimer = window.setInterval(fetchPendingOrdersCount, resolvedInterval);
    };

    const applyPreferences = (nextPrefs) => {
      preferences.value = nextPrefs;
      theme.global.name.value = nextPrefs.theme;
      syncNavigationState(nextPrefs);
      startPendingOrdersPolling(nextPrefs.pendingRefreshMs);
    };

    // Theme Toggle
    const toggleTheme = () => {
      const nextTheme = theme.global.name.value === "dark" ? "light" : "dark";
      const nextPrefs = saveAdminPreferences({
        ...preferences.value,
        theme: nextTheme,
      });
      applyPreferences(nextPrefs);
    };

    // Navigation
    const goToProfile = () => {
      router.push({ name: "AdminProfile" });
    };

    // Sidebar
    const toggleSidebarCollapse = () => {
      const nextPrefs = saveAdminPreferences({
        ...preferences.value,
        sidebarCollapsed: !preferences.value.sidebarCollapsed,
      });
      applyPreferences(nextPrefs);
    };

    // Logout
    const openLogoutConfirm = () => {
      logoutConfirmDialog.value = true;
    };

    const closeLogoutConfirm = () => {
      logoutConfirmDialog.value = false;
    };

    const logout = async () => {
      closeLogoutConfirm();
      isLoading.value = true;

      try {
        storage.clearAuth();
        storage.clearPendingOrder();
        await router.replace("/login");
      } finally {
        isLoading.value = false;
      }
    };

    // Fetch Pending Orders Count
    const fetchPendingOrdersCount = async () => {
      try {
        const res = await api.get("/admin/orders", {
          params: {
            page: 0,
            size: 1,
            status: "pending",
          },
        });

        const payload = res.data?.data;
        const list = Array.isArray(payload)
          ? payload
          : Array.isArray(payload?.content)
          ? payload.content
          : [];

        pendingOrdersCount.value = Number(payload?.totalElements ?? list.length ?? 0);
      } catch (error) {
        console.error("Error fetching pending orders:", error);
        pendingOrdersCount.value = 0;
      }
    };

    // Keep drawer behavior consistent across desktop/mobile
    watch(
      mobile,
      () => {
        syncNavigationState();
      },
      { immediate: true },
    );

    // Lifecycle
    onMounted(() => {
      applyPreferences(loadAdminPreferences());
      fetchPendingOrdersCount();

      stopListeningPreferences = onAdminPreferencesChange((event) => {
        const nextPrefs = event?.detail || loadAdminPreferences();
        applyPreferences(nextPrefs);
      });
    });

    onBeforeUnmount(() => {
      if (pendingOrdersTimer) {
        clearInterval(pendingOrdersTimer);
        pendingOrdersTimer = null;
      }

      if (typeof stopListeningPreferences === "function") {
        stopListeningPreferences();
      }
    });

    return {
      drawer,
      rail,
      mobile,
      isLoading,
      pendingOrdersCount,
      logoutConfirmDialog,
      userName,
      userEmail,
      userRole,
      userInitial,
      pageTitle,
      breadcrumbs,
      theme,
      toggleTheme,
      goToProfile,
      openLogoutConfirm,
      closeLogoutConfirm,
      logout,
      toggleSidebarCollapse,
    };
  },
};
</script>

<style scoped>
/* ===== FONT SIZES - CHỮ TO HƠN ===== */
.logo-title {
  font-size: 19px !important;
  font-weight: 700 !important;
  line-height: 1.2;
}

.menu-section-title {
  font-size: 12px !important;
  font-weight: 700 !important;
  letter-spacing: 0.5px !important;
  color: rgba(var(--v-theme-on-surface), 0.6) !important;
  padding-top: 12px !important;
  padding-bottom: 8px !important;
}

.page-title {
  font-size: 20px !important;
  font-weight: 700 !important;
  margin-top: 0;
  line-height: 1.1;
}

.notification-title {
  font-size: 16px !important;
  font-weight: 600 !important;
}

.user-name {
  font-size: 14px !important;
  font-weight: 600 !important;
}

.user-role {
  font-size: 12px !important;
  color: rgba(var(--v-theme-on-surface), 0.6);
}

.user-menu-name {
  font-size: 14px !important;
  font-weight: 700 !important;
}

.user-menu-email {
  font-size: 11px !important;
  color: rgba(var(--v-theme-on-surface), 0.6);
}

.user-menu-card {
  overflow: hidden;
  border: 1px solid rgba(var(--v-theme-on-surface), 0.06);
  border-radius: 16px;
  background: rgba(var(--v-theme-surface), 0.98);
}

.user-menu-card__header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 10px 8px;
  background:
    linear-gradient(
      180deg,
      rgba(var(--v-theme-primary), 0.08) 0%,
      rgba(var(--v-theme-surface), 0.98) 100%
    );
}

.user-menu-card__meta {
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.user-menu-avatar {
  flex: 0 0 auto;
}

.user-menu-trigger {
  min-width: 0 !important;
  height: 44px !important;
  padding-inline: 10px !important;
  border-radius: 14px !important;
  background: rgba(var(--v-theme-surface), 0.92);
  border: 1px solid rgba(var(--v-theme-on-surface), 0.08);
  letter-spacing: 0 !important;
  text-transform: none !important;
}

.user-menu-trigger__text {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  min-width: 0;
  line-height: 1.05;
}

.user-menu-trigger__name {
  max-width: 130px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px !important;
  font-weight: 700 !important;
  line-height: 1.05;
}

.btn-text {
  font-size: 15px !important;
  font-weight: 600 !important;
}

.user-menu-list {
  padding: 4px 4px 2px !important;
}

:deep(.user-menu-list .v-list-item) {
  min-height: 32px !important;
  padding-inline: 8px !important;
  border-radius: 9px !important;
  margin-bottom: 1px !important;
}

:deep(.user-menu-list .v-list-item-title) {
  font-size: 12px !important;
  font-weight: 600 !important;
}

:deep(.user-menu-list .v-list-item:hover) {
  background: rgba(var(--v-theme-primary), 0.06) !important;
}

.user-menu-logout {
  height: 34px !important;
  border-radius: 10px !important;
  font-size: 12px !important;
  font-weight: 700 !important;
  letter-spacing: 0.01em !important;
}

.menu-item .v-list-item-title {
  font-size: 16px !important;
  font-weight: 600 !important;
}

/* ===== MENU ITEMS STYLING ===== */
:deep(.menu-item .v-list-item-title) {
  font-size: 15px !important;
  font-weight: 500 !important;
  letter-spacing: 0.2px !important;
}

:deep(.menu-item) {
  min-height: 48px !important;
  padding: 10px 16px !important;
  margin-bottom: 4px !important;
}

:deep(.menu-item .v-icon) {
  font-size: 22px !important;
}

/* ===== BUTTON SIZES ===== */
.btn-large {
  height: 42px !important;
  font-size: 15px !important;
}

:deep(.sidebar-footer) {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.sidebar-toggle-btn {
  width: 40px !important;
  height: 40px !important;
  min-width: 40px !important;
  align-self: flex-start;
}

.sidebar-action-btn {
  width: 40px !important;
  height: 40px !important;
  min-width: 40px !important;
  align-self: flex-start;
}

.sidebar-logout-btn {
  margin-top: 2px;
}

:deep(.user-menu-trigger .v-btn__content) {
  gap: 8px;
  align-items: center;
}

:deep(.user-menu-trigger:focus-visible) {
  outline: none;
  box-shadow: 0 0 0 2px rgba(var(--v-theme-primary), 0.16);
}

/* ===== PAGE TRANSITION ===== */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

/* ===== ACTIVE MENU ITEM ===== */
:deep(.v-list-item--active) {
  background: linear-gradient(
    90deg,
    rgba(var(--v-theme-primary), 0.12) 0%,
    rgba(var(--v-theme-primary), 0.08) 100%
  ) !important;
  border-left: 3px solid rgb(var(--v-theme-primary));
}

:deep(.v-list-item--active .v-list-item-title) {
  color: rgb(var(--v-theme-primary)) !important;
  font-weight: 700 !important;
}

:deep(.v-list-item--active .v-icon) {
  color: rgb(var(--v-theme-primary)) !important;
}

/* ===== HOVER EFFECTS ===== */
:deep(.v-list-item) {
  transition: all 0.2s ease;
}

:deep(.v-list-item:hover:not(.v-list-item--active)) {
  background: rgba(var(--v-theme-primary), 0.05) !important;
  transform: translateX(4px);
}

/* ===== NAVIGATION RAIL MODE ===== */
:deep(.v-navigation-drawer--rail) {
  .v-list-item__prepend {
    margin-inline-end: 0 !important;
  }

  .v-list-subheader {
    display: none;
  }
}

/* ===== CUSTOM SCROLLBAR ===== */
:deep(.v-navigation-drawer__content) {
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: rgba(var(--v-theme-primary), 0.3) transparent;
}

:deep(.v-navigation-drawer__content::-webkit-scrollbar) {
  width: 6px;
}

:deep(.v-navigation-drawer__content::-webkit-scrollbar-thumb) {
  background: rgba(var(--v-theme-primary), 0.3);
  border-radius: 10px;
}

:deep(.v-navigation-drawer__content::-webkit-scrollbar-thumb:hover) {
  background: rgba(var(--v-theme-primary), 0.5);
}

/* ===== APP BAR SHADOW ===== */
:deep(.v-app-bar) {
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06) !important;
  backdrop-filter: blur(14px);
}

:deep(.v-navigation-drawer) {
  border-right: 1px solid rgba(15, 23, 42, 0.08) !important;
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.06) !important;
}

:deep(.v-navigation-drawer--rail) {
  background: rgba(var(--v-theme-surface), 0.98) !important;
}

:deep(.v-navigation-drawer__content) {
  background: rgba(var(--v-theme-surface), 0.98);
}

:deep(.v-main) {
  background: linear-gradient(
    180deg,
    rgba(248, 250, 252, 1) 0%,
    rgba(241, 245, 249, 1) 100%
  ) !important;
}

.v-theme--dark :deep(.v-main) {
  background: linear-gradient(
    180deg,
    rgb(18, 18, 18) 0%,
    rgb(24, 24, 24) 100%
  ) !important;
}

.admin-page-shell {
  width: 100%;
  max-width: 100% !important;
}

:deep(.menu-admin),
:deep(.users-page),
:deep(.category-management),
:deep(.orders-admin),
:deep(.promo-manager),
:deep(.admin-orders),
:deep(.dashboard-page) {
  background: transparent !important;
  max-width: none !important;
  margin: 0 !important;
  min-height: auto !important;
}

/* ===== BREADCRUMBS STYLING ===== */
:deep(.v-breadcrumbs-item--disabled) {
  opacity: 0.6;
}

:deep(.v-breadcrumbs) {
  font-size: 13px !important;
}

/* ===== REMOVE GREEN SCROLLBAR ===== */
:global(*) {
  scrollbar-color: #cbd5e1 transparent !important;
}

:global(*::-webkit-scrollbar-thumb) {
  background: #cbd5e1 !important;
}

:global(*::-webkit-scrollbar-thumb:hover) {
  background: #94a3b8 !important;
}
</style>
