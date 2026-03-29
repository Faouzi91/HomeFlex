package com.homeflex.features.property.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ queue and binding for the property-index consumer.
 * <p>
 * Binds to the outbox topic exchange so that every
 * {@code Property.PropertyIndexed} event published by the relay
 * is routed to the {@code property.index} queue.
 */
@Configuration
public class PropertySearchConfig {

    public static final String QUEUE_NAME = "property.index";
    public static final String ROUTING_KEY = "Property.PropertyIndexed";

    @Bean
    Queue propertyIndexQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    Binding propertyIndexBinding(Queue propertyIndexQueue, TopicExchange outboxExchange) {
        return BindingBuilder.bind(propertyIndexQueue).to(outboxExchange).with(ROUTING_KEY);
    }
}
