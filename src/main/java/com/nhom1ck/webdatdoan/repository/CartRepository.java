package com.nhom1ck.webdatdoan.repository;

import com.nhom1ck.webdatdoan.entity.Cart;
import com.nhom1ck.webdatdoan.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    Optional<Cart> findByUser(User user);
    
    void deleteByUser(User user);
}