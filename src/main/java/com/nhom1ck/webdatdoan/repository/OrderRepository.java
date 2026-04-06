package com.pdq.repository;

import com.pdq.entity.Order;
import com.pdq.entity.OrderStatus;
import com.pdq.entity.PaymentMethod;
import com.pdq.entity.PaymentStatus;
import com.pdq.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    Page<Order> findByUser(User user, Pageable pageable);
    
    Page<Order> findByOrderStatus(OrderStatus status, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.orderStatus = :status AND DATE(o.createdAt) = CURRENT_DATE")
    List<Order> findTodayOrdersByStatus(@Param("status") OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE DATE(o.createdAt) = CURRENT_DATE")
    List<Order> findTodayOrders();
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = :status")
    long countByOrderStatus(@Param("status") OrderStatus status);

    List<Order> findByPaymentStatus(PaymentStatus paymentStatus);

    List<Order> findByPaymentStatusAndCreatedAtBetween(
            PaymentStatus paymentStatus,
            LocalDateTime dateFrom,
            LocalDateTime dateTo);

    List<Order> findByPaymentStatusAndCreatedAtAfter(
            PaymentStatus paymentStatus,
            LocalDateTime dateFrom);

    List<Order> findByPaymentStatusAndCreatedAtBefore(
            PaymentStatus paymentStatus,
            LocalDateTime dateTo);

    List<Order> findByPaymentStatusAndPaymentExpiresAtBefore(
            PaymentStatus paymentStatus,
            LocalDateTime expiresAt);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE " +
           "o.paymentStatus = 'paid' AND " +
           "o.createdAt >= :startDate AND o.createdAt <= :endDate")
    BigDecimal getTotalRevenue(@Param("startDate") LocalDateTime startDate, 
                               @Param("endDate") LocalDateTime endDate);

    // ========================================
    // Revenue by payment method and date range
    // ========================================
    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o " +
           "WHERE o.orderStatus = com.pdq.entity.OrderStatus.delivered " +
           "AND o.paymentStatus = com.pdq.entity.PaymentStatus.paid " +
           "AND o.paymentMethod = :paymentMethod " +
           "AND (:dateFrom IS NULL OR o.createdAt >= :dateFrom) " +
           "AND (:dateTo IS NULL OR o.createdAt <= :dateTo)")
    BigDecimal sumRevenueByPaymentMethodAndDateRange(
        @Param("paymentMethod") PaymentMethod paymentMethod,
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo
    );

    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o " +
           "WHERE o.orderStatus = com.pdq.entity.OrderStatus.delivered " +
           "AND o.paymentStatus = com.pdq.entity.PaymentStatus.paid " +
           "AND (:dateFrom IS NULL OR o.createdAt >= :dateFrom) " +
           "AND (:dateTo IS NULL OR o.createdAt <= :dateTo)")
    BigDecimal sumTotalRevenueByDateRange(
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo
    );

    // ========================================
    // Count orders by payment method and date range
    // ========================================
    @Query("SELECT COUNT(o) FROM Order o " +
           "WHERE o.orderStatus = com.pdq.entity.OrderStatus.delivered " +
           "AND o.paymentStatus = com.pdq.entity.PaymentStatus.paid " +
           "AND o.paymentMethod = :paymentMethod " +
           "AND (:dateFrom IS NULL OR o.createdAt >= :dateFrom) " +
           "AND (:dateTo IS NULL OR o.createdAt <= :dateTo)")
    Long countOrdersByPaymentMethodAndDateRange(
        @Param("paymentMethod") PaymentMethod paymentMethod,
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo
    );

    @Query("SELECT COUNT(o) FROM Order o " +
           "WHERE o.orderStatus = com.pdq.entity.OrderStatus.delivered " +
           "AND o.paymentStatus = com.pdq.entity.PaymentStatus.paid " +
           "AND (:dateFrom IS NULL OR o.createdAt >= :dateFrom) " +
           "AND (:dateTo IS NULL OR o.createdAt <= :dateTo)")
    Long countTotalOrdersByDateRange(
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo
    );

    // ========================================
    // Completed orders with items for product sales
    // ========================================
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.items items " +
           "LEFT JOIN FETCH items.product " +
           "WHERE o.orderStatus = com.pdq.entity.OrderStatus.delivered " +
           "AND o.paymentStatus = com.pdq.entity.PaymentStatus.paid " +
           "AND (:dateFrom IS NULL OR o.createdAt >= :dateFrom) " +
           "AND (:dateTo IS NULL OR o.createdAt <= :dateTo)")
    List<Order> findCompletedOrdersWithItemsByDateRange(
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo
    );

    @Query("SELECT o FROM Order o WHERE o.paymentStatus = com.pdq.entity.PaymentStatus.paid " +
           "AND o.orderStatus <> com.pdq.entity.OrderStatus.cancelled " +
           "AND o.createdAt >= :startTime AND o.createdAt <= :endTime")
    List<Order> findPaidOrdersInTimeRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    List<Order> findByUserUserIdOrderByCreatedAtDesc(Long userId);
}
