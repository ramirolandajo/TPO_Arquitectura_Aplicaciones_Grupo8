package com.uade.order.infrastructure.adapter.out.persistence;

import com.uade.order.domain.model.Order;
import com.uade.order.domain.port.out.OrderRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OrderPersistenceAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository jpaRepository;
    private final OrderMapper mapper;

    public OrderPersistenceAdapter(OrderJpaRepository jpaRepository, OrderMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Order> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity saved = jpaRepository.save(mapper.toJpaEntity(order));
        return mapper.toDomain(saved);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
