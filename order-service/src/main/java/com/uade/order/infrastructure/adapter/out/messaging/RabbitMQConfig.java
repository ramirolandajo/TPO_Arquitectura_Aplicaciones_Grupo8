package com.uade.order.infrastructure.adapter.out.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("rabbitmq")
public class RabbitMQConfig {

    // --- Topología propia: eventos que order-service PUBLICA ---
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_QUEUE = "order.created.queue";
    public static final String ORDER_ROUTING_KEY = "order.created";

    // --- Topología de inventory-service: eventos que order-service CONSUME ---
    // Cola propia (distinta de la de notification-service) ligada al exchange de inventory,
    // para recibir cada ProductCreated de forma independiente.
    public static final String INVENTORY_EXCHANGE = "inventory.exchange";
    public static final String PRODUCT_ORDER_QUEUE = "product.created.order.queue";
    public static final String PRODUCT_ROUTING_KEY = "product.created";

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable(ORDER_QUEUE).build();
    }

    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(orderExchange).with(ORDER_ROUTING_KEY);
    }

    @Bean
    public TopicExchange inventoryExchange() {
        return new TopicExchange(INVENTORY_EXCHANGE);
    }

    @Bean
    public Queue productCreatedOrderQueue() {
        return QueueBuilder.durable(PRODUCT_ORDER_QUEUE).build();
    }

    @Bean
    public Binding productCreatedOrderBinding(Queue productCreatedOrderQueue, TopicExchange inventoryExchange) {
        return BindingBuilder.bind(productCreatedOrderQueue).to(inventoryExchange).with(PRODUCT_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
