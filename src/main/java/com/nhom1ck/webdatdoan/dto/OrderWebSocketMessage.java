package com.nhom1ck.webdatdoan.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class OrderWebSocketMessage {
    private Long orderId;
    private Long userId;
    private String userName;
    private String status;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private List<Map<String, Object>> items;
    private String message;
    private Long timestamp;
}
