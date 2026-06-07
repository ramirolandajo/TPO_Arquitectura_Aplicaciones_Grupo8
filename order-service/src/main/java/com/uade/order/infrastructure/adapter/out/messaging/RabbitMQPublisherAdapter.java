package com.uade.order.infrastructure.adapter.out.messaging;

import com.uade.order.domain.event.OrderCreatedEvent;
import com.uade.order.domain.port.out.EventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("rabbitmq")
public class RabbitMQPublisherAdapter implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQPublisherAdapter.class);

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQPublisherAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishOrderCreated(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_ROUTING_KEY,
                event
        );
        log.info("Evento publicado: OrderCreated [id={}, product={}]", event.getOrderId(), event.getProduct());
    }
}
