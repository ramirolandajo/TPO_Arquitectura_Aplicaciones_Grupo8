package com.uade.order.domain.port.out;

import com.uade.order.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {

    List<Order> findAll();

    Optional<Order> findById(Long id);

    Order save(Order order);

    long count();
}
