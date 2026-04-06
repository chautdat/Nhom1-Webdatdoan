package com.pdq.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pdq.entity.OrderCancellation;

@Repository
public interface OrderCancellationRepository extends JpaRepository<OrderCancellation, Long> {
    Optional<OrderCancellation> findByOrderOrderId(Long orderId);
    void deleteByOrderOrderId(Long orderId);
}
