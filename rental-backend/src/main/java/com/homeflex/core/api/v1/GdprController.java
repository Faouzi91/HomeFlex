package com.homeflex.core.api.v1;

import com.homeflex.core.service.GdprService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/gdpr")
@RequiredArgsConstructor
public class GdprController {

    private final GdprService gdprService;

    @GetMapping("/export")
    public ResponseEntity<Map<String, Object>> exportMyData(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(gdprService.exportUserData(userId));
    }

    @DeleteMapping("/erase")
    public ResponseEntity<Void> eraseMyData(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        gdprService.eraseUserData(userId);
        return ResponseEntity.noContent().build();
    }
}
