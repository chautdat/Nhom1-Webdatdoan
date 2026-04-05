package com.pdq.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pdq.dto.common.ApiResponse;
import com.pdq.dto.product.ProductResponse;
import com.pdq.service.ProductService;

@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProductsAdmin();
        return ResponseEntity.ok(ApiResponse.success("All products retrieved", products));
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<ProductResponse>> updateAvailability(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {

        if (request == null || !request.containsKey("isAvailable")) {
            throw new RuntimeException("Missing 'isAvailable' field");
        }

        Boolean isAvailable = request.get("isAvailable");
        ProductResponse updated = productService.updateAvailability(id, isAvailable);

        String message = Boolean.TRUE.equals(isAvailable)
                ? "Product marked as AVAILABLE"
                : "Product marked as UNAVAILABLE";

        return ResponseEntity.ok(ApiResponse.success(message, updated));
    }
}
