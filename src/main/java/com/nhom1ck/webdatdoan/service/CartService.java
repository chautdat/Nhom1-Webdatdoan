package com.pdq.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pdq.dto.cart.AddToCartRequest;
import com.pdq.dto.cart.CartResponse;
import com.pdq.entity.Cart;
import com.pdq.entity.CartItem;
import com.pdq.entity.Product;
import com.pdq.entity.User;
import com.pdq.exception.BadRequestException;
import com.pdq.exception.ResourceNotFoundException;
import com.pdq.repository.CartItemRepository;
import com.pdq.repository.CartRepository;
import com.pdq.repository.ProductRepository;
import com.pdq.repository.UserRepository;

@Service
@Transactional // 🔥 SỬA: KHÔNG readOnly nữa
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(String userEmail) {
        User user = getUserByEmail(userEmail);
        Cart cart = getOrCreateCart(user);
        return mapToResponse(cart);
    }

    @Transactional(readOnly = false) // 🔥 Đảm bảo được ghi DB
    public CartResponse addToCart(String userEmail, AddToCartRequest request) {
        User user = getUserByEmail(userEmail);
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getIsAvailable()) {
            throw new BadRequestException("Product is not available");
        }

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new BadRequestException("Insufficient stock");
        }

        CartItem existingItem = cartItemRepository.findByCartAndProduct(cart, product).orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            existingItem.setNotes(request.getNotes());
            // Recalculate subtotal
            BigDecimal unitPrice = existingItem.getDiscountPrice() != null
                    && existingItem.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0
                    ? existingItem.getDiscountPrice()
                    : existingItem.getPrice();
            existingItem.setSubtotal(unitPrice.multiply(BigDecimal.valueOf(existingItem.getQuantity())));
            cartItemRepository.save(existingItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(product.getPrice());
            cartItem.setDiscountPrice(product.getDiscountPrice() != null ? product.getDiscountPrice() : BigDecimal.ZERO);
            cartItem.setNotes(request.getNotes());
            BigDecimal unitPrice = cartItem.getDiscountPrice() != null
                    && cartItem.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0
                    ? cartItem.getDiscountPrice()
                    : cartItem.getPrice();
            cartItem.setSubtotal(unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            cartItemRepository.save(cartItem);
        }

        cart = cartRepository.findById(cart.getCartId()).orElseThrow();
        return mapToResponse(cart);
    }

    @Transactional(readOnly = false) // 🔥 update quantity -> phải writable
    public CartResponse updateCartItem(String userEmail, Long cartItemId, Integer quantity) {
        User user = getUserByEmail(userEmail);
        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getCartId().equals(cart.getCartId())) {
            throw new BadRequestException("Cart item does not belong to user");
        }

        Product product = cartItem.getProduct();
        if (!product.getIsAvailable()) {
            throw new BadRequestException("Product is not available");
        }
        if (product.getStockQuantity() < quantity) {
            throw new BadRequestException("Insufficient stock");
        }

        cartItem.setQuantity(quantity);
        BigDecimal unitPrice = cartItem.getDiscountPrice() != null
                && cartItem.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0
                ? cartItem.getDiscountPrice()
                : cartItem.getPrice();
        cartItem.setSubtotal(unitPrice.multiply(BigDecimal.valueOf(quantity)));
        cartItemRepository.save(cartItem);

        cart = cartRepository.findById(cart.getCartId()).orElseThrow();
        return mapToResponse(cart);
    }

    @Transactional(readOnly = false) // 🔥 delete item -> phải writable
    public CartResponse removeCartItem(String userEmail, Long cartItemId) {
        User user = getUserByEmail(userEmail);
        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getCartId().equals(cart.getCartId())) {
            throw new BadRequestException("Cart item does not belong to user");
        }

        cartItemRepository.delete(cartItem);

        cart = cartRepository.findById(cart.getCartId()).orElseThrow();
        return mapToResponse(cart);
    }

    @Transactional(readOnly = false) // 🔥 clear cart -> phải writable
    public void clearCart(String userEmail) {
        User user = getUserByEmail(userEmail);
        Cart cart = cartRepository.findByUser(user).orElse(null);

        if (cart != null) {
            cartItemRepository.deleteByCart(cart);
        }
    }

    private User getUserByEmail(String emailOrUsername) {
        return userRepository.findByUsername(emailOrUsername)
                .orElseGet(() -> userRepository.findByEmail(emailOrUsername)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    @Transactional(readOnly = false) // 🔥 create cart -> cần save()
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    private CartResponse mapToResponse(Cart cart) {
        List<CartResponse.CartItemResponse> items = cart.getItems().stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList());

        int totalItems = items.stream()
                .mapToInt(CartResponse.CartItemResponse::getQuantity)
                .sum();

        BigDecimal totalPrice = items.stream()
                .map(item -> {
                    if (item.getSubtotal() != null) {
                        return item.getSubtotal();
                    }
                    BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
                    return price.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CartResponse response = new CartResponse();
        response.setCartId(cart.getCartId());
        response.setTotalItems(totalItems);
        response.setTotalPrice(totalPrice);
        response.setItems(items);

        return response;
    }

    private CartResponse.CartItemResponse mapItemToResponse(CartItem item) {
        BigDecimal subtotal = item.getSubtotal();
        if (subtotal == null) {
            BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
            subtotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));
        }

        CartResponse.CartItemResponse response = new CartResponse.CartItemResponse();
        response.setCartItemId(item.getCartItemId());
        response.setProductId(item.getProduct().getProductId());
        response.setProductName(item.getProduct().getProductName());
        response.setProductImage(item.getProduct().getImageUrl());
        response.setPrice(item.getPrice());
        response.setDiscountPrice(item.getDiscountPrice());
        response.setQuantity(item.getQuantity());
        response.setSubtotal(subtotal);
        response.setNotes(item.getNotes());

        return response;
    }
}
