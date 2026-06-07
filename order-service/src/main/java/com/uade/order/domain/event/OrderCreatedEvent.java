package com.uade.order.domain.event;

import java.time.Instant;

public class OrderCreatedEvent {

    private Long orderId;
    private String product;
    private Integer quantity;
    private String customer;
    private Instant timestamp;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(Long orderId, String product, Integer quantity, String customer) {
        this.orderId = orderId;
        this.product = product;
        this.quantity = quantity;
        this.customer = customer;
        this.timestamp = Instant.now();
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
