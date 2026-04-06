package com.nhom1ck.webdatdoan.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "recipient_name", nullable = false, length = 100)
    private String recipientName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(name = "delivery_address", nullable = false, length = 500)
    private String deliveryAddress;

    @Column(name = "address_line", nullable = false, length = 255)
    private String addressLine;

    @Column(name = "delivery_lat")
    private Double deliveryLat;

    @Column(name = "delivery_lng")
    private Double deliveryLng;

    @Column(name = "distance")
    private Double distance;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "estimated_arrival")
    private LocalDateTime estimatedArrival;

    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "shipping_fee", precision = 10, scale = 2)
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 38, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "final_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod = PaymentMethod.cash;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.pending;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus = OrderStatus.pending;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "cancelled_reason", length = 500)
    private String cancelledReason;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (deliveryAddress != null && addressLine == null) {
            addressLine = deliveryAddress.length() > 255
                    ? deliveryAddress.substring(0, 255)
                    : deliveryAddress;
        }
        if (addressLine != null && deliveryAddress == null) {
            deliveryAddress = addressLine;
        }

        if (finalAmount == null) {
            BigDecimal total = subtotal != null ? subtotal : BigDecimal.ZERO;
            BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
            BigDecimal shipping = shippingFee != null ? shippingFee : BigDecimal.ZERO;
            finalAmount = total.subtract(discount).add(shipping);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        if (deliveryAddress != null && addressLine == null) {
            addressLine = deliveryAddress.length() > 255
                    ? deliveryAddress.substring(0, 255)
                    : deliveryAddress;
        }
        if (addressLine != null && deliveryAddress == null) {
            deliveryAddress = addressLine;
        }

        if (orderStatus == OrderStatus.confirmed && confirmedAt == null) {
            confirmedAt = LocalDateTime.now();
        }
        if (orderStatus == OrderStatus.delivered && deliveredAt == null) {
            deliveredAt = LocalDateTime.now();
        }
        if (orderStatus == OrderStatus.cancelled && cancelledAt == null) {
            cancelledAt = LocalDateTime.now();
        }
    }

    // Custom setters with sync logic (Lombok won't override these)
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        if (deliveryAddress != null && this.addressLine == null) {
            this.addressLine = deliveryAddress.length() > 255
                    ? deliveryAddress.substring(0, 255)
                    : deliveryAddress;
        }
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
        if (addressLine != null && this.deliveryAddress == null) {
            this.deliveryAddress = addressLine;
        }
    }
}
