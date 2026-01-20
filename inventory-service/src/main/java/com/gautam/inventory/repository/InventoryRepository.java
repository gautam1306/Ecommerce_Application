package com.gautam.inventory.repository;

import com.gautam.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory , Long> {

    boolean existsBySkuCodeAndQuantityIsGreaterThanEqual(String skuCode, int quantity);

    Optional<Inventory> findBySkuCode(String skuCode);

    default void addStock(String skuCode, int quantity) {
        Inventory inventory = findBySkuCode(skuCode)
                .map(existingInventory -> {
                    existingInventory.setQuantity(existingInventory.getQuantity() + quantity);
                    return existingInventory;
                })
                .orElseGet(() -> {
                    Inventory newInventory = new Inventory();
                    newInventory.setSkuCode(skuCode);
                    newInventory.setQuantity(quantity);
                    return newInventory;
                });
        save(inventory);
    }
}
