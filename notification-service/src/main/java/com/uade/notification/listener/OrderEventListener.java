package com.uade.notification.listener;

import com.uade.notification.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("rabbitmq")
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    @RabbitListener(queues = "order.created.queue")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("=== NOTIFICACIÓN RECIBIDA (Orden) ===");
        log.info("Nueva orden creada:");
        log.info("  ID:       {}", event.getOrderId());
        log.info("  Producto: {}", event.getProduct());
        log.info("  Cantidad: {}", event.getQuantity());
        log.info("  Cliente:  {}", event.getCustomer());
        log.info("  Fecha:    {}", event.getTimestamp());
        log.info("=====================================");
    }
}
