package com.realestate.rental.controller;

import com.realestate.rental.dto.api.ApiValueResponse;
import com.realestate.rental.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {

    private final PropertyService propertyService;

    @GetMapping
    public ResponseEntity<ApiValueResponse<Map<String, Long>>> getStats() {
        Map<String, Long> stats = propertyService.getStats();
        return ResponseEntity.ok(new ApiValueResponse<>(stats));
    }
}
