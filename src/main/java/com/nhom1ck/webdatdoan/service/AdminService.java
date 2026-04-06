package com.pdq.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pdq.dto.admin.StatisticsResponse;
import com.pdq.entity.OrderStatus;
import com.pdq.repository.OrderRepository;
import com.pdq.repository.ProductRepository;
import com.pdq.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class AdminService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public AdminService(OrderRepository orderRepository,
                       UserRepository userRepository,
                       ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public StatisticsResponse getDashboardStatistics() {
        StatisticsResponse response = new StatisticsResponse();

        // Dashboard stats
        StatisticsResponse.DashboardStats stats = new StatisticsResponse.DashboardStats();
        
        stats.setTotalOrders(orderRepository.count());
        stats.setPendingOrders(orderRepository.countByOrderStatus(OrderStatus.pending));
        stats.setCompletedOrders(orderRepository.countByOrderStatus(OrderStatus.delivered));
        stats.setTotalUsers(userRepository.count());
        stats.setTotalProducts(productRepository.count());

        // Total revenue (all paid orders)
        LocalDateTime startOfTime = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime now = LocalDateTime.now();
        BigDecimal totalRevenue = orderRepository.getTotalRevenue(startOfTime, now);
        stats.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // Today revenue
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        BigDecimal todayRevenue = orderRepository.getTotalRevenue(startOfDay, now);
        stats.setTodayRevenue(todayRevenue != null ? todayRevenue : BigDecimal.ZERO);

        response.setDashboardStats(stats);

        // Top products (mock - implement with actual query)
        response.setTopProducts(getTopProducts());

        // Revenue by date (mock - implement with actual query)
        response.setRevenueByDate(getRevenueByDate(7));

        return response;
    }

    private List<StatisticsResponse.TopProduct> getTopProducts() {
        // Implement: Query top 10 products by sold count
        List<StatisticsResponse.TopProduct> topProducts = new ArrayList<>();
        // This is a simplified version - you should implement proper query
        return topProducts;
    }

    private List<StatisticsResponse.RevenueByDate> getRevenueByDate(int days) {
        // Implement: Query revenue for last N days
        List<StatisticsResponse.RevenueByDate> revenueList = new ArrayList<>();
        // This is a simplified version - you should implement proper query
        return revenueList;
    }
}