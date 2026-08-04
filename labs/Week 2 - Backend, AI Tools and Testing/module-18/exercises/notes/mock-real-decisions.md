
## Exercise 1 — When to Keep Real Validator

### Goal

Decide which collaborator stays real for activate tests.

CustomerRepository  -> MOCK     (I/O boundary, slow / non-deterministic)
StatusValidator     -> REAL     (pure, deterministic, fast)
CustomerNotifier    -> MOCK     (avoid email/IO in unit tests)

**RULE**: mock I/O and unstable deps; keep pure domain helpers real when cheap.
