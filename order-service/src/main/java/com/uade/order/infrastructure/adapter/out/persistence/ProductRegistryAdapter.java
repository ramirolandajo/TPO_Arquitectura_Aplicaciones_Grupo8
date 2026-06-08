package com.uade.order.infrastructure.adapter.out.persistence;

import com.uade.order.domain.port.out.ProductRegistryPort;
import org.springframework.stereotype.Component;

@Component
public class ProductRegistryAdapter implements ProductRegistryPort {

    private final KnownProductJpaRepository repo;

    public ProductRegistryAdapter(KnownProductJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public boolean existsByName(String productName) {
        return repo.existsByName(productName);
    }

    @Override
    public void register(Long productId, String name, Integer quantity, Double price) {
        if (!repo.existsById(productId)) {
            repo.save(new KnownProductJpaEntity(productId, name, quantity, price));
        }
    }
}
