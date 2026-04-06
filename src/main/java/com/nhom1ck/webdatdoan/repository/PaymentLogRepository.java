package com.pdq.repository;

import com.pdq.entity.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {
    List<PaymentLog> findByOrderOrderIdOrderByCreatedAtDesc(Long orderId);
    Optional<PaymentLog> findByTransactionId(String transactionId);
    void deleteByOrderOrderId(Long orderId);
}
