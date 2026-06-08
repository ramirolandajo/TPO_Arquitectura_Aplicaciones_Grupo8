package com.uade.order.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "known_products")
public class KnownProductJpaEntity {

    @Id
    private Long productId;

    @Column(nullable = false, unique = true)
    private String name;

    private Integer quantity;
    private Double price;

    public KnownProductJpaEntity() {
    }

    public KnownProductJpaEntity(Long productId, String name, Integer quantity, Double price) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }
}
