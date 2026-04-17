package com.homeflex.core.api.v1;

import com.homeflex.core.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/config")
@RequiredArgsConstructor
public class AppConfigController {

    private final AppProperties appProperties;

    @GetMapping
    public ResponseEntity<Map<String, String>> getPublicConfig() {
        return ResponseEntity.ok(Map.of(
                "stripePublishableKey", appProperties.getStripe().getPublishableKey() != null
                        ? appProperties.getStripe().getPublishableKey()
                        : ""
        ));
    }
}
