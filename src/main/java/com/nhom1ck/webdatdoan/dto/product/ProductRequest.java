package com.pdq.dto.product;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotNull(message = "Category ID is required")
    private Integer categoryId;

    @NotBlank(message = "Product name is required")
    private String productName;

    private String productSlug;

    private String sku;
    private String description;
    private String shortDescription;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    private BigDecimal discountPrice;
    private String imageUrl;
    private List<String> images;
    private Integer stockQuantity;
    private String unit;
    private Integer preparationTime;
    private Boolean isFeatured;
    private Boolean isAvailable;
}
