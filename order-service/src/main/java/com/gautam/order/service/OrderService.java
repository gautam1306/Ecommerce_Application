package com.gautam.order.service;

import com.gautam.order.client.InventoryClient;
import com.gautam.order.model.Order;
import com.gautam.order.dto.OrderRequest;
import com.gautam.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
            throw new RuntimeException("Product with sku_code :::: "+ orderRequest.skuCode()+" :::: is out of stock");
        }


    }

    public List<Order> getOrders(Long id) {
        return orderRepository.getOrdersById(id);
    }
}
