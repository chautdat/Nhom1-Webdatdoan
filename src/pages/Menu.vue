<template>
  <div class="menu-page">
    <div class="page-header">
      <div class="header-content">
        <span class="header-badge">THỰC ĐƠN</span>
        <h1 class="page-title">Món ăn đặc biệt của chúng tôi</h1>
        <p class="page-subtitle">
          Khám phá các món ăn ngon được chế biến từ nguyên liệu tươi mới nhất
        </p>
      </div>
    </div>

    <div class="menu-container">
      <aside class="filter-sidebar">
        <div class="filter-header">
          <h3>🔍 Bộ lọc</h3>
          <button class="btn-clear" @click="resetFilters">Xóa tất cả</button>
        </div>

        <div class="filter-section">
          <label class="filter-label">Tìm kiếm</label>
          <div class="search-wrapper">
            <i class="fas fa-search search-icon"></i>
            <input
              type="text"
              class="search-input"
              v-model.trim="filters.keyword"
              placeholder="Tìm món ăn..."
            />
          </div>
        </div>

        <div class="filter-section">
          <label class="filter-label">Danh mục</label>
          <div class="category-buttons">
            <button
              v-for="c in categoriesComputed"
              :key="c.id"
              class="category-btn"
              :class="{ active: filters.category === c.id }"
              @click="selectCategory(c.id)"
            >
              <span class="category-icon">{{ c.icon }}</span>
              <span class="category-text">{{ c.label }}</span>
              <span class="category-count">{{ c.count }}</span>
            </button>
          </div>
        </div>

        <div class="filter-section">
          <label class="filter-label">Khoảng giá</label>
          <div class="price-buttons">
            <button
              v-for="p in priceVN"
              :key="p.id"
              class="price-btn"
              :class="{ active: filters.price === p.id }"
              @click="selectPrice(p.id)"
            >
              {{ p.label }}
            </button>
          </div>
        </div>

        <div class="filter-section availability-row">
          <label class="filter-label">Tình trạng</label>
          <label class="switch">
            <input type="checkbox" v-model="filters.onlyAvailable" />
            <span class="slider"></span>
            <span class="switch-label">Chỉ hiển thị món còn bán</span>
          </label>
        </div>

        <div class="filter-stats">
          <p>
            Tìm thấy <strong>{{ filteredFoods.length }}</strong> món ăn
          </p>
        </div>
      </aside>

      <main class="menu-content">
        <div class="quick-tabs">
          <button
            v-for="c in categoriesComputed"
            :key="c.id"
            class="quick-tab"
            :class="{ active: filters.category === c.id }"
            @click="selectCategory(c.id)"
          >
            <span>{{ c.icon }}</span>
            <span>{{ c.label }}</span>
          </button>
          <div class="sort-wrapper">
            <label for="sortSelect">Sắp xếp</label>
            <select id="sortSelect" v-model="filters.sort">
              <option value="default">Mới nhất</option>
              <option value="priceAsc">Giá thấp → cao</option>
              <option value="priceDesc">Giá cao → thấp</option>
              <option value="nameAsc">Tên A → Z</option>
            </select>
          </div>
        </div>

        <div v-if="activeChips.length" class="active-chips">
          <span
            v-for="chip in activeChips"
            :key="chip.id"
            class="chip"
            @click="chip.action()"
            >{{ chip.label }} ✕</span
          >
          <button class="chip-reset" @click="resetFilters">Xóa hết</button>
        </div>

        <div v-if="currentPageItems.length" class="products-grid">
          <div
            v-for="product in currentPageItems"
            :key="product.productId"
            class="product-card"
            :class="{ 'out-of-stock': !product.isAvailable }"
          >
            <div v-if="!product.isAvailable" class="sold-out-badge">
              🔴 TẠM HẾT
            </div>
            <div class="card-image" @click="openQuickView(product.productId)">
              <img
                :src="imagePath(product.imageUrl)"
                :alt="product.productName"
              />
              <div class="card-badge">{{ getCategoryName(product) }}</div>
            </div>
            <div class="card-content">
              <h3 class="product-name">{{ product.productName }}</h3>
              <p v-if="product.shortDescription" class="product-desc">
                {{ product.shortDescription.substring(0, 50)
                }}{{ product.shortDescription.length > 50 ? "..." : "" }}
              </p>
              <div class="card-footer">
                <div class="product-price">
                  {{ formatPrice(product.price) }}
                </div>
                <button
                  class="btn-add-cart"
                  :disabled="!product.isAvailable"
                  @click="openQuickView(product.productId)"
                >
                  <i class="fas fa-shopping-cart"></i>
                  <span v-if="product.isAvailable">Thêm vào giỏ</span>
                  <span v-else>Hết hàng</span>
                </button>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <div class="empty-icon">🍽️</div>
          <h3>Không tìm thấy món ăn</h3>
          <p>Vui lòng thử lại với bộ lọc khác</p>
          <button class="btn-reset-filter" @click="resetFilters">
            Xóa bộ lọc
          </button>
        </div>

        <div v-if="pages > 1" class="pagination">
          <button class="pagination-btn" :disabled="page === 0" @click="page--">
            <i class="fas fa-chevron-left"></i>
            <span>Trước</span>
          </button>
          <div class="pagination-numbers">
            <button
              v-for="i in visiblePages"
              :key="i"
              class="page-number"
              :class="{ active: page === i - 1 }"
              @click="page = i - 1"
            >
              {{ i }}
            </button>
          </div>
          <button
            class="pagination-btn"
            :disabled="page >= pages - 1"
            @click="page++"
          >
            <span>Sau</span>
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </main>
    </div>

    <QuickView
      :showQuickView="showQuickView"
      :productId="quickFoodId"
      @close="closeQuickView"
    />
  </div>
