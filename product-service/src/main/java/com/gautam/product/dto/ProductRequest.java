package com.gautam.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
@AllArgsConstructor
@Getter
public class ProductRequest {
    private String skuCode;
    private String description;
    private BigDecimal price;

}
