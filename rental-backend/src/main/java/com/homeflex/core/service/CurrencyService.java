package com.homeflex.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CurrencyService {

    // Base currency is USD
    private final Map<String, BigDecimal> exchangeRates = new HashMap<>();

    public CurrencyService() {
        // Initialize with some mock rates for prototype
        exchangeRates.put("USD", BigDecimal.ONE);
        exchangeRates.put("EUR", new BigDecimal("0.92"));
        exchangeRates.put("GBP", new BigDecimal("0.79"));
        exchangeRates.put("XAF", new BigDecimal("605.00"));
        exchangeRates.put("AED", new BigDecimal("3.67"));
        exchangeRates.put("SAR", new BigDecimal("3.75"));
    }

    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return amount;
        }

        BigDecimal fromRate = exchangeRates.get(fromCurrency.toUpperCase());
        BigDecimal toRate = exchangeRates.get(toCurrency.toUpperCase());

        if (fromRate == null || toRate == null) {
            log.warn("Exchange rate not found for {} or {}. Returning original amount.", fromCurrency, toCurrency);
            return amount;
        }

        // amount * (toRate / fromRate)
        BigDecimal amountInUsd = amount.divide(fromRate, 10, RoundingMode.HALF_UP);
        return amountInUsd.multiply(toRate).setScale(2, RoundingMode.HALF_UP);
    }

    public Map<String, BigDecimal> getAllRates() {
        return new HashMap<>(exchangeRates);
    }
}
