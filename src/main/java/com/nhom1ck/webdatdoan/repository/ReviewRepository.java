package com.pdq.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pdq.entity.Product;
import com.pdq.entity.Review;
import com.pdq.entity.ReviewStatus;
import com.pdq.entity.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    Page<Review> findByProductAndStatus(Product product, ReviewStatus status, Pageable pageable);
    
    Page<Review> findByUser(User user, Pageable pageable);
    
    Page<Review> findByStatus(ReviewStatus status, Pageable pageable);
    
    Optional<Review> findByUserAndProduct(User user, Product product);
    
    boolean existsByUserAndProduct(User user, Product product);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product = :product AND r.status = 'approved'")
    Double getAverageRating(@Param("product") Product product);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product = :product AND r.status = 'approved'")
    Long countApprovedReviews(@Param("product") Product product);
}