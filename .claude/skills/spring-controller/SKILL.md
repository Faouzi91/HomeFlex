---
name: spring-controller
description: >
  Generate Spring Boot REST controllers with full OpenAPI/Swagger annotations, validation,
  pagination support, and proper HTTP semantics. Trigger for any request to create a controller,
  REST endpoint, API route, resource handler, or when the user says "expose X as an API",
  "add an endpoint for X", "create a CRUD controller", or "I need a REST API for X".
  Always apply this skill — never freehand controller generation.
---

# Spring REST Controller Generation

## Stack Assumptions

- Spring Boot 4.x · SpringDoc OpenAPI 3.x · Jakarta Validation
- Controllers are thin — delegate everything to service layer
- Versioned under `/api/v1/`
- Standard HTTP status codes enforced

---

## Controller Template

```java
package com.example.api.<module>;

import com.example.application.<module>.<Entity>Service;
import com.example.application.<module>.dto.*;
import com.example.shared.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/<entities>")
@RequiredArgsConstructor
@Tag(name = "<Entity> Management", description = "Operations for managing <entities>")
public class <Entity>Controller {

    private final <Entity>Service <entity>Service;

    // ── GET /api/v1/<entities> ───────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "List all <entities>", description = "Returns a paginated list of <entities>")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    public ResponseEntity<PageResponse<<Entity>Response>> findAll(
        @ParameterObject @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(<entity>Service.findAll(pageable));
    }

    // ── GET /api/v1/<entities>/{id} ──────────────────────────────────────────

    @GetMapping("/{id}")
    @Operation(summary = "Get <entity> by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "<Entity> found"),
        @ApiResponse(responseCode = "404", description = "<Entity> not found")
    })
    public ResponseEntity<<Entity>Response> findById(
        @Parameter(description = "<Entity> ID", required = true) @PathVariable Long id
    ) {
        return ResponseEntity.ok(<entity>Service.findById(id));
    }

    // ── POST /api/v1/<entities> ──────────────────────────────────────────────

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new <entity>")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "<Entity> created"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<<Entity>Response> create(
        @Valid @RequestBody <Entity>CreateRequest request
    ) {
        <Entity>Response created = <entity>Service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ── PUT /api/v1/<entities>/{id} ──────────────────────────────────────────

    @PutMapping("/{id}")
    @Operation(summary = "Update <entity>")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "<Entity> updated"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "404", description = "<Entity> not found")
    })
    public ResponseEntity<<Entity>Response> update(
        @PathVariable Long id,
        @Valid @RequestBody <Entity>UpdateRequest request
    ) {
        return ResponseEntity.ok(<entity>Service.update(id, request));
    }

    // ── DELETE /api/v1/<entities>/{id} ───────────────────────────────────────

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete <entity>")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "<Entity> deleted"),
        @ApiResponse(responseCode = "404", description = "<Entity> not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        <entity>Service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## Global Exception Handler

Always pair controllers with this advice (generate once per project):

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(404, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .toList();
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(400, "Validation failed", errors));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(new ErrorResponse(422, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(500, "Internal server error"));
    }
}
```

```java
public record ErrorResponse(int status, String message, List<String> errors) {
    public ErrorResponse(int status, String message) { this(status, message, List.of()); }
}
```

---

## HTTP Semantics Reference

| Action           | Method   | URL                                  | Success Status |
| ---------------- | -------- | ------------------------------------ | -------------- |
| List (paginated) | GET      | `/api/v1/<entities>`                 | 200            |
| Get by ID        | GET      | `/api/v1/<entities>/{id}`            | 200            |
| Create           | POST     | `/api/v1/<entities>`                 | 201            |
| Full update      | PUT      | `/api/v1/<entities>/{id}`            | 200            |
| Partial update   | PATCH    | `/api/v1/<entities>/{id}`            | 200            |
| Delete           | DELETE   | `/api/v1/<entities>/{id}`            | 204            |
| Subresource      | GET/POST | `/api/v1/<entities>/{id}/<children>` | 200/201        |

---

## OpenAPI Config (once per project)

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("My App API")
                .version("1.0.0")
                .description("REST API documentation"))
            .addSecurityItem(new SecurityRequirement().addList("Bearer"))
            .components(new Components()
                .addSecuritySchemes("Bearer", new SecurityScheme()
                    .name("Authorization")
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
    }
}
```

---

## Package Layout

```
src/main/java/com/example/
└── api/
    └── <module>/
        └── <Entity>Controller.java
```

---

## Checklist Before Outputting

- [ ] `@RequestMapping("/api/v1/<entities>")` with correct plural noun
- [ ] `@Tag` on class for OpenAPI grouping
- [ ] `@Operation` + `@ApiResponse` on every method
- [ ] `@Valid` on every `@RequestBody`
- [ ] `@ParameterObject` + `Pageable` for list endpoints
- [ ] `ResponseEntity` used consistently
- [ ] POST returns 201, DELETE returns 204
- [ ] No business logic in controller — delegate to service
- [ ] `GlobalExceptionHandler` exists in project
