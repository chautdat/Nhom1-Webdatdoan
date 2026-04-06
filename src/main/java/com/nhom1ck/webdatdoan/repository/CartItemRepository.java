package com.nhom1ck.webdatdoan.repository;

import com.nhom1ck.webdatdoan.entity.Cart;
import com.nhom1ck.webdatdoan.entity.CartItem;
import com.nhom1ck.webdatdoan.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    
    void deleteByCart(Cart cart);
}