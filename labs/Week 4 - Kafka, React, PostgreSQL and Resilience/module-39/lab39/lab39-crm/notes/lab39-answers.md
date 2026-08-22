# Lab 39 Answers

## Reflection Questions

1. **Which design decision most affected correctness (types, OSIV, Flyway)?**\
The most crucial design decision was using `ddl-auto: validate` with Flyway owning all schema changes. It's the only thing that guarantees the entities and the real database never silently drift apart, which `update` and `create` would let happen.
2. **What evidence proves PostgreSQL mappings work (not just unit mocks)?**\
Evidence the PostgreSQL mappings work is `CustomerRepositoryIT` passing against the real running Postgres container, not H2. The save/find rounf-trip only proves anything because it's exercising the actual JDBC driver, actual column types, and actual constraints, not an in-memory approximation.
3. **Which failure was hardest to diagnose?**\
The container-name collision I ran into with the already running Lab 37 and 38 Postgres instance was hard for me to diagnose. The symptom was clear with "container name already in use", but the deeper issue wasn't obvious at first. This lab's schema is incompatible with lab 37 and 38's live tables.

