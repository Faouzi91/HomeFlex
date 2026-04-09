---
name: spring-service
description: >
  Generate Spring Boot service classes with transactional boundaries, Resilience4j patterns,
  and clean domain logic. Trigger this skill for any request to create a service class, business logic
  layer, use case, application service, or when adding operations like "create X", "update X",
  "delete X", "find X". Also trigger for "add retry logic", "add circuit breaker", or
  "handle X failure gracefully". Always apply this — never freehand service generation.
---

# Spring Service Generation

## Stack Assumptions

- Spring Boot 4.x · Spring Data JPA · Resilience4j · Lombok
- Services live in `application/<module>/` (application layer)
- Domain logic belongs in entities or domain services — not here
- One service class per aggregate root

---

## Service Template

```java
package com.example.application.<module>;

import com.example.application.<module>.dto.*;
import com.example.application.<module>.mapper.<Entity>Mapper;
import com.example.domain.<module>.<Entity>;
import com.example.domain.<module>.<Entity>Repository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)   // Default: all methods are read-only
public class <Entity>Service {

    private final <Entity>Repository <entity>Repository;
    private final <Entity>Mapper <entity>Mapper;

    // ── Queries (readOnly = true inherited) ─────────────────────────────────

    public <Entity>Response findById(Long id) {
        return <entity>Repository.findById(id)
            .map(<entity>Mapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("<Entity>", id));
    }

    public PageResponse<<Entity>Response> findAll(Pageable pageable) {
        Page<<Entity>Response> page = <entity>Repository.findAll(pageable)
            .map(<entity>Mapper::toResponse);
        return PageResponse.from(page);
    }

    // ── Commands (explicit @Transactional) ──────────────────────────────────

    @Transactional
    public <Entity>Response create(<Entity>CreateRequest request) {
        log.info("Creating <entity>: {}", request);
        <Entity> entity = <entity>Mapper.toEntity(request);
        <Entity> saved = <entity>Repository.save(entity);
        return <entity>Mapper.toResponse(saved);
    }

    @Transactional
    public <Entity>Response update(Long id, <Entity>UpdateRequest request) {
        <Entity> entity = <entity>Repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("<Entity>", id));
        <entity>Mapper.updateEntity(request, entity);
        return <entity>Mapper.toResponse(entity);   // No explicit save needed (dirty checking)
    }

    @Transactional
    public void delete(Long id) {
        if (!<entity>Repository.existsById(id)) {
            throw new ResourceNotFoundException("<Entity>", id);
        }
        <entity>Repository.deleteById(id);
    }
}
```

---

## Resilience4j Patterns

### Retry (transient failures — external HTTP, flaky DB calls)

```java
@Retry(name = "<entity>-service", fallbackMethod = "findByIdFallback")
public <Entity>Response findById(Long id) { ... }

private <Entity>Response findByIdFallback(Long id, Exception ex) {
    log.warn("Fallback triggered for <entity> id={}: {}", id, ex.getMessage());
    throw new ServiceUnavailableException("Service temporarily unavailable");
}
```

`application.yml`:

```yaml
resilience4j:
  retry:
    instances:
      <entity>-service:
        max-attempts: 3
        wait-duration: 500ms
        retry-exceptions:
          - java.io.IOException
          - org.springframework.dao.TransientDataAccessException
```

### Circuit Breaker (downstream service calls)

```java
@CircuitBreaker(name = "<entity>-service", fallbackMethod = "externalCallFallback")
public ExternalResponse callExternalService(String param) { ... }

private ExternalResponse externalCallFallback(String param, Exception ex) {
    log.error("Circuit open for <entity>-service: {}", ex.getMessage());
    return ExternalResponse.empty();
}
```

`application.yml`:

```yaml
resilience4j:
  circuit-breaker:
    instances:
      <entity>-service:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
        permitted-number-of-calls-in-half-open-state: 3
```

---

## Exception Handling

### Standard Exceptions

```java
// ResourceNotFoundException.java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " not found with id: " + id);
    }
}

// BusinessException.java (domain rule violations)
public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}

// ServiceUnavailableException.java
public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) { super(message); }
}
```

These are caught by a `@RestControllerAdvice` — generate that separately if needed.

---

## Transactional Rules

| Method type                    | Annotation                                                |
| ------------------------------ | --------------------------------------------------------- |
| Read-only (select, find, list) | Class-level `@Transactional(readOnly = true)` (inherited) |
| Write (create, update, delete) | Method-level `@Transactional`                             |
| External HTTP calls            | No `@Transactional` — don't hold DB connections           |
| Cascading multiple services    | Orchestrate at the service level, not repository          |

**Never** call `save()` after an update inside an existing transaction — Hibernate dirty checking handles it.

---

## MinIO (file upload pattern)

```java
@Transactional
public <Entity>Response attachFile(Long id, MultipartFile file) {
    <Entity> entity = <entity>Repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("<Entity>", id));

    String key = "entities/" + id + "/" + file.getOriginalFilename();
    minioService.upload(bucket, key, file);

    entity.setFileKey(key);
    return <entity>Mapper.toResponse(entity);
}
```

---

## Package Layout

```
src/main/java/com/example/
└── application/
    └── <module>/
        ├── <Entity>Service.java
        ├── dto/
        └── mapper/
```

---

## Checklist Before Outputting

- [ ] Class annotated `@Transactional(readOnly = true)`
- [ ] Write methods override with `@Transactional`
- [ ] `@RequiredArgsConstructor` (no `@Autowired`)
- [ ] `@Slf4j` for logging
- [ ] `ResourceNotFoundException` thrown for missing entities
- [ ] No `save()` inside update (dirty checking)
- [ ] Resilience4j applied for external calls
- [ ] Fallback method signature matches primary method + `Exception` param
