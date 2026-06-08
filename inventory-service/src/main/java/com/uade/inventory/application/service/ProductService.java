package com.uade.inventory.application.service;

import com.uade.inventory.domain.event.ProductCreatedEvent;
import com.uade.inventory.domain.exception.InvalidProductException;
import com.uade.inventory.domain.model.Product;
import com.uade.inventory.domain.port.in.ProductUseCase;
import com.uade.inventory.domain.port.out.EventPublisherPort;
import com.uade.inventory.domain.port.out.ProductRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements ProductUseCase {

    private final ProductRepositoryPort repositoryPort;
    private final EventPublisherPort eventPublisherPort;

    public ProductService(ProductRepositoryPort repositoryPort, EventPublisherPort eventPublisherPort) {
        this.repositoryPort = repositoryPort;
        this.eventPublisherPort = eventPublisherPort;
    }

    @Override
    public List<Product> getAllProducts() {
        return repositoryPort.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return repositoryPort.findById(id);
    }

    @Override
    public Product createProduct(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new InvalidProductException("El nombre del producto no puede estar vacío");
        }
        if (product.getQuantity() == null || product.getQuantity() < 0) {
            throw new InvalidProductException("La cantidad debe ser mayor o igual a 0");
        }
        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new InvalidProductException("El precio debe ser mayor a 0");
        }
        Product saved = repositoryPort.save(product);
        eventPublisherPort.publishProductCreated(
                new ProductCreatedEvent(saved.getId(), saved.getName(), saved.getQuantity(), saved.getPrice())
        );
        return saved;
    }
}
