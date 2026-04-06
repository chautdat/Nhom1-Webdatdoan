package com.nhom1ck.webdatdoan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nhom1ck.webdatdoan.entity.PaymentLog;

@Repository
public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {
    void deleteByOrderOrderId(Long orderId);
}