</template>

<script>
import api from "@/axios";
import QuickView from "@/components/QuickView.vue";

export default {
  name: "MenuPage",
  components: { QuickView },
  data() {
    return {
      foods: [],
      categories: [],
      filters: {
        keyword: "",
        category: "all",
        price: "",
        onlyAvailable: false,
        sort: "default",
      },
      priceVN: [
        { id: "<20", label: "< 20.000đ", min: 0, max: 19999 },
        { id: "20-50", label: "20k - 50k", min: 20000, max: 50000 },
        { id: "50-100", label: "50k - 100k", min: 50000, max: 100000 },
        { id: "100-150", label: "100k - 150k", min: 100000, max: 150000 },
        { id: ">150", label: "> 150.000đ", min: 150001, max: Infinity },
      ],
      page: 0,
      perPage: 9,
      showQuickView: false,
      quickFoodId: null,
    };
  },
  computed: {
    categoriesComputed() {
      const counts = {};
      this.foods.forEach((f) => {
        const key = this.getCategoryKey(f);
        const label = this.getCategoryLabel(f);
        if (!counts[key]) counts[key] = { count: 0, labelOverride: label };
        counts[key].count += 1;
        if (label) counts[key].labelOverride = label;
      });

      const categories = [
        { id: "all", label: "Tất cả", icon: "🍽️", count: this.foods.length },
      ];

      const knownCategoryIds = new Set();
      this.categories.forEach((cat) => {
        const id = String(cat.categoryId);
        knownCategoryIds.add(id);
        const info = counts[id] || {};
        const label = cat.categoryName || info.labelOverride || `Danh mục ${id}`;
        categories.push({
          id,
          label,
          icon: this.getCategoryIcon(label),
          count: info.count || 0,
        });
      });

      Object.keys(counts).forEach((id) => {
        if (id === "all" || knownCategoryIds.has(String(id))) return;
        const label = counts[id].labelOverride || "Khác";
        categories.push({
          id,
          label,
          icon: this.getCategoryIcon(label),
          count: counts[id].count || 0,
        });
      });

      return categories;
    },
    filteredFoods() {
      const kw = this.filters.keyword.toLowerCase().trim();
      const priceRange = this.priceVN.find((p) => p.id === this.filters.price);
      return this.foods
        .map((f) => ({
          ...f,
          _price: Number(f.price) || 0,
          _name: (f.productName || "").toLowerCase(),
          _desc: (f.shortDescription || f.description || "").toLowerCase(),
          _categoryKey: this.getCategoryKey(f),
        }))
        .filter((f) => {
          const keywordOk = !kw || f._name.includes(kw) || f._desc.includes(kw);
          const categoryOk =
            this.filters.category === "all" ||
            String(f._categoryKey) === String(this.filters.category);
          const priceOk =
            !priceRange ||
            (f._price >= priceRange.min &&
              f._price <= (priceRange.max ?? Infinity));
          const stockOk =
            !this.filters.onlyAvailable || f.isAvailable !== false;
          return keywordOk && categoryOk && priceOk && stockOk;
        });
    },
    sortedFoods() {
      const list = [...this.filteredFoods];
      switch (this.filters.sort) {
        case "priceAsc":
          return list.sort((a, b) => a._price - b._price);
        case "priceDesc":
          return list.sort((a, b) => b._price - a._price);
        case "nameAsc":
          return list.sort((a, b) => a._name.localeCompare(b._name));
        default:
          return list;
      }
    },
    pages() {
      return 1;
    },
    currentPageItems() {
      return this.sortedFoods;
    },
    visiblePages() {
      const total = this.pages;
      const current = this.page + 1;
      const windowSize = 5;
      if (total <= windowSize)
        return Array.from({ length: total }, (_, i) => i + 1);
      let start = Math.max(1, current - 2);
      let end = Math.min(total, start + windowSize - 1);
      if (end - start < windowSize - 1)
        start = Math.max(1, end - windowSize + 1);
      return Array.from({ length: end - start + 1 }, (_, i) => start + i);
    },
    activeChips() {
      const chips = [];
      if (this.filters.keyword)
        chips.push({
          id: "kw",
          label: `Từ khóa: "${this.filters.keyword}"`,
          action: () => (this.filters.keyword = ""),
        });
      if (this.filters.category !== "all") {
        const cat = this.categoriesComputed.find(
          (c) => c.id === this.filters.category
        );
        chips.push({
          id: "cat",
          label: `Danh mục: ${cat?.label || this.filters.category}`,
          action: () => (this.filters.category = "all"),
        });
      }
      if (this.filters.price) {
        const p = this.priceVN.find((p) => p.id === this.filters.price);
        chips.push({
          id: "price",
          label: `Giá: ${p?.label || this.filters.price}`,
          action: () => (this.filters.price = ""),
        });
      }
      if (this.filters.onlyAvailable)
        chips.push({
          id: "stock",
          label: "Chỉ còn hàng",
          action: () => (this.filters.onlyAvailable = false),
        });
      return chips;
    },
  },
  mounted() {
    this.fetchMenuData();
  },
  methods: {
    async fetchMenuData() {
      await Promise.all([this.fetchCategories(), this.fetchFoods()]);
    },
    getCategoryIcon(name) {
      const norm = this.normalizeLabel(name || "");
      if (norm.includes("chinh") || norm.includes("main")) return "🍖";
      if (norm.includes("phu") || norm.includes("side")) return "🥗";
      if (
        norm.includes("uong") ||
        norm.includes("drink") ||
        norm.includes("nuoc")
      )
        return "🥤";
      if (norm.includes("trang mieng") || norm.includes("dessert")) return "🍰";
      if (norm.includes("topping")) return "🧂";
      return "🍽️";
    },
    normalizeLabel(str) {
      if (!str) return "";
      return str
        .toString()
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .replace(/đ/g, "d")
        .trim();
    },
    async fetchCategories() {
      try {
        const res = await api.get("/categories");
        const raw = res.data?.data || res.data || [];
        this.categories = Array.isArray(raw) ? raw : [];
      } catch (e) {
        console.error("❌ Error fetching categories:", e);
        this.categories = [];
      }
    },
    getCategoryKey(item) {
      const id =
        item && typeof item === "object"
          ? item.categoryId || item.category?.categoryId
          : item;
      const name =
        item && typeof item === "object"
          ? item.categoryName || item.category?.categoryName
          : null;
      if (id !== null && id !== undefined && id !== "") return String(id);
      if (name) {
        const match = this.categories.find(
          (cat) => this.normalizeLabel(cat.categoryName) === this.normalizeLabel(name)
        );
        if (match) return String(match.categoryId);
      }
      return "uncategorized";
    },
    getCategoryLabel(item) {
      if (item && typeof item === "object" && item.categoryName)
        return item.categoryName;
      if (item && typeof item === "object" && item.category?.categoryName)
        return item.category.categoryName;
      const key = this.getCategoryKey(item);
      const fromApi = this.categories.find(
        (cat) => String(cat.categoryId) === String(key)
      );
      return fromApi?.categoryName || null;
    },
    async fetchFoods() {
      try {
        const res = await api.get("/products", {
          params: { includeUnavailable: true, page: 0, size: 200 },
        });
        this.foods = res.data?.data?.content || res.data?.data || [];
      } catch (e) {
        console.error("❌ Error fetching products:", e);
        alert("Không thể tải danh sách món ăn!");
      }
    },
    imagePath(src) {
      if (!src) return "/images/notfound.png";
      const backendUrl = "http://localhost:3000";
      if (src.startsWith("http")) return src;
      if (src.startsWith("/uploads")) return `${backendUrl}${src}`;
      return `${backendUrl}/uploads/${src}`;
    },
    formatPrice(v) {
      return Number(v).toLocaleString("vi-VN") + "đ";
    },
    getCategoryName(item) {
      const key = this.getCategoryKey(item);
      const labelFromItem = this.getCategoryLabel(item);
      const cat = this.categoriesComputed.find(
        (c) => String(c.id) === String(key)
      );
      return cat?.label || labelFromItem || "Khác";
    },
    selectCategory(id) {
      this.filters.category = id;
      this.page = 0;
    },
    selectPrice(id) {
      this.filters.price = this.filters.price === id ? "" : id;
      this.page = 0;
    },
    openQuickView(id) {
      if (!id) return;
      this.quickFoodId = Number(id);
      this.showQuickView = true;
    },
    closeQuickView() {
      this.showQuickView = false;
      this.quickFoodId = null;
    },
    resetFilters() {
      this.filters = {
        keyword: "",
        category: "all",
        price: "",
        onlyAvailable: false,
        sort: "default",
      };
      this.page = 0;
    },
  },
  watch: {
    "filters.category"() {
      this.page = 0;
    },
    "filters.price"() {
      this.page = 0;
    },
    "filters.keyword"() {
      this.page = 0;
    },
    "filters.onlyAvailable"() {
      this.page = 0;
    },
    "filters.sort"() {
      this.page = 0;
    },
    sortedFoods() {
      if (this.page > this.pages - 1) this.page = 0;
    },
  },
};
</script>

