# DTO boundary notes — Lab 14

1. **Why entity must not be the public contract:**\
The entity carries internal-only fields and reshapes whenever storage changes. Exposing it directly means every internal refactor becomes an accidental breaking API change. A DTO is a small, deliberately stable contract independent of storage.
2. **Where validation runs vs business rules:**\
Shape validation (blank/format/size) happens in the facade before mapping. Business rules (duplicate IDs, status transitions) happen inside the service after mapping. Two different kinds of "invalid," checked in two different places on purpose.
3. **Correlation on invalid payloads:**\
Every validation failure thrown from the facade includes `[lab-request-001]` in the exception message, so a rejected request is traceable in logs without needing a full stack trace.
4. **Never on a response DTO:**\
Password hashes, internal database IDs, audit-only flags. Really anything that isn't part of the deliberate public contract stays off `CustomerResponseDTO`.

### Concepts to Discuss
1. **Main data/request flow (facade → validate → map → service → response DTO)**\
facade -> `validator.validate()` -> map to entity -> service -> map back to response DTO
2. **Trust boundary and input validation point**\
The facade, specifically the `validate()` call before anything touches the service.
3. **Success and failure contract (validation vs duplicate ID vs not found)**\
A validation failure means an `IllegalArgumentExceptions` before the service runs. A duplicate ID means an `IllegalStateException`. A not found means a facade wraps with correlation.
4. **Stable identity (CUS-1001) vs mutable display fields**\
CUS-1001 stays ficed, while `status`/`email` are mutable fields.
5. **Retry/idempotency implications at the DTO boundary**\
Unchanged from lab 12. `get` is safe to retry. `create` is not blindly safe.
6. **Local programmatic Validator vs Spring @Valid later**\
Same underyling Hibernate Validator rules. Spring just changes who calls `validate()`, not what is actually validated.
7. **Logs/evidence for support (lab-request-001 on failures)**\
Correlation ID appears in every thrown validation / not-found message.
8. **Behavior with two application instances (independent memory)**\
Independent memory, same as prior labs. Instance A will not affect instance B.
9. **Why response DTOs should prefer getters-only / factory methods**\
Callers shouldn't be able to mutate a response object and pretend that reflects the server state.
10. **What must never appear on a response DTO (password hashes, internal flags)**\
Password hashes, internal flags, and anything not meant for the client to see should never appear on a response DTO.