---
name: spring-dto
description: >
  Generate DTOs (request/response), MapStruct mappers, and validation annotations for Spring Boot
  projects using a DTO-first design pattern. Trigger this skill whenever the user asks to create
  a DTO, request/response object, mapper, MapStruct interface, API payload, or data transfer class.
  Also trigger for "add validation to X", "map entity to DTO", or "I need a payload for X".
  Always apply this — never freehand DTO or mapper generation.
---

# Spring DTO + MapStruct Generation

## Stack Assumptions

- Spring Boot 4.x · Jakarta Validation · Lombok · MapStruct 1.5+
- `@Valid` on controller params
- Records preferred for immutable responses; classes for mutable requests

---

## Naming Convention

| Purpose         | Name Pattern            | Example                      |
| --------------- | ----------------------- | ---------------------------- |
| Create input    | `<Entity>CreateRequest` | `UserCreateRequest`          |
| Update input    | `<Entity>UpdateRequest` | `UserUpdateRequest`          |
| API response    | `<Entity>Response`      | `UserResponse`               |
| Nested/embedded | `<Entity>Summary`       | `UserSummary`                |
| Paginated list  | `PageResponse<T>`       | `PageResponse<UserResponse>` |

---

## Request DTO (mutable — use class)

```java
package com.example.application.<module>.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class <Entity>CreateRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotNull(message = "Status is required")
    private <EnumType> status;

    @Email(message = "Invalid email format")
    private String email;

    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}
```

## Response DTO (immutable — use record)

```java
package com.example.application.<module>.dto;

import java.time.Instant;

public record <Entity>Response(
    Long id,
    String name,
    <EnumType> status,
    Instant createdAt,
    Instant updatedAt
) {}
```

---

## MapStruct Mapper

```java
package com.example.application.<module>.mapper;

import com.example.domain.<module>.<Entity>;
import com.example.application.<module>.dto.*;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface <Entity>Mapper {

    <Entity>Response toResponse(<Entity> entity);

    List<<Entity>Response> toResponseList(List<<Entity>> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    <Entity> toEntity(<Entity>CreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(<Entity>UpdateRequest request, @MappingTarget <Entity> entity);
}
```

---

## PageResponse Wrapper

```java
public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean last
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast()
        );
    }
}
```

---

## Validation Annotations Reference

| Constraint                      | Use case                               |
| ------------------------------- | -------------------------------------- |
| `@NotNull`                      | Object fields that must not be null    |
| `@NotBlank`                     | Strings that must have content         |
| `@Size(min, max)`               | String/collection length               |
| `@Min` / `@Max`                 | Numeric range                          |
| `@Positive` / `@PositiveOrZero` | Amounts, quantities                    |
| `@Email`                        | Email format                           |
| `@Pattern(regexp)`              | Custom regex                           |
| `@Valid`                        | Cascade validation into nested objects |
| `@Future` / `@Past`             | Date constraints                       |

---

## Rules

### DTO-First Design

- **Never expose entities directly in controllers** — always map through DTOs
- Create separate `CreateRequest` and `UpdateRequest` — don't reuse them
- Response DTOs should be records (immutable, concise)

### MapStruct Config

- `componentModel = "spring"` — always, so Spring injects the mapper
- `nullValuePropertyMappingStrategy = IGNORE` on update mappings — partial update support
- `ReportingPolicy.ERROR` — compile-time failure if fields are unmapped (fail fast)
- Always ignore `id`, `createdAt`, `updatedAt` in `toEntity()` / `updateEntity()`

### Nested Objects

When an entity has a relationship, map nested to a summary record:

```java
@Mapping(target = "parentName", source = "parent.name")
<Entity>Response toResponse(<Entity> entity);
```

### Package Layout

```
src/main/java/com/example/
└── application/
    └── <module>/
        ├── dto/
        │   ├── <Entity>CreateRequest.java
        │   ├── <Entity>UpdateRequest.java
        │   └── <Entity>Response.java
        └── mapper/
            └── <Entity>Mapper.java
```

---

## Checklist Before Outputting

- [ ] Separate Create/Update/Response DTOs
- [ ] Records for responses, classes for requests
- [ ] Jakarta validation on all request fields
- [ ] Mapper uses `componentModel = "spring"`
- [ ] `id`, `createdAt`, `updatedAt` ignored in `toEntity`
- [ ] `updateEntity` uses `@MappingTarget` + `NullValuePropertyMappingStrategy.IGNORE`
- [ ] `unmappedTargetPolicy = ReportingPolicy.ERROR`
