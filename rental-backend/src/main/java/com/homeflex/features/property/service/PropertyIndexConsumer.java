package com.homeflex.features.property.service;

import com.homeflex.core.dto.event.OutboxEventMessage;
import com.homeflex.features.property.config.PropertySearchConfig;
import com.homeflex.features.property.domain.document.PropertyDocument;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.features.property.domain.repository.PropertySearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Component;

/**
 * Consumes {@code Property.PropertyIndexed} events from RabbitMQ
 * and upserts the corresponding document into Elasticsearch.
 * <p>
 * If the property has been soft-deleted, the document is removed
 * from the index instead.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PropertyIndexConsumer {

    private final PropertyRepository propertyRepository;
    private final PropertySearchRepository propertySearchRepository;

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    @RabbitListener(queues = PropertySearchConfig.QUEUE_NAME)
    public void onPropertyIndexed(OutboxEventMessage message) {
        String propertyId = message.aggregateId().toString();

        try {
            log.debug("Received PropertyIndexed event for property {}", propertyId);

            Property property = propertyRepository.findById(message.aggregateId()).orElse(null);

            if (property == null || property.getDeletedAt() != null) {
                propertySearchRepository.deleteById(propertyId);
                log.info("Removed property {} from search index (deleted or not found)", propertyId);
                return;
            }

            PropertyDocument doc = toDocument(property);
            propertySearchRepository.save(doc);
            log.info("Indexed property {} into Elasticsearch", propertyId);
        } catch (Exception e) {
            log.error("Failed to index property {} into Elasticsearch: {}", propertyId, e.getMessage());
            // Reject and don't requeue to trigger DLX/DLQ
            throw new org.springframework.amqp.AmqpRejectAndDontRequeueException("Elasticsearch indexing failed", e);
        }
    }

    private PropertyDocument toDocument(Property property) {
        GeoPoint location = null;
        if (property.getLatitude() != null && property.getLongitude() != null) {
            location = new GeoPoint(
                    property.getLatitude().doubleValue(),
                    property.getLongitude().doubleValue()
            );
        }

        return PropertyDocument.builder()
                .id(property.getId().toString())
                .title(property.getTitle())
                .description(property.getDescription())
                .propertyType(property.getPropertyType().name())
                .listingType(property.getListingType().name())
                .price(property.getPrice().doubleValue())
                .currency(property.getCurrency())
                .city(property.getCity())
                .country(property.getCountry())
                .status(property.getStatus().name())
                .available(Boolean.TRUE.equals(property.getIsAvailable()))
                .bedrooms(property.getBedrooms())
                .bathrooms(property.getBathrooms())
                .areaSqm(property.getAreaSqm() != null ? property.getAreaSqm().doubleValue() : null)
                .amenityIds(property.getAmenities().stream()
                        .map(a -> a.getId().toString())
                        .collect(java.util.stream.Collectors.toList()))
                .location(location)
                .createdAt(property.getCreatedAt())
                .build();
    }
}
