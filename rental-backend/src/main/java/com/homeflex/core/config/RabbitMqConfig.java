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

    @Bean
    TopicExchange deadLetterExchange() {
        return new TopicExchange("homeflex.dead-letter.exchange", true, false);
    }

    // ── Booking event queue ─────────────────────────────────────────────

    @Bean
    Queue bookingEventsQueue() {
        return org.springframework.amqp.core.QueueBuilder.durable("homeflex.booking.events")
                .withArgument("x-dead-letter-exchange", "homeflex.dead-letter.exchange")
                .withArgument("x-dead-letter-routing-key", "dead-letter.booking")
                .build();
    }

    @Bean
    Queue bookingEventsDlq() {
        return new Queue("homeflex.booking.events.dlq", true);
    }

    @Bean
    Binding bookingEventsBinding(Queue bookingEventsQueue, TopicExchange outboxExchange) {
        return BindingBuilder.bind(bookingEventsQueue).to(outboxExchange).with("Booking.#");
    }

    @Bean
    Binding bookingEventsDlqBinding(Queue bookingEventsDlq, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(bookingEventsDlq).to(deadLetterExchange).with("dead-letter.booking");
    }

    // ── Property event queue ─────────────────────────────────────────────

    @Bean
    Queue propertyEventsQueue() {
        return org.springframework.amqp.core.QueueBuilder.durable("homeflex.property.events")
                .withArgument("x-dead-letter-exchange", "homeflex.dead-letter.exchange")
                .withArgument("x-dead-letter-routing-key", "dead-letter.property")
                .build();
    }

    @Bean
    Queue propertyEventsDlq() {
        return new Queue("homeflex.property.events.dlq", true);
    }

    @Bean
    Binding propertyEventsBinding(Queue propertyEventsQueue, TopicExchange outboxExchange) {
        return BindingBuilder.bind(propertyEventsQueue).to(outboxExchange).with("Property.#");
    }

    @Bean
    Binding propertyEventsDlqBinding(Queue propertyEventsDlq, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(propertyEventsDlq).to(deadLetterExchange).with("dead-letter.property");
    }

    // ── Notification event queue ─────────────────────────────────────────

    @Bean
    Queue notificationEventsQueue() {
        return org.springframework.amqp.core.QueueBuilder.durable("homeflex.notification.events")
                .withArgument("x-dead-letter-exchange", "homeflex.dead-letter.exchange")
                .withArgument("x-dead-letter-routing-key", "dead-letter.notification")
                .build();
    }

    @Bean
    Queue notificationEventsDlq() {
        return new Queue("homeflex.notification.events.dlq", true);
    }

    @Bean
    Binding notificationEventsBinding(Queue notificationEventsQueue, TopicExchange outboxExchange) {
        return BindingBuilder.bind(notificationEventsQueue).to(outboxExchange).with("*.#");
    }

    @Bean
    Binding notificationEventsDlqBinding(Queue notificationEventsDlq, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(notificationEventsDlq).to(deadLetterExchange).with("dead-letter.notification");
    }

    // ── Serialization ────────────────────────────────────────────────────

    @Bean
    MessageConverter jacksonMessageConverter(com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        return new org.springframework.amqp.support.converter.Jackson2JsonMessageConverter(objectMapper);
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
