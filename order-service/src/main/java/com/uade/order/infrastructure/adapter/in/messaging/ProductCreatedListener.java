package com.uade.order.infrastructure.adapter.in.messaging;

import com.uade.order.infrastructure.adapter.out.messaging.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("rabbitmq")
public class ProductCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(ProductCreatedListener.class);

    @RabbitListener(queues = RabbitMQConfig.PRODUCT_ORDER_QUEUE)
    public void handleProductCreated(ProductCreatedEvent event) {
        log.info("=== EVENTO RECIBIDO DE INVENTORY (order-service) ===");
        log.info("Producto disponible para ordenar:");
        log.info("  ID:       {}", event.getProductId());
        log.info("  Nombre:   {}", event.getName());
        log.info("  Cantidad: {}", event.getQuantity());
        log.info("  Precio:   ${}", event.getPrice());
        log.info("====================================================");
    }
}
