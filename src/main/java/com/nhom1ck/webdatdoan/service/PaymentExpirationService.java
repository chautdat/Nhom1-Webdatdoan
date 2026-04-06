package com.nhom1ck.webdatdoan.service;

import com.nhom1ck.webdatdoan.entity.Order;
import com.nhom1ck.webdatdoan.entity.PaymentLog;
import com.nhom1ck.webdatdoan.entity.PaymentMethod;
import com.nhom1ck.webdatdoan.entity.PaymentStatus;
import com.nhom1ck.webdatdoan.repository.OrderRepository;
import com.nhom1ck.webdatdoan.repository.PaymentLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentExpirationService {

    private final OrderRepository orderRepository;
    private final PaymentLogRepository paymentLogRepository;

    @Scheduled(fixedRate = 300000) // Mỗi 5 phút
    @Transactional
    public void checkExpiredPayments() {
        log.info("🕐 Checking expired payments...");

        LocalDateTime now = LocalDateTime.now();

        List<Order> expiredOrders = orderRepository
                .findByPaymentStatusAndPaymentExpiresAtBefore(
                        PaymentStatus.pending,
                        now
                );

        log.info("Found {} expired orders", expiredOrders.size());

        for (Order order : expiredOrders) {
            if (order.getRetryCount() != null && order.getMaxRetries() != null
                    && order.getRetryCount() >= order.getMaxRetries()) {
                log.info("⏰ Order {} expired - Converting to COD", order.getOrderNumber());

                order.setPaymentMethod(PaymentMethod.cash);
                order.setPaymentStatus(PaymentStatus.pending);
                order.setPaymentExpiresAt(null);

                // Option B: Hủy đơn khi timeout (uncomment nếu cần)
                // order.setOrderStatus(OrderStatus.cancelled);
                // order.setCancelledReason("Payment timeout");

                orderRepository.save(order);

                PaymentLog logEntry = PaymentLog.builder()
                        .order(order)
                        .paymentMethod("CASH")
                        .paymentStatus("AUTO_CONVERTED_TO_COD")
                        .amount(order.getFinalAmount())
                        .retryCount(order.getRetryCount())
                        .build();
                paymentLogRepository.save(logEntry);

                log.info("✅ Order {} converted to COD", order.getOrderNumber());
            }
        }
    }
}
