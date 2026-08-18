package com.gautam.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InventoryRequest(
        @NotBlank(message = "skuCode is required") String skuCode,
        @NotNull(message = "quantity is required") @Min(value = 1, message = "quantity must be at least 1") Integer quantity) {
}
