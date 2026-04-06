package com.pdq.vnpaydemo;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment")
public class VnPayController {

    private final VnPayService vnPayService;

    public VnPayController(VnPayService vnPayService) {
        this.vnPayService = vnPayService;
    }

    // Body: { "amount": 510000, "orderInfo": "Thanh toan don hang #123" }
    @PostMapping("/vnpay-create")
    public ResponseEntity<Map<String, Object>> createPayment(
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {

        long amount = Long.parseLong(body.getOrDefault("amount", "0").toString());
        String orderInfo = body.getOrDefault("orderInfo", "Thanh toan don hang").toString();
        String clientIp = getClientIp(request);
        String orderId = generateTxnRef();

        String paymentUrl = vnPayService.createPaymentUrl(amount, clientIp, orderInfo, orderId);

        Map<String, Object> resp = new HashMap<>();
        resp.put("paymentUrl", paymentUrl);
        resp.put("orderId", orderId);
        return ResponseEntity.ok(resp);
    }

    // VNPay callback redirect
    @GetMapping("/vnpay-return")
    public void vnpayReturn(@RequestParam Map<String, String> params,
                            HttpServletResponse response) throws Exception {

        boolean valid = vnPayService.validateSignature(params);
        String responseCode = params.getOrDefault("vnp_ResponseCode", "99");
        String txnRef = params.getOrDefault("vnp_TxnRef", "");
        String amount = params.getOrDefault("vnp_Amount", "0"); // đã *100

        boolean success = valid && "00".equals(responseCode);

        String redirectUrl = "http://localhost:5173/payment-result"
                + "?code=" + responseCode
                + "&orderId=" + txnRef
                + "&amount=" + amount
                + "&valid=" + valid
                + "&success=" + success;

        response.sendRedirect(redirectUrl);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    // VNPay khuyến nghị vnp_TxnRef là chuỗi số, độ dài ngắn (8-10 ký tự). Dùng ngẫu nhiên để tránh quá dài.
    private String generateTxnRef() {
        long now = System.currentTimeMillis();
        String nano = String.valueOf(now % 1_000_000_000L);
        String base = nano + (int)(Math.random() * 10000);
        // Lấy tối đa 12 ký tự để tránh bị từ chối do quá dài
        return base.substring(0, Math.min(base.length(), 12));
    }
}
