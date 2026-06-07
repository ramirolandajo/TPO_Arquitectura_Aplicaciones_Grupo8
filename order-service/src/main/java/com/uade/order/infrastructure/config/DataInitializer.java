package com.uade.order.infrastructure.config;

import com.uade.order.domain.model.Order;
import com.uade.order.domain.port.out.OrderRepositoryPort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final OrderRepositoryPort orderRepositoryPort;

    public DataInitializer(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public void run(String... args) {
        if (orderRepositoryPort.count() == 0) {
            orderRepositoryPort.save(new Order("Laptop", 1, "admin", "CREATED"));
            orderRepositoryPort.save(new Order("Mouse", 2, "user", "CREATED"));
        }
    }
}
