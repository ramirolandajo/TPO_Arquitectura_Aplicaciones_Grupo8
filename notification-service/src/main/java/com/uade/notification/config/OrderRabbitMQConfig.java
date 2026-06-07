package com.uade.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Topología RabbitMQ para consumir los eventos OrderCreated publicados por order-service.
 * El MessageConverter ya está declarado en {@link RabbitMQConfig}.
 */
@Configuration
@Profile("rabbitmq")
public class OrderRabbitMQConfig {

    public static final String EXCHANGE = "order.exchange";
    public static final String QUEUE = "order.created.queue";
    public static final String ROUTING_KEY = "order.created";

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(orderExchange).with(ROUTING_KEY);
    }
}
