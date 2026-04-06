<template>
  <div id="app">
    <!-- ✅ Nếu là layout admin -->
    <div v-if="isAdminLayout">
      <router-view />
    </div>

    <!-- ✅ Nếu là layout người dùng -->
    <div v-else-if="isUserLayout">
      <NavBar />
      <div class="auth-wrapper">
        <div class="auth-inner">
          <router-view />
        </div>
      </div>
      <FooterComponent />
    </div>

    <!-- ✅ Nếu là layout trống (ví dụ: trang login admin) -->
    <div v-else>
      <router-view />
    </div>
  </div>
</template>

<script>
import NavBar from "./components/NavBar.vue";
import FooterComponent from "./components/FooterComponent.vue";
import { mapActions } from "vuex";

export default {
  name: "App",
  components: {
    NavBar,
    FooterComponent,
  },

  mounted() {
    this.getFoodsData();
  },

  computed: {
    // ✅ Kiểm tra loại layout từ route.meta
    layout() {
      return this.$route.meta.layout || "user";
    },

    isAdminLayout() {
      return this.layout === "admin";
    },

    isUserLayout() {
      return this.layout === "user";
    },
  },

  methods: {
    ...mapActions(["getFoodsData"]),
  },
};
</script>

<style>
@import "./assets/css/global_style.css";
</style>
