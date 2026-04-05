package com.homeflex.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ infrastructure beans.
 * <p>
 * Uses a single <strong>topic exchange</strong> so consumers can bind with
 * routing-key patterns such as {@code Booking.#} or {@code Property.*}.
 * The routing key published by the relay is {@code {aggregateType}.{eventType}}
 * (e.g. {@code Booking.BookingCreated}).
 * <p>
 * Publisher confirms are enabled via {@code spring.rabbitmq.publisher-confirm-type=correlated}
 * in application.yml — no extra programmatic setup needed here.
 */
@Configuration
@RequiredArgsConstructor
public class RabbitMqConfig {

    private final AppProperties appProperties;

    @Bean
    TopicExchange outboxExchange() {
        return new TopicExchange(appProperties.getOutbox().getExchangeName(), true, false);
    }

    // ── Booking event queue ───────────────────────────��──────────────────

    @Bean
    Queue bookingEventsQueue() {
        return new Queue("homeflex.booking.events", true);
    }

    @Bean
    Binding bookingEventsBinding(Queue bookingEventsQueue, TopicExchange outboxExchange) {
        return BindingBuilder.bind(bookingEventsQueue).to(outboxExchange).with("Booking.#");
    }

    // ── Property event queue ─────────────────────────────────────────────

    @Bean
    Queue propertyEventsQueue() {
        return new Queue("homeflex.property.events", true);
    }

    @Bean
    Binding propertyEventsBinding(Queue propertyEventsQueue, TopicExchange outboxExchange) {
        return BindingBuilder.bind(propertyEventsQueue).to(outboxExchange).with("Property.#");
    }

    // ── Notification event queue ─────────────────────────────────────────

    @Bean
    Queue notificationEventsQueue() {
        return new Queue("homeflex.notification.events", true);
    }

    @Bean
    Binding notificationEventsBinding(Queue notificationEventsQueue, TopicExchange outboxExchange) {
        return BindingBuilder.bind(notificationEventsQueue).to(outboxExchange).with("*.#");
    }

    // ── Serialization ────────────────────────────────────────────────────

    @Bean
    @SuppressWarnings("removal")
    MessageConverter jacksonMessageConverter() {
        // Jackson2JsonMessageConverter works with com.fasterxml.jackson (current project dependency).
        // JacksonJsonMessageConverter requires tools.jackson (Spring Boot 4's repackaged namespace).
        // Using the fasterxml variant until the project migrates to tools.jackson.
        var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        return new org.springframework.amqp.support.converter.Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                 MessageConverter jacksonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonMessageConverter);
        template.setExchange(appProperties.getOutbox().getExchangeName());
        template.setMandatory(true);
        return template;
    }
}
