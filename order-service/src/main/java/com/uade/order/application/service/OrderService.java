package com.uade.order.application.service;

import com.uade.order.domain.event.OrderCreatedEvent;
import com.uade.order.domain.exception.InvalidOrderException;
import com.uade.order.domain.exception.ProductNotRegisteredException;
import com.uade.order.domain.model.Order;
import com.uade.order.domain.port.in.OrderUseCase;
import com.uade.order.domain.port.out.EventPublisherPort;
import com.uade.order.domain.port.out.OrderRepositoryPort;
import com.uade.order.domain.port.out.ProductRegistryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService implements OrderUseCase {

    private final OrderRepositoryPort repositoryPort;
    private final EventPublisherPort eventPublisherPort;
    private final ProductRegistryPort productRegistryPort;

    public OrderService(OrderRepositoryPort repositoryPort, EventPublisherPort eventPublisherPort,
                        ProductRegistryPort productRegistryPort) {
        this.repositoryPort = repositoryPort;
        this.eventPublisherPort = eventPublisherPort;
        this.productRegistryPort = productRegistryPort;
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
        if (order.getProduct() == null || order.getProduct().isBlank()) {
            throw new InvalidOrderException("El nombre del producto no puede estar vacío");
        }
        if (!productRegistryPort.existsByName(order.getProduct())) {
            throw new ProductNotRegisteredException(order.getProduct());
        }
        if (order.getQuantity() == null || order.getQuantity() <= 0) {
            throw new InvalidOrderException("La cantidad debe ser mayor a 0");
        }
        if (order.getCustomer() == null || order.getCustomer().isBlank()) {
            throw new InvalidOrderException("El cliente no puede estar vacío");
        }
        Order saved = repositoryPort.save(order);
        eventPublisherPort.publishOrderCreated(
                new OrderCreatedEvent(saved.getId(), saved.getProduct(), saved.getQuantity(), saved.getCustomer())
        );
        return saved;
    }
}
