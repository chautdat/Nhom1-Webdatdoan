const { defineConfig } = require("@vue/cli-service");

module.exports = defineConfig({
  transpileDependencies: true,

  devServer: {
    port: 8080,

    // Fix lỗi Cannot GET /menu khi F5
    historyApiFallback: true,

    // ✅ PROXY QUAN TRỌNG
    proxy: {
      "/api": {
        target: "http://localhost:3000",
        changeOrigin: true,
        secure: false,
        ws: true,
        logLevel: "debug", // Bật log để debug
      },
    },
  },
});
