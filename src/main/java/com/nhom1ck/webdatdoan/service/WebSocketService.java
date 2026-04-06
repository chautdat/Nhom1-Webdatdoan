package com.nhom1ck.webdatdoan.service;

import com.nhom1ck.webdatdoan.dto.OrderWebSocketMessage;
import com.nhom1ck.webdatdoan.dto.websocket.OrderStatusUpdate;
import com.nhom1ck.webdatdoan.entity.Order;
import com.nhom1ck.webdatdoan.entity.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebSocketService {

    private static final Logger log = LoggerFactory.getLogger(WebSocketService.class);
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastNewOrder(Order order) {
        OrderWebSocketMessage payload = buildOrderMessage(order, "Đơn hàng mới");
        messagingTemplate.convertAndSend("/topic/orders", payload);
        log.info("🔔 Broadcast new order {}", order.getOrderId());
    }

    public void broadcastOrderStatusUpdate(Order order, String oldStatus) {
        String message = "Trạng thái đổi từ " + oldStatus + " → " + order.getOrderStatus().name();
        OrderWebSocketMessage payload = buildOrderMessage(order, message);
        messagingTemplate.convertAndSend("/topic/orders/status", payload);
        log.info("🔔 Broadcast status change for order {}", order.getOrderId());
    }

    public void sendKitchenNotification(Long orderId, String action, String details) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", orderId);
        payload.put("action", action);
        payload.put("details", details);
        payload.put("timestamp", System.currentTimeMillis());
        messagingTemplate.convertAndSend("/topic/kitchen", payload);
        log.info("👩‍🍳 Sent kitchen notification for order {}", orderId);
    }

    public void sendOrderUpdate(Long orderId, String orderCode, String status, String customerEmail) {
        OrderStatusUpdate update = new OrderStatusUpdate(
                orderId,
                orderCode,
                status,
                getStatusMessage(status),
                LocalDateTime.now(),
                customerEmail
        );

        messagingTemplate.convertAndSend("/topic/admin/orders", update);

        log.info("✅ WebSocket: Order #{} | Status: {}", orderCode, status);
    }

    private String getStatusMessage(String status) {
        if (status == null) {
            return "Trạng thái đơn hàng đã thay đổi";
        }
        return switch (status.toUpperCase()) {
            case "PENDING" -> "Đơn hàng đang chờ xác nhận";
            case "CONFIRMED" -> "Đơn hàng đã được xác nhận";
            case "PREPARING" -> "Đang chuẩn bị món ăn";
            case "SHIPPING" -> "Đơn hàng đang được giao";
            case "DELIVERED" -> "Đơn hàng đã được giao thành công";
            case "CANCELLED" -> "Đơn hàng đã bị hủy";
            default -> "Trạng thái đơn hàng đã thay đổi";
        };
    }

    private OrderWebSocketMessage buildOrderMessage(Order order, String message) {
        List<Map<String, Object>> itemList = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                Map<String, Object> map = new HashMap<>();
                map.put("productId", item.getProduct().getProductId());
                map.put("name", item.getProductName());
                map.put("quantity", item.getQuantity());
                map.put("price", item.getPrice());
                map.put("subtotal", item.getSubtotal());
                itemList.add(map);
            }
        }

        String userName = null;
        if (order.getUser() != null) {
            userName = order.getUser().getFullName();
            if (userName == null || userName.isBlank()) {
                userName = order.getUser().getUsername();
            }
        }

        OrderWebSocketMessage payload = new OrderWebSocketMessage();
        payload.setOrderId(order.getOrderId());
        payload.setUserId(order.getUser() != null ? order.getUser().getUserId() : null);
        payload.setUserName(userName);
        payload.setStatus(order.getOrderStatus() != null ? order.getOrderStatus().name() : null);
        payload.setTotalAmount(order.getTotalAmount());
        payload.setPaymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null);
        payload.setItems(itemList);
        payload.setMessage(message);
        payload.setTimestamp(System.currentTimeMillis());
        return payload;
    }
}
