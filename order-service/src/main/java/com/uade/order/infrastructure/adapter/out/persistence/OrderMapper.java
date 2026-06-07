package com.uade.order.infrastructure.adapter.out.persistence;

import com.uade.order.domain.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public Order toDomain(OrderJpaEntity entity) {
        return new Order(
                entity.getId(),
                entity.getProduct(),
                entity.getQuantity(),
                entity.getCustomer(),
                entity.getStatus()
        );
    }

    public OrderJpaEntity toJpaEntity(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity(
                order.getProduct(),
                order.getQuantity(),
                order.getCustomer(),
                order.getStatus()
        );
        if (order.getId() != null) {
            entity.setId(order.getId());
        }
        return entity;
    }
}
