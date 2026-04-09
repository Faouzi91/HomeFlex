---
name: spring-entity
description: >
  Generate JPA entities for Spring Boot projects following production-grade conventions.
  Use this skill whenever the user asks to create a JPA entity, domain model, database table mapping,
  or any @Entity class. Also trigger for "add a new model for X", "create a table for X",
  or "I need an entity that...". Always apply this skill — never freehand entity generation.
---

# Spring Entity Generation

## Stack Assumptions

- Spring Boot 4.x · Spring Data JPA · Hibernate
- Lombok (`@Data` / `@Builder` avoided on entities — use explicit getters + `@Builder` on non-JPA classes)
- Jakarta Persistence (`jakarta.persistence.*`)
- PostgreSQL dialect
- Auditing via `@EntityListeners(AuditingEntityListener.class)`

---

## Entity Template

```java
package com.example.domain.<module>;

import com.example.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "<snake_case_plural>")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class <EntityName> extends BaseEntity {

    @Column(name = "...", nullable = false, length = 255)
    private String fieldName;

    // Enum fields
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private <EnumType> status;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private ParentEntity parent;

    // OneToMany (always LAZY, mapped by owner)
    @OneToMany(mappedBy = "thisEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ChildEntity> children = new ArrayList<>();
}
```

## BaseEntity (always extend this)

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
```

---

## Rules

### IDs

- Always use `Long` with `GenerationType.IDENTITY` (PostgreSQL `BIGSERIAL`)
- Never expose raw IDs in APIs — use DTOs

### Column Naming

- Always declare `@Column(name = "snake_case")` — never rely on naming strategy alone
- `nullable = false` for required fields
- Set `length` for `VARCHAR` columns (default 255 is fine, tune as needed)

### Relationships

| Relationship  | Default Fetch | Notes                             |
| ------------- | ------------- | --------------------------------- |
| `@ManyToOne`  | LAZY          | Always                            |
| `@OneToMany`  | LAZY          | Always — `mappedBy` on this side  |
| `@OneToOne`   | LAZY          | Always                            |
| `@ManyToMany` | LAZY          | Prefer association entity instead |

- Never use `FetchType.EAGER`
- Initialize collections: `= new ArrayList<>()`
- Bidirectional: add `addChild()` / `removeChild()` helper methods on the owning side

### Enums

- Always `@Enumerated(EnumType.STRING)` — never `ORDINAL`
- Declare enum as a nested class or in a separate file under `domain/<module>/enums/`

### Lombok on Entities

- Use `@Getter` + `@Setter` individually — never `@Data` (causes issues with Hibernate proxies)
- Never use `@EqualsAndHashCode` on JPA entities — implement manually using `id` only, or use the pattern below:

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof <EntityName> other)) return false;
    return id != null && id.equals(other.id);
}

@Override
public int hashCode() { return getClass().hashCode(); }
```

### Soft Delete (when needed)

```java
@Column(name = "deleted_at")
private Instant deletedAt;

public boolean isDeleted() { return deletedAt != null; }
```

Pair with `@Where(clause = "deleted_at IS NULL")` or Spring Data `@Query`.

---

## Checklist Before Outputting

- [ ] Extends `BaseEntity`
- [ ] `@Table(name = "snake_case_plural")`
- [ ] All relationships are LAZY
- [ ] No `@Data`, no `FetchType.EAGER`
- [ ] Enum fields use `EnumType.STRING`
- [ ] Collections initialized inline
- [ ] `equals`/`hashCode` based on `id` only
- [ ] A companion Flyway migration is needed — remind the user or generate it
