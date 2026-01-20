package com.gautam.order.repository;


import com.gautam.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order , Long> {

    List<Order> getOrdersById(Long id);
}
