package com.uade.order.domain.port.out;

public interface ProductRegistryPort {

    boolean existsByName(String productName);

    void register(Long productId, String name, Integer quantity, Double price);
}
