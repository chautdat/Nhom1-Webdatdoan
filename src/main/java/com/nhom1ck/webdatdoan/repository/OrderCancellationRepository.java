package com.nhom1ck.webdatdoan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nhom1ck.webdatdoan.entity.OrderCancellation;

@Repository
public interface OrderCancellationRepository extends JpaRepository<OrderCancellation, Long> {
    Optional<OrderCancellation> findByOrderOrderId(Long orderId);
    void deleteByOrderOrderId(Long orderId);
}
