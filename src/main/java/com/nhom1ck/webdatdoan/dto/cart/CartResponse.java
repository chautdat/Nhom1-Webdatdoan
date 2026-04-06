package com.nhom1ck.webdatdoan.dto.cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long cartId;
    private Integer totalItems;
    private BigDecimal totalPrice;
    private List<CartItemResponse> items = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemResponse {
        private Long cartItemId;
        private Long productId;
        private String productName;
        private String productImage;
        private BigDecimal price;
        private BigDecimal discountPrice;
        private Integer quantity;
        private BigDecimal subtotal;
        private String notes;
    }
}