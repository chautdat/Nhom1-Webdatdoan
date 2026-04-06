package com.pdq.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pdq.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    Optional<Category> findByCategorySlug(String slug);
    
    List<Category> findByIsActiveTrueOrderByDisplayOrderAsc();
    
    boolean existsByCategoryName(String categoryName);
    
    boolean existsByCategorySlug(String categorySlug);

    boolean existsByCategoryNameAndCategoryIdNot(String categoryName, Integer categoryId);

    boolean existsByCategorySlugAndCategoryIdNot(String categorySlug, Integer categoryId);
    
    @Query("SELECT COALESCE(MAX(c.displayOrder), 0) FROM Category c")
    Integer findMaxDisplayOrder();
    
    @Query("SELECT c FROM Category c ORDER BY c.displayOrder ASC, c.categoryName ASC")
    List<Category> findAllOrderedByDisplayOrder();
}
