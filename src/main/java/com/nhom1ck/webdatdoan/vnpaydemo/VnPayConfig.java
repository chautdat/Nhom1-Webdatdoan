package com.pdq.vnpaydemo;

public class VnPayConfig {

    // Dùng sandbox chung của VNPay (demo)
    public static final String VNP_TMN_CODE = "2QXUI4J4";
    public static final String VNP_HASH_SECRET = "SECRETKEYFORYOURPROJECT";
    public static final String VNP_PAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    // Cổng backend hiện đang chạy 3000, cần trùng để callback được
    public static final String VNP_RETURN_URL = "http://localhost:3000/api/v1/payment/vnpay-return";

    public static final String VNP_VERSION = "2.1.0";
    public static final String VNP_COMMAND = "pay";
    public static final String VNP_CURRENCY_CODE = "VND";
    public static final String VNP_LOCALE = "vn";

    private VnPayConfig() {}
}
