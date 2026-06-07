package com.uade.order.domain.model;

import java.util.Objects;

public class Order {

    private Long id;
    private String product;
    private Integer quantity;
    private String customer;
    private String status;

    public Order() {
    }

    public Order(String product, Integer quantity, String customer, String status) {
        this.product = product;
        this.quantity = quantity;
        this.customer = customer;
        this.status = status;
    }

    public Order(Long id, String product, Integer quantity, String customer, String status) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.customer = customer;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getCustomer() {
        return customer;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