<style scoped>
* {
  box-sizing: border-box;
}
.menu-page {
  min-height: 100vh;
  background: #f8f9fa;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  font-size: 17px;
}
.page-header {
  background: linear-gradient(135deg, #00b067 0%, #00d97e 100%);
  padding: 3rem 4%;
  text-align: center;
  color: white;
}
.header-content {
  max-width: 700px;
  margin: 0 auto;
}
.header-badge {
  display: inline-block;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  padding: 0.4rem 1rem;
  border-radius: 16px;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 1.5px;
  margin-bottom: 0.8rem;
}
.page-title {
  font-size: 2.4rem;
  font-weight: 800;
  margin: 0 0 0.8rem 0;
  line-height: 1.2;
}
.page-subtitle {
  font-size: 1.1rem;
  opacity: 0.95;
  margin: 0;
  line-height: 1.5;
}
.menu-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem 4%;
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 2.5rem;
  align-items: start;
}
.filter-sidebar {
  background: white;
  border-radius: 12px;
  padding: 1.3rem;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 1.5rem;
}
.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.2rem;
  padding-bottom: 0.8rem;
  border-bottom: 2px solid #f0f0f0;
}
.filter-header h3 {
  font-size: 1.05rem;
  font-weight: 700;
  color: #333;
  margin: 0;
}
.btn-clear {
  background: none;
  border: none;
  color: #00b067;
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
  padding: 0.3rem 0.7rem;
  border-radius: 6px;
  transition: all 0.3s;
}
.btn-clear:hover {
  background: #e8f6ee;
  color: #009954;
}
.filter-section {
  margin-bottom: 1.3rem;
}
.filter-label {
  display: block;
  font-size: 0.9rem;
  font-weight: 700;
  color: #555;
  margin-bottom: 0.6rem;
}
.search-wrapper {
  position: relative;
}
.search-icon {
  position: absolute;
  left: 0.9rem;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  font-size: 0.85rem;
}
.search-input {
  width: 100%;
  padding: 0.7rem 0.9rem 0.7rem 2.4rem;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  font-size: 0.85rem;
  background: #fafafa;
  transition: all 0.3s;
}
.search-input:focus {
  outline: none;
  border-color: #00b067;
  background: white;
  box-shadow: 0 0 0 3px rgba(0, 176, 103, 0.08);
}
.category-buttons {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
.category-btn {
  display: flex;
  align-items: center;
  gap: 0.7rem;
  padding: 0.7rem 0.9rem;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  background: white;
  font-size: 0.85rem;
  font-weight: 600;
  color: #555;
  cursor: pointer;
  transition: all 0.3s;
  text-align: left;
}
.category-btn:hover {
  border-color: #00b067;
  background: #f0fff8;
  color: #00b067;
}
.category-btn.active {
  border-color: #00b067;
  background: linear-gradient(135deg, #00b067 0%, #00d97e 100%);
  color: white;
}
.category-count {
  margin-left: auto;
  background: rgba(255, 255, 255, 0.2);
  color: inherit;
  padding: 0.15rem 0.5rem;
  border-radius: 8px;
  font-size: 0.7rem;
  font-weight: 700;
}
.category-icon {
  font-size: 1.1rem;
  width: 24px;
  text-align: center;
}
.price-buttons {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
.price-btn {
  padding: 0.7rem 0.9rem;
  border: 2px solid #e0e0e0;
  border-radius: 9px;
  background: white;
  font-size: 0.8rem;
  font-weight: 600;
  color: #555;
  cursor: pointer;
  transition: all 0.3s;
  text-align: left;
}
.price-btn:hover {
  border-color: #27ae60;
  background: #f0fff4;
  color: #27ae60;
}
.price-btn.active {
  border-color: #27ae60;
  background: #27ae60;
  color: white;
}
.availability-row .switch {
  display: inline-flex;
  align-items: center;
  gap: 0.6rem;
  cursor: pointer;
}
.availability-row .slider {
  position: relative;
  width: 42px;
  height: 22px;
  background: #d9d9d9;
  border-radius: 11px;
  transition: 0.2s;
}
.availability-row input {
  display: none;
}
.availability-row input:checked + .slider {
  background: #00b067;
}
.availability-row .slider::after {
  content: "";
  position: absolute;
  top: 3px;
  left: 3px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: white;
  transition: 0.2s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
}
.availability-row input:checked + .slider::after {
  transform: translateX(20px);
}
.availability-row .switch-label {
  font-weight: 600;
  font-size: 0.85rem;
  color: #444;
}
.filter-stats {
  margin-top: 1.2rem;
  padding-top: 1.2rem;
  border-top: 2px solid #f0f0f0;
  text-align: center;
}
.filter-stats p {
  font-size: 0.85rem;
  color: #666;
  margin: 0;
}
.filter-stats strong {
  color: #00b067;
  font-weight: 800;
}
.menu-content {
  min-height: 600px;
}
.quick-tabs {
  display: flex;
  gap: 0.6rem;
  flex-wrap: wrap;
  margin-bottom: 1.6rem;
  padding: 0.9rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
}
.quick-tab {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.6rem 1rem;
  border: none;
  border-radius: 16px;
  background: #f0f0f0;
  color: #555;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}
.quick-tab:hover {
  background: #e0e0e0;
  transform: translateY(-1px);
}
.quick-tab.active {
  background: linear-gradient(135deg, #00b067 0%, #00d97e 100%);
  color: white;
  box-shadow: 0 3px 8px rgba(0, 176, 103, 0.25);
}
.sort-wrapper {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
  font-size: 0.85rem;
  color: #444;
}
.sort-wrapper select {
  padding: 0.45rem 0.8rem;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  background: #f8f8f8;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: 0.2s;
}
.sort-wrapper select:focus {
  outline: none;
  border-color: #00b067;
  box-shadow: 0 0 0 3px rgba(0, 176, 103, 0.08);
}
.active-chips {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-bottom: 0.9rem;
}
.chip {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.4rem 0.8rem;
  border-radius: 10px;
  background: #e8f6ee;
  color: #006c3e;
  font-size: 0.8rem;
  font-weight: 700;
  cursor: pointer;
  transition: 0.2s;
}
.chip:hover {
  background: #d9f2e4;
}
.chip-reset {
  border: none;
  background: none;
  color: #00b067;
  font-size: 0.8rem;
  font-weight: 700;
  cursor: pointer;
}
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 1.5rem;
}
.product-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
}
.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}
.product-card.out-of-stock {
  opacity: 0.6;
}
.product-card.out-of-stock:hover {
  transform: translateY(-2px);
}
.sold-out-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
  padding: 0.4rem 0.8rem;
  border-radius: 10px;
  font-size: 0.75rem;
  font-weight: 800;
  z-index: 5;
  box-shadow: 0 3px 8px rgba(239, 68, 68, 0.35);
}
.card-image {
  position: relative;
  width: 100%;
  height: 180px;
  overflow: hidden;
  background: #f5f5f5;
  cursor: pointer;
}
.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s;
}
.product-card:hover .card-image img {
  transform: scale(1.06);
}
.card-badge {
  position: absolute;
  bottom: 0.8rem;
  left: 0.8rem;
  padding: 0.35rem 0.8rem;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  font-size: 0.7rem;
  font-weight: 700;
  color: #00b067;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}
.card-content {
  padding: 1rem;
  flex: 1;
  display: flex;
  flex-direction: column;
}
.product-name {
  font-size: 1.05rem;
  font-weight: 700;
  color: #333;
  margin: 0 0 0.5rem 0;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-clamp: 2;
  overflow: hidden;
}
.product-desc {
  font-size: 0.85rem;
  color: #777;
  line-height: 1.5;
  margin: 0 0 0.7rem 0;
  flex: 1;
}
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.8rem;
  margin-top: auto;
  padding-top: 0.8rem;
  border-top: 2px solid #f5f5f5;
}
.product-price {
  font-size: 1.1rem;
  font-weight: 800;
  color: #27ae60;
}
.btn-add-cart {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.6rem 1rem;
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, #00b067 0%, #00d97e 100%);
  color: white;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 6px rgba(0, 176, 103, 0.25);
}
.btn-add-cart:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 10px rgba(0, 176, 103, 0.35);
}
.btn-add-cart:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}
.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  background: white;
  border-radius: 12px;
}
.empty-icon {
  font-size: 4rem;
  margin-bottom: 1.2rem;
  opacity: 0.4;
}
.empty-state h3 {
  font-size: 1.25rem;
  color: #333;
  margin: 0 0 0.7rem 0;
}
.empty-state p {
  font-size: 0.9rem;
  color: #777;
  margin: 0 0 1.6rem 0;
}
.btn-reset-filter {
  padding: 0.8rem 1.6rem;
  border: none;
  border-radius: 10px;
  background: #00b067;
  color: white;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}
