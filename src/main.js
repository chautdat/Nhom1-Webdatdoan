import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import "@/axios";

import "vuetify/styles";
import "@mdi/font/css/materialdesignicons.css";
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";

const vuetify = createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: "light",
    themes: {
      light: {
        colors: {
          primary: "#2563eb",
          secondary: "#0ea5e9",
        },
      },
      dark: {
        colors: {
          primary: "#2563eb",
          secondary: "#0ea5e9",
        },
      },
    },
  },
});

createApp(App).use(router).use(store).use(vuetify).mount("#app");
