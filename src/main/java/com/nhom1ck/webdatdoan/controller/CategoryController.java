package com.pdq.controller;

import java.text.Normalizer;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.pdq.dto.CategoryRequest;
import com.pdq.dto.CategoryResponse;
import com.pdq.dto.common.ApiResponse;
import com.pdq.entity.Category;
import com.pdq.repository.CategoryRepository;
import com.pdq.repository.ProductRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryController(
            CategoryRepository categoryRepository,
            ProductRepository productRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    // ==================== GET ALL ====================
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {

        List<Category> categories =
                categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc();

        List<CategoryResponse> responses = categories.stream()
                .map(category -> {
                    Long itemCount = productRepository.countByCategoryCategoryId(category.getCategoryId());
                    return new CategoryResponse(category, itemCount);  // ← Dùng constructor đúng chuẩn
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success("Categories retrieved successfully", responses)
        );
    }

    // ==================== GET BY ID ====================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable Integer id
    ) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Long itemCount = productRepository.countByCategoryCategoryId(id);

        CategoryResponse response = new CategoryResponse(category, itemCount);

        return ResponseEntity.ok(
                ApiResponse.success("Category retrieved successfully", response)
        );
    }

    // ==================== CREATE ====================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request
    ) {
        if (categoryRepository.existsByCategoryName(request.getCategoryName())) {
            throw new RuntimeException("Category name already exists");
        }

        String slug = generateSlug(request.getCategoryName());
        if (categoryRepository.existsByCategorySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Category category = new Category();
        category.setCategoryName(request.getCategoryName());
        category.setCategorySlug(slug);
        category.setDescription(request.getDescription());
        category.setIsActive(true);
        category.setImageUrl(request.getImageUrl());
        category.setDisplayOrder(categoryRepository.findMaxDisplayOrder() + 1);

        Category saved = categoryRepository.save(category);

        CategoryResponse response = new CategoryResponse(saved, 0L);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", response));
    }

    // ==================== UPDATE ====================
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Integer id,
            @Valid @RequestBody CategoryRequest request
    ) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getCategoryName().equals(request.getCategoryName())) {
            if (categoryRepository.existsByCategoryNameAndCategoryIdNot(request.getCategoryName(), id)) {
                throw new RuntimeException("Category name already exists");
            }

            String slug = generateSlug(request.getCategoryName());
            if (categoryRepository.existsByCategorySlugAndCategoryIdNot(slug, id)) {
                slug = slug + "-" + System.currentTimeMillis();
            }
            category.setCategorySlug(slug);
        }

        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());

        Category updated = categoryRepository.save(category);

        Long itemCount = productRepository.countByCategoryCategoryId(id);

        CategoryResponse response = new CategoryResponse(updated, itemCount);

        return ResponseEntity.ok(
                ApiResponse.success("Category updated successfully", response)
        );
    }

    // ==================== DELETE ====================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        long productCount = productRepository.countByCategoryCategoryId(id);
        if (productCount > 0) {
            throw new RuntimeException(
                    "Cannot delete category. There are " + productCount + " products in this category"
            );
        }

        categoryRepository.delete(category);

        return ResponseEntity.ok(
                ApiResponse.success("Category deleted successfully", null)
        );
    }

    // ==================== HELPER ====================
    private String generateSlug(String name) {
        return Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}
