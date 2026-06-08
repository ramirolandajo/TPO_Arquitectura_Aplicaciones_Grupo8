package com.uade.order.domain.exception;

public class ProductNotRegisteredException extends RuntimeException {

    public ProductNotRegisteredException(String productName) {
        super("El producto '" + productName + "' no existe en el inventario");
    }
}
