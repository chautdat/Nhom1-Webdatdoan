package com.pdq.vnpaydemo;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

@Service
public class VnPayService {

    // Tạo URL thanh toán, amount là VND (số nguyên), VNPay cần *100
    public String createPaymentUrl(long amount, String clientIp, String orderInfo, String orderId) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("vnp_Version", VnPayConfig.VNP_VERSION);
            params.put("vnp_Command", VnPayConfig.VNP_COMMAND);
            params.put("vnp_TmnCode", VnPayConfig.VNP_TMN_CODE);
            params.put("vnp_Amount", String.valueOf(amount * 100)); // VNPay yêu cầu *100
            params.put("vnp_CurrCode", VnPayConfig.VNP_CURRENCY_CODE);
            params.put("vnp_TxnRef", orderId);
            params.put("vnp_OrderInfo", orderInfo);
            params.put("vnp_OrderType", "other");
            params.put("vnp_Locale", VnPayConfig.VNP_LOCALE);
            params.put("vnp_ReturnUrl", VnPayConfig.VNP_RETURN_URL);
            params.put("vnp_IpAddr", clientIp);

            // Thời gian tạo và hết hạn
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar cal = Calendar.getInstance();
            params.put("vnp_CreateDate", formatter.format(cal.getTime()));
            cal.add(Calendar.MINUTE, 15);
            params.put("vnp_ExpireDate", formatter.format(cal.getTime()));

            String query = buildSignedQuery(params, VnPayConfig.VNP_HASH_SECRET);
            // Log URL để dễ kiểm tra khi sandbox báo lỗi website
            System.out.println("VNPay payment URL: " + VnPayConfig.VNP_PAY_URL + "?" + query);
            return VnPayConfig.VNP_PAY_URL + "?" + query;
        } catch (Exception ex) {
            throw new RuntimeException("Error while creating VNPay URL", ex);
        }
    }

    // Validate chữ ký từ callback
    public boolean validateSignature(Map<String, String> requestParams) {
        if (requestParams == null || requestParams.isEmpty()) return false;
        String vnpSecureHash = requestParams.get("vnp_SecureHash");
        if (vnpSecureHash == null) return false;

        Map<String, String> params = new HashMap<>(requestParams);
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        String recalculated = hmacSHA512(VnPayConfig.VNP_HASH_SECRET, buildData(params));
        return vnpSecureHash.equalsIgnoreCase(recalculated);
    }

    // ---------- Helper methods ----------

    private String buildSignedQuery(Map<String, String> params, String secret) throws Exception {
        String data = buildData(params);
        String hash = hmacSHA512(secret, data);
        return data + "&vnp_SecureHash=" + hash;
    }

    // Sắp xếp key alphabet, urlencode value, nối key=value&...
    private String buildData(Map<String, String> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String k = keys.get(i);
            String v = params.get(k);
            if (v != null && !v.isEmpty()) {
                sb.append(k)
                  .append("=")
                  .append(URLEncoder.encode(v, StandardCharsets.US_ASCII));
                if (i < keys.size() - 1) sb.append("&");
            }
        }
        return sb.toString();
    }

    // HmacSHA512
    private String hmacSHA512(String secret, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(key);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating HMAC SHA512", e);
        }
    }
}
