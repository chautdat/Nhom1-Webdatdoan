package com.pdq.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pdq.dto.common.ApiResponse;
import com.pdq.dto.order.CancelOrderRequest;
import com.pdq.dto.order.CreateOrderRequest;
import com.pdq.dto.order.OrderResponse;
import com.pdq.dto.order.UpdateOrderStatusRequest;
import com.pdq.exception.BadRequestException;
import com.pdq.service.OrderService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ========================================
    // CUSTOMER/USER ENDPOINTS
    // ========================================

    /**
     * CREATE ORDER (VNPay + Cash)
     */
    @PostMapping("/orders")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createOrder(
            @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpRequest) {

        String ipAddress = getClientIp(httpRequest);

        Map<String, Object> result =
                orderService.createOrder(userDetails.getUsername(), request, ipAddress);

        boolean requiresPayment =
                Boolean.TRUE.equals(result.get("requiresPayment"));

        return ResponseEntity.ok(
            ApiResponse.success(
                requiresPayment
                        ? "Order created successfully. Please complete payment."
                        : "Order created successfully",
                result
            )
        );
    }

    /**
     * GET ORDER BY ID
     */
    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails) {

        OrderResponse order =
                orderService.getOrderById(orderId, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success("Order retrieved successfully", order));
    }

    /**
     * GET ORDER BY NUMBER
     */
    @GetMapping("/orders/number/{orderNumber}")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByNumber(
            @PathVariable String orderNumber,
            @AuthenticationPrincipal UserDetails userDetails) {

        OrderResponse order =
                orderService.getOrderByNumber(orderNumber, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success("Order retrieved successfully", order));
    }

    /**
     * GET MY ORDERS (User)
     */
    @GetMapping("/orders/my-orders")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<OrderResponse> orders =
                orderService.getOrdersByUsername(userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders));
    }

    /**
     * CANCEL ORDER
     */
    @PostMapping("/orders/{orderId}/cancel")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody CancelOrderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        OrderResponse order = orderService.cancelOrder(orderId, userDetails.getUsername(), request);

        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", order));
    }

    /**
     * RETRY PAYMENT
     */
    @PostMapping("/orders/{orderId}/retry-payment")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> retryPayment(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpRequest) {

        String paymentMethod = request.get("paymentMethod");
        String ipAddress = getClientIp(httpRequest);

        Map<String, Object> result = orderService.retryPayment(
                orderId,
                userDetails.getUsername(),
                paymentMethod,
                ipAddress
        );

        return ResponseEntity.ok(ApiResponse.success("Payment URL created", result));
    }

    /**
     * CONVERT TO COD
     */
    @PostMapping("/orders/{orderId}/convert-to-cod")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> convertToCOD(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails) {

        OrderResponse order = orderService.convertToCOD(orderId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Converted to COD", order));
    }

    // ========================================
    // ADMIN ENDPOINTS
    // ========================================

    /**
     * UPDATE ORDER STATUS (Admin) - Fallback endpoint for compatibility
     */
    @PatchMapping("/orders/{orderId}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {

        System.out.println("🔄 Fallback endpoint called - redirecting to admin handler");
        String newStatus = request.get("status");
        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new BadRequestException("Status is required");
        }

        UpdateOrderStatusRequest statusRequest = new UpdateOrderStatusRequest();
        statusRequest.setStatus(newStatus.toLowerCase().trim());

        OrderResponse order = orderService.updateOrderStatus(orderId, statusRequest);
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", order));
    }

    // ========================================
    // HELPER METHODS
    // ========================================

    /**
     * Helper: Get Client IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip != null ? ip : "0.0.0.0";
    }
}
