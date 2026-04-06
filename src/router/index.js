import { createRouter, createWebHashHistory } from "vue-router";
import storage from "@/utils/storage";

// --- PAGE USER ---
import Login from "../pages/Login.vue";
import Register from "../pages/Register.vue";
import Home from "../pages/Home.vue";
import About from "../pages/About.vue";
import Menu from "../pages/Menu.vue";
import Table from "../pages/Table.vue";
import Cart from "../pages/Cart.vue";
import Checkout from "../pages/Checkout.vue";
import Thank from "../pages/Thank.vue";
import MyOrder from "../pages/MyOrder.vue";
import Profile from "../pages/Profile.vue";
import OrderReview from "../pages/OrderReview.vue";
import PaymentReturn from "../pages/PaymentReturn.vue";

// --- ADMIN ---
import AdminLayout from "@/admin/AdminLayout.vue";
import Dashboard from "@/admin/Dashboard.vue";
import MenuManagement from "@/admin/MenuManagement.vue";
import AdminOrders from "@/admin/Orders.vue";
import Categories from "@/admin/Categories.vue";
import Users from "@/admin/Users.vue";
import AdminProfile from "@/admin/AdminProfile.vue";
import CustomerOrderTracking from "@/pages/CustomerOrderTracking.vue";

const routes = [
  // USER LAYOUT
  { path: "/", name: "Home", component: Home, meta: { role: "user" } },
  { path: "/about", component: About, meta: { role: "user" } },
  { path: "/menu", component: Menu, meta: { role: "user" } },
  { path: "/table", component: Table, meta: { role: "user" } },

  {
    path: "/cart",
    component: Cart,
    meta: { role: "user", auth: true },
  },

  {
    path: "/checkout",
    component: Checkout,
    meta: { role: "user", auth: true },
  },

  {
    path: "/thank",
    name: "ThankYou",
    component: Thank,
    meta: { role: "user" },
  },

  {
    path: "/payment/return",
    name: "PaymentReturn",
    component: PaymentReturn,
    meta: { role: "user", auth: true },
  },
  {
    path: "/payment/callback",
    name: "PaymentCallback",
    component: () => import("@/pages/PaymentCallback.vue"),
    meta: { role: "user", auth: true },
  },

  {
    path: "/profile",
    component: Profile,
    meta: { role: "user", auth: true },
  },

  {
    path: "/my-order",
    component: MyOrder,
    meta: { role: "user", auth: true },
  },
  {
    path: "/orders",
    name: "Orders",
    component: () => import("@/pages/Orders.vue"),
    meta: { role: "user", auth: true },
  },

  { path: "/login", name: "Login", component: Login, meta: { free: true } },
  { path: "/register", component: Register, meta: { free: true } },

  {
    path: "/orders/:orderId/review",
    name: "OrderReview",
    component: OrderReview,
    meta: { role: "user", auth: true },
  },

  // ADMIN LAYOUT
  {
    path: "/admin",
    component: AdminLayout,
    meta: { role: "admin", auth: true, layout: "admin" },
    children: [
      { path: "", name: "AdminDashboard", component: Dashboard },
      { path: "dashboard", redirect: { name: "AdminDashboard" } },
      { path: "menu", name: "AdminMenu", component: MenuManagement },
      { path: "orders", name: "AdminOrders", component: AdminOrders },
      { path: "categories", name: "AdminCategories", component: Categories },
      { path: "users", name: "AdminUsers", component: Users },
      { path: "profile", name: "AdminProfile", component: AdminProfile },
    ],
  },

  {
    path: "/orders/:id/track",
    name: "CustomerOrderTracking",
    component: CustomerOrderTracking,
    meta: { role: "user", auth: true },
  },

  { path: "/:pathMatch(.*)*", redirect: "/" },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

// ====================== ROLE GUARD ======================
router.beforeEach((to, from, next) => {
  const token = storage.getToken();
  const user = storage.getUser();
  const role = user?.role || null;

  // TRANG FREE → KHÔNG CẦN LOGIN
  if (to.meta.free) return next();

  // CHƯA LOGIN
  if (!token && to.meta.auth) return next("/login");

  // ROLE ADMIN
  if (role === "ROLE_ADMIN") {
    if (to.meta.role === "admin") return next();
    return next("/admin");
  }

  // ROLE USER
  if (role === "ROLE_CUSTOMER") {
    if (to.meta.role === "user") return next();
    return next("/");
  }

  return next("/login");
});

export default router;
