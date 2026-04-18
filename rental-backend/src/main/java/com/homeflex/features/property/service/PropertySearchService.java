package com.homeflex.features.property.service;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import com.homeflex.core.dto.common.ApiPageResponse;
import com.homeflex.features.property.domain.document.PropertyDocument;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.enums.PropertyStatus;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.features.property.dto.response.PropertyDto;
import com.homeflex.features.property.mapper.PropertyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.GeoDistanceOrder;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Full-text and geo-spatial property search backed by Elasticsearch.
 *
 * <h3>Query strategy</h3>
 * <ul>
 *   <li><strong>Fuzzy matching</strong> — the {@code q} parameter is matched
 *       against the {@code title} field with {@code fuzziness: AUTO}
 *       (edit-distance 1 for short terms, 2 for longer).</li>
 *   <li><strong>Faceted filtering</strong> — {@code propertyType} and
 *       {@code city} are applied as keyword term filters inside the
 *       {@code bool.filter} clause so they don't affect relevance scoring.</li>
 *   <li><strong>Geo-distance sorting</strong> — when {@code lat}/{@code lng}
 *       are provided the result set is sorted by ascending distance from
 *       that point (kilometres).</li>
 * </ul>
 *
 * <h3>Data freshness</h3>
 * Elasticsearch is used only for hit selection and ordering.
 * The returned {@link PropertyDto} instances are always loaded from
 * PostgreSQL so the response is consistent with the source of truth.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PropertySearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;

    @Transactional(readOnly = true)
    public ApiPageResponse<PropertyDto> search(String q,
                                               String propertyType,
                                               String city,
                                               Double minPrice,
                                               Double maxPrice,
                                               Integer bedrooms,
                                               Integer bathrooms,
                                               List<String> amenityIds,
                                               Double lat,
                                               Double lng,
                                               Pageable pageable) {

        List<UUID> ids;
        long totalHits;
        try {
            NativeQuery query = buildQuery(q, propertyType, city, minPrice, maxPrice,
                    bedrooms, bathrooms, amenityIds, lat, lng, pageable);

            SearchHits<PropertyDocument> hits =
                    elasticsearchOperations.search(query, PropertyDocument.class);

            ids = hits.getSearchHits().stream()
                    .map(hit -> UUID.fromString(hit.getContent().getId()))
                    .toList();
            totalHits = hits.getTotalHits();
        } catch (Exception e) {
            log.warn("Elasticsearch search failed, falling back to PostgreSQL: {}", e.getMessage());
            ids = List.of();
            totalHits = 0;
        }

        // Fallback: when ES is empty or unavailable, serve approved properties straight from PostgreSQL.
        // This keeps the landing page resilient before the outbox relay has indexed new listings.
        if (ids.isEmpty()) {
            return searchFromDatabase(pageable);
        }

        // Batch-fetch full entities from PostgreSQL
        List<Property> properties = propertyRepository.findAllById(ids);

        // Maintain the sort order returned by Elasticsearch
        Map<UUID, Property> byId = properties.stream()
                .collect(Collectors.toMap(Property::getId, Function.identity()));

        List<PropertyDto> dtos = ids.stream()
                .map(byId::get)
                .filter(Objects::nonNull)
                .map(this::initializeAndMap)
                .toList();

        int totalPages = Math.max(1,
                (int) Math.ceil((double) totalHits / pageable.getPageSize()));

        return new ApiPageResponse<>(dtos, pageable.getPageNumber(),
                pageable.getPageSize(), totalHits, totalPages);
    }

    private ApiPageResponse<PropertyDto> searchFromDatabase(Pageable pageable) {
        Pageable capped = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort()
        );
        Page<Property> page = propertyRepository.findByStatus(PropertyStatus.APPROVED, capped);
        List<PropertyDto> dtos = page.getContent().stream()
                .map(this::initializeAndMap)
                .toList();
        return new ApiPageResponse<>(
                dtos,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                Math.max(1, page.getTotalPages())
        );
    }

    // ── private helpers ────────────────────────────────────────────────

    private NativeQuery buildQuery(String q, String propertyType, String city,
                                   Double minPrice, Double maxPrice,
                                   Integer bedrooms, Integer bathrooms,
                                   List<String> amenityIds,
                                   Double lat, Double lng,
                                   Pageable pageable) {

        var builder = NativeQuery.builder()
                .withQuery(qb -> qb.bool(bool -> {
                    buildBool(bool, q, propertyType, city, minPrice, maxPrice,
                            bedrooms, bathrooms, amenityIds);
                    return bool;
                }));

        // Geo-distance sort when coordinates are provided
        if (lat != null && lng != null) {
            Sort geoSort = Sort.by(new GeoDistanceOrder("location",
                    new GeoPoint(lat, lng)).withUnit("km"));
            builder.withSort(geoSort);
        }

        builder.withPageable(pageable);
        return builder.build();
    }

    private void buildBool(BoolQuery.Builder bool, String q,
                           String propertyType, String city,
                           Double minPrice, Double maxPrice,
                           Integer bedrooms, Integer bathrooms,
                           List<String> amenityIds) {

        // Hard filters — only approved + available
        bool.filter(f -> f.term(t -> t.field("status").value("APPROVED")));
        bool.filter(f -> f.term(t -> t.field("available").value(true)));

        // Fuzzy text match on title
        if (q != null && !q.isBlank()) {
            bool.must(m -> m.match(mt -> mt
                    .field("title")
                    .query(q)
                    .fuzziness("AUTO")
            ));
        }

        // Faceted keyword filters
        if (propertyType != null && !propertyType.isBlank()) {
            bool.filter(f -> f.term(t -> t
                    .field("propertyType")
                    .value(propertyType.toUpperCase())
            ));
        }

        if (city != null && !city.isBlank()) {
            bool.filter(f -> f.term(t -> t.field("city").value(city)));
        }

        // Price range
        if (minPrice != null || maxPrice != null) {
            bool.filter(f -> f.range(r -> {
                var number = r.number(n -> {
                    n.field("price");
                    if (minPrice != null) n.gte(minPrice);
                    if (maxPrice != null) n.lte(maxPrice);
                    return n;
                });
                return r;
            }));
        }

        // Bedrooms / bathrooms minimum
        if (bedrooms != null) {
            bool.filter(f -> f.range(r -> r
                    .number(n -> n.field("bedrooms").gte(bedrooms.doubleValue()))
            ));
        }

        if (bathrooms != null) {
            bool.filter(f -> f.range(r -> r
                    .number(n -> n.field("bathrooms").gte(bathrooms.doubleValue()))
            ));
        }

        // Amenities filter (must have all selected amenities)
        if (amenityIds != null && !amenityIds.isEmpty()) {
            for (String amenityId : amenityIds) {
                bool.filter(f -> f.term(t -> t.field("amenityIds").value(amenityId)));
            }
        }
    }

    private PropertyDto initializeAndMap(Property property) {
        property.getImages().size();
        property.getVideos().size();
        property.getAmenities().size();
        property.getLandlord().getEmail();
        return propertyMapper.toDto(property);
    }
}
