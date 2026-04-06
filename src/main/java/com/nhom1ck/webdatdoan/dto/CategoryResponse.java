package com.pdq.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pdq.entity.Category;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    @JsonProperty("categoryId")
    private Long categoryId;

    @JsonProperty("categoryName")
    private String categoryName;

    @JsonProperty("categorySlug")
    private String categorySlug;

    @JsonProperty("description")
    private String description;

    @JsonProperty("imageUrl")
    private String imageUrl;

    @JsonProperty("displayOrder")
    private Integer displayOrder;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("itemCount")
    private Long itemCount;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    public CategoryResponse(Category category, Long itemCount) {
        this.categoryId = category.getCategoryId().longValue();
        this.categoryName = category.getCategoryName();
        this.categorySlug = category.getCategorySlug();
        this.description = category.getDescription();
        this.imageUrl = category.getImageUrl();
        this.displayOrder = category.getDisplayOrder();
        this.isActive = category.getIsActive();
        this.itemCount = itemCount;
        this.createdAt = category.getCreatedAt();
        this.updatedAt = category.getUpdatedAt();
    }
}
