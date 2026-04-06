package com.pdq.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VNPayConfig {
    
    @Value("${vnpay.tmn-code}")
    private String tmnCode;
    
    @Value("${vnpay.hash-secret}")
    private String hashSecret;
    
    @Value("${vnpay.url}")
    private String vnpayUrl;
    
    // ✅ FIX: Đọc từ vnpay.return-url trong application.properties
    @Value("${vnpay.return-url}")
    private String returnUrl;
    
    @Value("${vnpay.api-url}")
    private String apiUrl;

    public String getTmnCode() {
        return tmnCode;
    }

    public String getHashSecret() {
        return hashSecret;
    }

    public String getVnpayUrl() {
        return vnpayUrl;
    }

    public String getReturnUrl() {
        System.out.println("🔍 VNPay Return URL: " + returnUrl);
        return returnUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }
}