package com.homeflex.core.api.v1;

import com.homeflex.core.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/currencies")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("/rates")
    public ResponseEntity<Map<String, BigDecimal>> getRates() {
        return ResponseEntity.ok(currencyService.getAllRates());
    }

    @GetMapping("/convert")
    public ResponseEntity<BigDecimal> convert(
            @RequestParam BigDecimal amount,
            @RequestParam String from,
            @RequestParam String to) {
        return ResponseEntity.ok(currencyService.convert(amount, from, to));
    }
}
