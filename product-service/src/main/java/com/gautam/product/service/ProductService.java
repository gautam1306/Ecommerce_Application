package com.gautam.product.service;

import com.gautam.product.dto.ProductRequest;
import com.gautam.product.model.Product;
import com.gautam.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;

    public Product createProduct(ProductRequest productRequest){
        Product product = Product.builder().skuCode(productRequest.getSkuCode()).description(productRequest.getDescription()).price(productRequest.getPrice()).build();
        productRepository.save(product);
        log.info("Product Created successfully");
        return product;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
