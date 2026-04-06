package com.nhom1ck.webdatdoan.service;

import java.io.UnsupportedEncodingException;

import org.springframework.stereotype.Service;

import com.nhom1ck.webdatdoan.entity.Order;

@Service
public class VNPayService {

    public String createPaymentUrl(Order order, String ipAddress) throws UnsupportedEncodingException {
        if (order == null || order.getOrderId() == null) {
            throw new UnsupportedEncodingException("Invalid order data");
        }

        String clientIp = (ipAddress == null || ipAddress.isBlank()) ? "0.0.0.0" : ipAddress;
        return "/api/payments/vnpay/mock?orderId=" + order.getOrderId() + "&ip=" + clientIp;
    }
}
