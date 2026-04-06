package com.pdq.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pdq.entity.Category;
import com.pdq.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findByProductSlug(String slug);
    
    Optional<Product> findBySku(String sku);
    
    Page<Product> findByIsAvailableTrue(Pageable pageable);

    List<Product> findByIsAvailableTrue();

    Page<Product> findByCategory(Category category, Pageable pageable);

    List<Product> findByCategoryCategoryIdAndIsAvailableTrue(Integer categoryId);

    List<Product> findByProductNameContainingIgnoreCase(String keyword);
    
    Page<Product> findByIsFeaturedTrue(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE " +
           "(:includeUnavailable = true OR p.isAvailable = true) AND " +
           "(:categoryId IS NULL OR p.category.categoryId = :categoryId) AND " +
           "(:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> searchProducts(
        @Param("includeUnavailable") Boolean includeUnavailable,
        @Param("categoryId") Integer categoryId,
        @Param("keyword") String keyword,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        Pageable pageable
    );
    
    @Query("SELECT p FROM Product p WHERE p.isAvailable = true ORDER BY p.soldCount DESC")
    List<Product> findBestSellers(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity < 10 ORDER BY p.stockQuantity ASC")
    List<Product> findLowStockProducts();
    
    @Modifying
    @Query("UPDATE Product p SET p.viewCount = p.viewCount + 1 WHERE p.productId = :productId")
    void incrementViewCount(@Param("productId") Long productId);
    
    @Modifying
    @Query("UPDATE Product p SET p.soldCount = p.soldCount + :quantity WHERE p.productId = :productId")
    void incrementSoldCount(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity WHERE p.productId = :productId")
    void decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity + :quantity WHERE p.productId = :productId")
    void increaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    long countByCategoryCategoryId(Integer categoryId);

    long countByCategoryCategoryIdAndIsAvailableTrue(Integer categoryId);

    @Query("SELECT p.isAvailable FROM Product p WHERE p.productId = :productId")
    Boolean isProductAvailable(@Param("productId") Long productId);
}
