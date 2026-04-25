package com.homeflex.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Converts a free-text address to WGS-84 coordinates using the Nominatim
 * (OpenStreetMap) geocoding API — no API key required.
 *
 * Nominatim usage policy: max 1 req/s, must set a meaningful User-Agent.
 * Property creation is a low-frequency write path so rate limits are fine.
 */
@Slf4j
@Service
public class GeocodingService {

    private final RestClient restClient;

    public GeocodingService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://nominatim.openstreetmap.org")
                .defaultHeader("User-Agent", "HomeFlex/1.0 (contact@homeflex.com)")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    /**
     * Returns [latitude, longitude] for the given address, or empty when the
     * query yields no results or the request fails.  Never throws.
     */
    public Optional<BigDecimal[]> geocode(String address, String city, String country) {
        String query = buildQuery(address, city, country);
        if (query.isBlank()) return Optional.empty();

        try {
            JsonNode results = restClient.get()
                    .uri("/search?q={q}&format=json&limit=1&addressdetails=0", query)
                    .retrieve()
                    .body(JsonNode.class);

            if (results != null && results.isArray() && !results.isEmpty()) {
                JsonNode first = results.get(0);
                BigDecimal lat = new BigDecimal(first.get("lat").asText());
                BigDecimal lon = new BigDecimal(first.get("lon").asText());
                log.debug("Geocoded '{}' -> ({}, {})", query, lat, lon);
                return Optional.of(new BigDecimal[]{lat, lon});
            }
        } catch (Exception e) {
            log.warn("Geocoding failed for '{}': {}", query, e.getMessage());
        }
        return Optional.empty();
    }

    private String buildQuery(String address, String city, String country) {
        StringBuilder sb = new StringBuilder();
        if (address != null && !address.isBlank()) sb.append(address).append(", ");
        if (city != null && !city.isBlank()) sb.append(city).append(", ");
        if (country != null && !country.isBlank()) sb.append(country);
        return sb.toString().replaceAll(",\\s*$", "").trim();
    }
}
