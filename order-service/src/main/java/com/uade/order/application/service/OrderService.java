package com.uade.order.application.service;

import com.uade.order.domain.event.OrderCreatedEvent;
import com.uade.order.domain.model.Order;
import com.uade.order.domain.port.in.OrderUseCase;
import com.uade.order.domain.port.out.EventPublisherPort;
import com.uade.order.domain.port.out.OrderRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService implements OrderUseCase {

    private final OrderRepositoryPort repositoryPort;
    private final EventPublisherPort eventPublisherPort;

    public OrderService(OrderRepositoryPort repositoryPort, EventPublisherPort eventPublisherPort) {
        this.repositoryPort = repositoryPort;
        this.eventPublisherPort = eventPublisherPort;
    }

    @Override
    public List<Order> getAllOrders() {
        return repositoryPort.findAll();
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return repositoryPort.findById(id);
    }

    @Override
    public Order createOrder(Order order) {
        Order saved = repositoryPort.save(order);
        eventPublisherPort.publishOrderCreated(
                new OrderCreatedEvent(saved.getId(), saved.getProduct(), saved.getQuantity(), saved.getCustomer())
        );
        return saved;
    }
}
