package com.pdq.dto.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    private DashboardStats dashboardStats;
    private List<TopProduct> topProducts = new ArrayList<>();
    private List<RevenueByDate> revenueByDate = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardStats {
        private Long totalOrders;
        private Long pendingOrders;
        private Long completedOrders;
        private Long totalUsers;
        private Long totalProducts;
        private BigDecimal totalRevenue;
        private BigDecimal todayRevenue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProduct {
        private Long productId;
        private String productName;
        private String imageUrl;
        private Integer soldCount;
        private BigDecimal revenue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueByDate {
        private String date;
        private BigDecimal revenue;
        private Long orderCount;
    }
}