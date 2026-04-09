package com.homeflex.core.service;

import com.homeflex.core.domain.entity.ProcessedStripeEvent;
import com.homeflex.core.domain.repository.ProcessedStripeEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeEventService {

    private final ProcessedStripeEventRepository processedEventRepository;

    @Transactional(readOnly = true)
    public boolean isProcessed(String eventId) {
        return processedEventRepository.existsById(eventId);
    }

    @Transactional
    public void recordProcessed(String eventId, String eventType) {
        ProcessedStripeEvent record = new ProcessedStripeEvent();
        record.setEventId(eventId);
        record.setEventType(eventType);
        processedEventRepository.save(record);
        log.info("Recorded Stripe event {} as processed", eventId);
    }
}
