# Lab 39 — JPA / PostgreSQL notes

## Flyway vs ddl-auto

`ddl-auto: validate` means Hibernate only checks the schema matches the entities. It will never silently alter a shared database. Flyway owns every actual schema change through versioned, reviewable migration files, so a bad migration can be caught in review instead of mutating a database no one asked it to touch.

## Optimistic locking

`@Version` increments on every update, so if two requests load the same row and both try to save, the second one's `UPDATE ... WHERE version = <stale>` matches zero rows, and Spring throws `ObjectOptimisticLockingFailureException`. `ApiExceptionHandler` maps that to a `409`, telling the second caller to reload and retry instead of silently overwriting the first caller's change.


