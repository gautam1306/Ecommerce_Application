package com.gautam.order.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrderRequest(
        Long id,
        String orderNumber,
        @NotBlank(message = "skuCode is required") String skuCode,
        @NotNull(message = "price is required") @DecimalMin(value = "0.0", inclusive = false, message = "price must be positive") BigDecimal price,
        @NotNull(message = "quantity is required") @Min(value = 1, message = "quantity must be at least 1") Integer quantity) {
}
