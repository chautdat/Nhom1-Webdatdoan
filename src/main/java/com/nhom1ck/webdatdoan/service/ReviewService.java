package com.pdq.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pdq.dto.common.PageResponse;
import com.pdq.dto.review.ReviewRequest;
import com.pdq.dto.review.ReviewResponse;
import com.pdq.entity.Order;
import com.pdq.entity.Product;
import com.pdq.entity.Review;
import com.pdq.entity.ReviewStatus;
import com.pdq.entity.User;
import com.pdq.exception.BadRequestException;
import com.pdq.exception.ResourceNotFoundException;
import com.pdq.repository.OrderRepository;
import com.pdq.repository.ProductRepository;
import com.pdq.repository.ReviewRepository;
import com.pdq.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ReviewService(ReviewRepository reviewRepository,
                        UserRepository userRepository,
                        ProductRepository productRepository,
                        OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public ReviewResponse createReview(String userEmail, ReviewRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Check if user already reviewed this product
        if (reviewRepository.existsByUserAndProduct(user, product)) {
            throw new BadRequestException("You have already reviewed this product");
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setStatus(ReviewStatus.pending);

        if (request.getOrderId() != null) {
            Order order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
            review.setOrder(order);
        }

        review = reviewRepository.save(review);
        return mapToResponse(review);
    }

    public PageResponse<ReviewResponse> getProductReviews(Long productId, int page, int size) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviewPage = reviewRepository.findByProductAndStatus(
                product, ReviewStatus.approved, pageable);

        return mapToPageResponse(reviewPage);
    }

    public PageResponse<ReviewResponse> getMyReviews(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviewPage = reviewRepository.findByUser(user, pageable);

        return mapToPageResponse(reviewPage);
    }

    @Transactional
    public ReviewResponse approveReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        review.setStatus(ReviewStatus.approved);
        reviewRepository.save(review);

        // Update product rating
        updateProductRating(review.getProduct());

        return mapToResponse(review);
    }

    @Transactional
    public ReviewResponse rejectReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        review.setStatus(ReviewStatus.rejected);
        reviewRepository.save(review);

        return mapToResponse(review);
    }

    @Transactional
    public ReviewResponse replyToReview(Long reviewId, String adminReply) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        review.setAdminReply(adminReply);
        review.setRepliedAt(LocalDateTime.now());
        reviewRepository.save(review);

        return mapToResponse(review);
    }

    @Transactional
    public void deleteReview(Long reviewId, String userEmail) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!review.getUser().getUserId().equals(user.getUserId())) {
            throw new BadRequestException("Access denied");
        }

        reviewRepository.delete(review);

        // Update product rating
        updateProductRating(review.getProduct());
    }

    public PageResponse<ReviewResponse> getAllPendingReviews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviewPage = reviewRepository.findByStatus(ReviewStatus.pending, pageable);
        return mapToPageResponse(reviewPage);
    }

    private void updateProductRating(Product product) {
        Double avgRating = reviewRepository.getAverageRating(product);
        Long reviewCount = reviewRepository.countApprovedReviews(product);

        if (avgRating != null) {
            product.setAverageRating(BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP));
        } else {
            product.setAverageRating(BigDecimal.ZERO);
        }

        product.setReviewCount(reviewCount != null ? reviewCount.intValue() : 0);
        productRepository.save(product);
    }

    private ReviewResponse mapToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setReviewId(review.getReviewId());
        response.setUserId(review.getUser().getUserId());
        response.setUserName(review.getUser().getFullName());
        response.setUserAvatar(review.getUser().getAvatarUrl());
        response.setProductId(review.getProduct().getProductId());
        response.setProductName(review.getProduct().getProductName());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setStatus(review.getStatus().name());
        response.setAdminReply(review.getAdminReply());
        response.setRepliedAt(review.getRepliedAt());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }

    private PageResponse<ReviewResponse> mapToPageResponse(Page<Review> page) {
        List<ReviewResponse> content = page.getContent().stream()
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