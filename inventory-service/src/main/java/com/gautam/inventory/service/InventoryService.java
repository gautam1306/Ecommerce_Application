package com.gautam.inventory.service;

import com.gautam.inventory.dto.InventoryRequest;
import com.gautam.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public boolean isInStock(String skuCode,int quantity){
        skuCode.split(" ");
        return inventoryRepository.existsBySkuCodeAndQuantityIsGreaterThanEqual(skuCode,quantity);

    }

    public boolean addStock(InventoryRequest inventoryRequest) {
        inventoryRepository.addStock(inventoryRequest.skuCode(), inventoryRequest.quantity());
        return false;
    }
}
