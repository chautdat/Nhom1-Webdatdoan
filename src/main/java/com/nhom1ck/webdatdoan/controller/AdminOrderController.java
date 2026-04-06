package com.nhom1ck.webdatdoan.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhom1ck.webdatdoan.dto.common.ApiResponse;
import com.nhom1ck.webdatdoan.dto.common.PageResponse;
import com.nhom1ck.webdatdoan.dto.order.OrderResponse;
import com.nhom1ck.webdatdoan.dto.order.UpdateOrderStatusRequest;
import com.nhom1ck.webdatdoan.exception.BadRequestException;
import com.nhom1ck.webdatdoan.service.OrderService;

/**
 * Admin-specific endpoints for orders.
 */
@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * GET ALL ORDERS (Admin)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        System.out.println("📋 Admin fetching orders - page: " + page + ", size: " + size + ", status: " + status);

        PageResponse<OrderResponse> orders = orderService.getAllOrders(page, size, status);

        return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders));
    }

    /**
     * UPDATE ORDER STATUS (Admin) - Main endpoint for frontend
     */
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {

        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("🔄 ADMIN UPDATE ORDER STATUS");
        System.out.println("   Order ID: " + orderId);
        System.out.println("   Request body: " + request);
        System.out.println("═══════════════════════════════════════════════════════");

        String newStatus = request.get("status");
        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new BadRequestException("Status is required");
        }

        UpdateOrderStatusRequest statusRequest = new UpdateOrderStatusRequest();
        statusRequest.setStatus(newStatus.toLowerCase().trim());

        System.out.println("   → New status: " + newStatus);

        OrderResponse order = orderService.updateOrderStatus(orderId, statusRequest);

        System.out.println("✅ Order status updated successfully");
        System.out.println("═══════════════════════════════════════════════════════\n");

        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", order));
    }

    /**
     * CONFIRM CASH PAYMENT (Admin)
     */
    @PutMapping("/{orderId}/confirm-cash")
    public ResponseEntity<ApiResponse<OrderResponse>> confirmCash(@PathVariable Long orderId) {
        
        System.out.println("💵 Admin confirming cash payment for order: " + orderId);
        
        OrderResponse order = orderService.confirmCashPayment(orderId);
        
        return ResponseEntity.ok(ApiResponse.success("Cash payment confirmed", order));
    }

    /**
     * DELETE ORDER (Admin)
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long orderId) {
        
        System.out.println("🗑️ Admin deleting order: " + orderId);
        
        orderService.deleteOrder(orderId);
        
        return ResponseEntity.ok(ApiResponse.success("Order deleted successfully", null));
    }
}