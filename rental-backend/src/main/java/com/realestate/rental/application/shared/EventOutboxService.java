package com.realestate.rental.application.shared;

import com.realestate.rental.domain.shared.event.OutboxEvent;
import com.realestate.rental.domain.shared.event.OutboxEventRepository;
import com.realestate.rental.shared.exception.DomainException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventOutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void enqueue(String aggregateType, UUID aggregateId, String eventType, Object payload) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setAggregateType(aggregateType);
        outboxEvent.setAggregateId(aggregateId);
        outboxEvent.setEventType(eventType);
        outboxEvent.setPayload(toJson(payload));
        outboxEventRepository.save(outboxEvent);
    }

    private String toJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new DomainException("Failed to serialize outbox payload");
        }
    }
}
