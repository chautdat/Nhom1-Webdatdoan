package com.pdq.dto.websocket;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdate {
    private Long orderId;
    private String orderCode;
    private String status;
    private String message;
    private LocalDateTime timestamp;
    private String customerEmail;
}
