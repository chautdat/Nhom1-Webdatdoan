package com.pdq.dto.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long productId;
    private String productName;
    private String productSlug;
    private String sku;
    private String description;
    private String shortDescription;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String imageUrl;
    private List<String> images;
    private Integer stockQuantity;
    private String unit;
    private Integer preparationTime;
    private Boolean isFeatured;
    private Boolean isAvailable;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private Integer soldCount;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private Long categoryId;
    private String categoryName;
    private String categorySlug;
}
