package com.gautam.order.service;

import com.gautam.order.client.InventoryClient;
import com.gautam.order.model.Order;
import com.gautam.order.dto.OrderRequest;
import com.gautam.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.gautam.order.exception.OrderNotFoundException;
import com.gautam.order.exception.OutOfStockException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    public void placeOrder(OrderRequest orderRequest){
        if(inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity())){
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price());
            order.setSkuCode(orderRequest.skuCode());
            order.setQuantity(orderRequest.quantity());
            orderRepository.save(order);
        }
        else {
            throw new OutOfStockException("Product with sku_code :::: "+ orderRequest.skuCode()+" :::: is out of stock");
        }


    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
    }
}
