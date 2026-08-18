package com.gautam.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
@AllArgsConstructor
@Getter
public class ProductRequest {
    @NotBlank(message = "skuCode is required")
    private String skuCode;
    @NotBlank(message = "description is required")
    private String description;
    @NotNull(message = "price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "price must be positive")
    private BigDecimal price;

}
