package com.homeflex.core.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CurrencyService {

    private static final String RATES_CACHE = "exchangeRates";
    private static final String RATE_API_URL = "https://open.er-api.com/v6/latest/USD";

    private final RestClient restClient;
    private final Map<String, BigDecimal> cachedRates = new ConcurrentHashMap<>();

    public CurrencyService() {
        this.restClient = RestClient.create();
        // Seed with static fallback rates, then attempt live fetch
        seedFallbackRates();
        refreshRates();
    }

    @Cacheable(value = RATES_CACHE)
    public Map<String, BigDecimal> getAllRates() {
        return new HashMap<>(cachedRates);
    }

    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return amount;
        }

        BigDecimal fromRate = cachedRates.get(fromCurrency.toUpperCase());
        BigDecimal toRate = cachedRates.get(toCurrency.toUpperCase());

        if (fromRate == null || toRate == null) {
            log.warn("Exchange rate not found for {} or {}. Returning original amount.", fromCurrency, toCurrency);
            return amount;
        }

        BigDecimal amountInUsd = amount.divide(fromRate, 10, RoundingMode.HALF_UP);
        return amountInUsd.multiply(toRate).setScale(2, RoundingMode.HALF_UP);
    }

    /** Refresh rates from external API every hour. */
    @Scheduled(fixedRate = 3_600_000, initialDelay = 3_600_000)
    public void refreshRates() {
        try {
            ExchangeRateResponse response = restClient.get()
                    .uri(RATE_API_URL)
                    .retrieve()
                    .body(ExchangeRateResponse.class);

            if (response != null && "success".equalsIgnoreCase(response.result) && response.rates != null) {
                cachedRates.clear();
                response.rates.forEach((currency, rate) ->
                        cachedRates.put(currency.toUpperCase(), BigDecimal.valueOf(rate)));
                log.info("Exchange rates refreshed successfully — {} currencies loaded.", cachedRates.size());
            } else {
                log.warn("Exchange rate API returned unexpected response. Keeping cached rates.");
            }
        } catch (Exception e) {
            log.warn("Failed to fetch live exchange rates: {}. Using fallback.", e.getMessage());
        }
    }

    private void seedFallbackRates() {
        cachedRates.put("USD", BigDecimal.ONE);
        cachedRates.put("EUR", new BigDecimal("0.92"));
        cachedRates.put("GBP", new BigDecimal("0.79"));
        cachedRates.put("XAF", new BigDecimal("605.00"));
        cachedRates.put("AED", new BigDecimal("3.67"));
        cachedRates.put("SAR", new BigDecimal("3.75"));
        cachedRates.put("NGN", new BigDecimal("1550.00"));
        cachedRates.put("CAD", new BigDecimal("1.36"));
        cachedRates.put("CHF", new BigDecimal("0.88"));
        cachedRates.put("JPY", new BigDecimal("154.50"));
        cachedRates.put("CNY", new BigDecimal("7.24"));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ExchangeRateResponse {
        @JsonProperty("result")
        public String result;

        @JsonProperty("rates")
        public Map<String, Double> rates;
    }
}
