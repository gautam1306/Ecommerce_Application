package com.gautam.order.controller;

import com.gautam.order.dto.OrderRequest;
import com.gautam.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public String placeOrder(@RequestBody OrderRequest orderRequest){
        orderService.placeOrder(orderRequest);
        return "Order has been Placed";
    }

    @GetMapping
    public String getOrder(@RequestParam(name = "id" ) Long id){
        return orderService.getOrders(id).toString();
    }
}
