package com.gautam.order.controller;

import com.gautam.order.dto.OrderRequest;
import com.gautam.order.model.Order;
import com.gautam.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody @Valid OrderRequest orderRequest){
        orderService.placeOrder(orderRequest);
        return "Order has been Placed";
    }

    @GetMapping
    public Order getOrder(@RequestParam(name = "id" ) Long id){
        return orderService.getOrder(id);
    }
}
