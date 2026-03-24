package com.realestate.rental.controller;

import com.realestate.rental.repository.BookingRepository;
import com.realestate.rental.repository.PropertyRepository;
import com.realestate.rental.repository.UserRepository;
import com.realestate.rental.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final PropertyService propertyService;

    @GetMapping
    public ResponseEntity<Map<String, Long>> getStats() {
        Map<String, Long> stats = propertyService.getStats();
        return ResponseEntity.ok(stats);
    }
}
