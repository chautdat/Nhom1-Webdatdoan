package com.nhom1ck.webdatdoan.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CancelOrderRequest {
    @NotBlank(message = "Cancellation reason is required")
    private String reason;
    private String additionalNotes;
}

