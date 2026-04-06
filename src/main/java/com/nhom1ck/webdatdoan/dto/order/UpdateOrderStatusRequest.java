package com.nhom1ck.webdatdoan.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    @NotBlank(message = "Status is required")
    private String status; // pending, confirmed, preparing, ready, delivering, delivered, cancelled

    private String note;
}