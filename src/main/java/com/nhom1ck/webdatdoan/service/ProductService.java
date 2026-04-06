package com.pdq.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pdq.dto.common.PageResponse;
import com.pdq.dto.product.ProductRequest;
import com.pdq.dto.product.ProductResponse;
import com.pdq.entity.Category;
import com.pdq.entity.Product;
import com.pdq.exception.BadRequestException;
import com.pdq.exception.ResourceNotFoundException;
import com.pdq.repository.CategoryRepository;
import com.pdq.repository.ProductRepository;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private static final int DEFAULT_STOCK_QUANTITY = 100;
    private static final String DEFAULT_UNIT = "phần";
    private static final int DEFAULT_PREPARATION_TIME = 15;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // ===================== SEARCH =====================
    public PageResponse<ProductResponse> searchProducts(
            Integer categoryId,
            String keyword,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sortBy,
            String sortDir,
            Boolean includeUnavailable) {

        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new BadRequestException("Min price cannot be greater than max price");
        }

        Sort sort = Sort.by(sortBy).ascending();
        if (sortDir.equalsIgnoreCase("desc")) {
            sort = Sort.by(sortBy).descending();
        }
        Pageable pageable = PageRequest.of(page, size, sort);

        boolean allowUnavailable = Boolean.TRUE.equals(includeUnavailable);

        Page<Product> productPage = productRepository.searchProducts(
                allowUnavailable, categoryId, keyword, minPrice, maxPrice, pageable);

        return mapToPageResponse(productPage);
    }

    // ===================== GET BY ID =====================
    @Transactional(readOnly = false)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id: " + id));

        productRepository.incrementViewCount(id);

        return mapToResponse(product);
    }

    // ===================== FEATURED PRODUCTS =====================
    public List<ProductResponse> getFeaturedProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> products = productRepository.findByIsFeaturedTrue(pageable);

        return products.getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ===================== CREATE =====================
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (request.getProductName() == null || request.getProductName().isBlank()) {
            throw new BadRequestException("Product name is required");
        }

        String slug;
        if (request.getProductSlug() == null || request.getProductSlug().isBlank()) {
            slug = request.getProductName()
                    .toLowerCase()
                    .replaceAll("\\s+", "-")
                    .replaceAll("[^a-z0-9-]", "");
        } else {
            slug = request.getProductSlug();
        }

        if (productRepository.findByProductSlug(slug).isPresent()) {
            throw new BadRequestException("Product slug already exists");
        }

        Product product = new Product();
        product.setCategory(category);
        product.setProductName(request.getProductName());
        product.setProductSlug(slug);
        product.setSku(request.getSku());
        product.setDescription(request.getDescription());
        product.setShortDescription(request.getShortDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setImageUrl(request.getImageUrl());
        // Đặt tồn kho mặc định > 0 nếu FE không gửi
        Integer stock = request.getStockQuantity();
        product.setStockQuantity(stock != null ? stock : DEFAULT_STOCK_QUANTITY);
        product.setUnit(request.getUnit() != null ? request.getUnit() : DEFAULT_UNIT);
        product.setPreparationTime(request.getPreparationTime() != null ? request.getPreparationTime() : DEFAULT_PREPARATION_TIME);
        product.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false);
        product.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true);

        productRepository.save(product);

        return mapToResponse(product);
    }

    // ===================== UPDATE =====================
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        String slug;
        if (request.getProductSlug() == null || request.getProductSlug().isBlank()) {
            slug = request.getProductName()
                    .toLowerCase()
                    .replaceAll("\\s+", "-")
                    .replaceAll("[^a-z0-9-]", "");
        } else {
            slug = request.getProductSlug();
        }

        productRepository.findByProductSlug(slug).ifPresent(p -> {
            if (!p.getProductId().equals(id)) {
                throw new BadRequestException("Product slug already exists");
            }
        });

        product.setCategory(category);
        product.setProductName(request.getProductName());
        product.setProductSlug(slug);
        product.setSku(request.getSku());
        product.setDescription(request.getDescription());
        product.setShortDescription(request.getShortDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setImageUrl(request.getImageUrl());
        // Nếu FE không gửi stockQuantity, giữ nguyên; nếu null hiện tại thì set mặc định DEFAULT_STOCK_QUANTITY
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        } else if (product.getStockQuantity() == null) {
            product.setStockQuantity(DEFAULT_STOCK_QUANTITY);
        }
        if (request.getUnit() != null) {
            product.setUnit(request.getUnit());
        }
        if (request.getPreparationTime() != null) {
            product.setPreparationTime(request.getPreparationTime());
        }
        // Giữ nguyên giá trị cũ nếu FE không gửi
        if (request.getIsFeatured() != null) {
            product.setIsFeatured(request.getIsFeatured());
        } else if (product.getIsFeatured() == null) {
            product.setIsFeatured(false);
        }
        if (request.getIsAvailable() != null) {
            product.setIsAvailable(request.getIsAvailable());
        } else if (product.getIsAvailable() == null) {
            product.setIsAvailable(true);
        }

        productRepository.save(product);

        return mapToResponse(product);
    }

    // ===================== GET ALL ADMIN =====================
    public List<ProductResponse> getAllProductsAdmin() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ===================== UPDATE AVAILABILITY =====================
    @Transactional
    public ProductResponse updateAvailability(Long id, Boolean isAvailable) {
        if (isAvailable == null) {
            throw new BadRequestException("isAvailable is required");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id: " + id));

        product.setIsAvailable(isAvailable);
        productRepository.save(product);

        return mapToResponse(product);
    }

    // ===================== DELETE =====================
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    // ===================== MAP TO RESPONSE (ĐÃ FIX) =====================
    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();

        response.setProductId(product.getProductId());
        response.setProductName(product.getProductName());
        response.setProductSlug(product.getProductSlug());
        response.setSku(product.getSku());
        response.setDescription(product.getDescription());
        response.setShortDescription(product.getShortDescription());
        response.setPrice(product.getPrice());
        response.setDiscountPrice(product.getDiscountPrice());
        response.setImageUrl(product.getImageUrl());
        response.setStockQuantity(product.getStockQuantity());
        response.setUnit(product.getUnit());
        response.setPreparationTime(product.getPreparationTime());
        response.setIsFeatured(product.getIsFeatured());
        response.setIsAvailable(product.getIsAvailable());
        response.setAverageRating(product.getAverageRating());
        response.setReviewCount(product.getReviewCount());
        response.setSoldCount(product.getSoldCount());
        response.setViewCount(product.getViewCount());
        response.setCreatedAt(product.getCreatedAt());

        // ⚡ FIX QUAN TRỌNG: TRẢ CATEGORY VỀ FE
        if (product.getCategory() != null) {
            Integer catId = product.getCategory().getCategoryId();
            response.setCategoryId(catId != null ? catId.longValue() : null);
            response.setCategoryName(product.getCategory().getCategoryName());
            response.setCategorySlug(product.getCategory().getCategorySlug());
        }

        return response;
    }

    // ===================== MAP PAGE RESPONSE =====================
    private PageResponse<ProductResponse> mapToPageResponse(Page<Product> page) {
        List<ProductResponse> content = page.getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
