package com.nhom1ck.webdatdoan.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Address is required")
    private String addressLine;

    private String ward;
    private String district;
    private String city;

    @NotNull(message = "Payment method is required")
    private String paymentMethod; // "cash" or "vnpay"

    private String notes;
}