.btn-reset-filter:hover {
  background: #009954;
  transform: translateY(-1px);
}
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.7rem;
  margin-top: 2.2rem;
  padding: 1.2rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
}
.pagination-btn {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.7rem 1rem;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  background: white;
  color: #555;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}
.pagination-btn:hover:not(:disabled) {
  border-color: #00b067;
  color: #00b067;
  background: #f0fff8;
}
.pagination-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
.pagination-numbers {
  display: flex;
  gap: 0.45rem;
}
.page-number {
  min-width: 36px;
  height: 36px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  background: white;
  color: #555;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}
.page-number:hover {
  border-color: #00b067;
  color: #00b067;
  background: #f0fff8;
}
.page-number.active {
  border-color: #00b067;
  background: linear-gradient(135deg, #00b067 0%, #00d97e 100%);
  color: white;
  box-shadow: 0 2px 6px rgba(0, 176, 103, 0.25);
}
@media (max-width: 1200px) {
  .menu-container {
    grid-template-columns: 250px 1fr;
    gap: 2.2rem;
  }
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }
}
@media (max-width: 992px) {
  .menu-container {
    grid-template-columns: 1fr;
  }
  .filter-sidebar {
    position: static;
  }
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
}
@media (max-width: 768px) {
  .page-header {
    padding: 2.5rem 4%;
  }
  .page-title {
    font-size: 1.8rem;
  }
  .page-subtitle {
    font-size: 0.9rem;
  }
  .menu-container {
    padding: 1.6rem 4%;
  }
  .quick-tabs {
    gap: 0.45rem;
  }
  .quick-tab {
    padding: 0.5rem 0.9rem;
    font-size: 0.8rem;
  }
  .products-grid {
    grid-template-columns: 1fr;
    gap: 1.2rem;
  }
  .pagination {
    flex-wrap: wrap;
  }
}
@media (max-width: 480px) {
  .page-title {
    font-size: 1.5rem;
  }
  .filter-sidebar {
    padding: 1.1rem;
  }
  .card-content {
    padding: 0.9rem;
  }
}
</style>
