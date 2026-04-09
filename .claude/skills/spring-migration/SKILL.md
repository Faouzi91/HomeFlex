---
name: spring-migration
description: >
  Generate Flyway SQL migration scripts for Spring Boot + PostgreSQL projects following strict
  naming conventions and safe SQL patterns. Trigger this skill whenever the user asks to create
  a database migration, add a table, add a column, create an index, rename a column,
  or says "add this to the database", "update the schema", or "I need a migration for X".
  Always apply this — never freehand migration SQL.
---

# Flyway Migration Generation

## Stack Assumptions

- Flyway 9+ · PostgreSQL · Spring Boot 4.x
- Migrations path: `src/main/resources/db/migration/`
- Repeatable migrations: `src/main/resources/db/migration/repeatable/`

---

## Naming Convention

```
V{version}__{description}.sql
```

| Part            | Rule                                                            |
| --------------- | --------------------------------------------------------------- |
| `V`             | Capital V — always                                              |
| `{version}`     | Timestamp format: `YYYYMMDDHHMMSS` or sequential `1`, `2`, `3`… |
| `__`            | Double underscore — always                                      |
| `{description}` | Snake_case, verb-first, describes the change                    |

**Examples:**

```
V20240101120000__create_users_table.sql
V20240102090000__add_email_to_users.sql
V20240103140000__create_idx_users_email.sql
V20240104__add_status_enum_to_orders.sql
```

**Repeatable (views, functions):**

```
R__create_user_summary_view.sql
```

---

## Templates

### Create Table

```sql
-- V<version>__create_<table>_table.sql

CREATE TABLE <table_name> (
    id          BIGSERIAL       PRIMARY KEY,
    -- foreign keys
    <parent>_id BIGINT          NOT NULL REFERENCES <parents>(id) ON DELETE CASCADE,
    -- required fields
    name        VARCHAR(255)    NOT NULL,
    status      VARCHAR(50)     NOT NULL DEFAULT '<default_value>',
    -- optional fields
    description TEXT,
    amount      NUMERIC(19, 2),
    -- audit
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_<table>_<field> ON <table_name>(<field>);
CREATE UNIQUE INDEX idx_<table>_<field>_unique ON <table_name>(<field>);

-- Comment (optional but recommended)
COMMENT ON TABLE <table_name> IS '<Description of what this table stores>';
```

### Add Column

```sql
-- V<version>__add_<column>_to_<table>.sql

ALTER TABLE <table_name>
    ADD COLUMN <column_name> <TYPE> [NOT NULL DEFAULT '<value>'];

-- If adding NOT NULL without a default to existing data:
-- Step 1: Add nullable
ALTER TABLE <table_name> ADD COLUMN <column_name> <TYPE>;
-- Step 2: Backfill
UPDATE <table_name> SET <column_name> = '<default>' WHERE <column_name> IS NULL;
-- Step 3: Add constraint
ALTER TABLE <table_name> ALTER COLUMN <column_name> SET NOT NULL;
```

### Add Index

```sql
-- V<version>__create_idx_<table>_<field>.sql

-- Use CONCURRENTLY for prod tables (doesn't lock — requires separate transaction)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_<table>_<field>
    ON <table_name>(<field>);

-- Partial index (performance win for filtered queries)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_<table>_status_active
    ON <table_name>(status)
    WHERE status = 'ACTIVE';
```

> ⚠️ `CONCURRENTLY` cannot run inside a Flyway transaction.
> Set `spring.flyway.mixed=true` or use a separate migration file without `BEGIN`.

### Rename Column

```sql
-- V<version>__rename_<old>_to_<new>_in_<table>.sql

ALTER TABLE <table_name> RENAME COLUMN <old_name> TO <new_name>;
```

### Drop Column (with safety)

```sql
-- V<version>__drop_<column>_from_<table>.sql

-- Only run after removing all references from application code
ALTER TABLE <table_name> DROP COLUMN IF EXISTS <column_name>;
```

### Enum Type (PostgreSQL native)

```sql
-- V<version>__create_<name>_enum_type.sql

-- Option A: PostgreSQL ENUM (strict, harder to alter)
CREATE TYPE <enum_name> AS ENUM ('VALUE_ONE', 'VALUE_TWO', 'VALUE_THREE');

ALTER TABLE <table_name>
    ALTER COLUMN status TYPE <enum_name> USING status::<enum_name>;

-- Option B (recommended): Keep as VARCHAR + CHECK constraint (easier to evolve)
ALTER TABLE <table_name>
    ADD CONSTRAINT chk_<table>_status
    CHECK (status IN ('VALUE_ONE', 'VALUE_TWO', 'VALUE_THREE'));
```

### Junction Table (ManyToMany)

```sql
-- V<version>__create_<a>_<b>_table.sql

CREATE TABLE <a>_<b> (
    <a>_id BIGINT NOT NULL REFERENCES <a>s(id) ON DELETE CASCADE,
    <b>_id BIGINT NOT NULL REFERENCES <b>s(id) ON DELETE CASCADE,
    PRIMARY KEY (<a>_id, <b>_id)
);
```

---

## PostgreSQL Type Reference

| Java Type         | SQL Type                   |
| ----------------- | -------------------------- |
| `String` (short)  | `VARCHAR(255)`             |
| `String` (long)   | `TEXT`                     |
| `Long` / `long`   | `BIGINT`                   |
| `Integer` / `int` | `INTEGER`                  |
| `BigDecimal`      | `NUMERIC(19, 2)`           |
| `Boolean`         | `BOOLEAN`                  |
| `Instant`         | `TIMESTAMP WITH TIME ZONE` |
| `LocalDate`       | `DATE`                     |
| `UUID`            | `UUID`                     |
| Enum (String)     | `VARCHAR(50)`              |

---

## Rules

1. **Never modify an existing migration** — always create a new one
2. **Migrations must be idempotent where possible** — use `IF NOT EXISTS`, `IF EXISTS`
3. **Always include audit columns** (`created_at`, `updated_at`) on new tables
4. **Foreign keys must have `ON DELETE` behavior declared** — CASCADE, SET NULL, or RESTRICT
5. **Index naming**: `idx_<table>_<columns>` (e.g., `idx_users_email`)
6. **Unique constraint naming**: `uq_<table>_<column>` (e.g., `uq_users_email`)
7. **Check constraint naming**: `chk_<table>_<column>` (e.g., `chk_orders_status`)
8. **Use `BIGSERIAL` for PKs** — maps to `GenerationType.IDENTITY`
9. **Never use `SERIAL`** — use `BIGSERIAL` only

---

## Checklist Before Outputting

- [ ] File name follows `V<timestamp>__verb_description.sql`
- [ ] All columns declared with explicit types and `NOT NULL` or nullable
- [ ] `created_at` and `updated_at` present on new tables
- [ ] FK constraints declared with `ON DELETE` behavior
- [ ] Indexes named `idx_<table>_<field>`
- [ ] No destructive operations without `IF EXISTS` guard
- [ ] Backfill step included if adding NOT NULL column to existing table
- [ ] Remind user to add corresponding entity/DTO changes
