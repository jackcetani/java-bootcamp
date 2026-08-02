# Lab 15 — service layer notes

## Status transition table

| From | Allowed to |
| ---- | ---------- |
| PROSPECT | ACTIVE, CLOSED |
| ACTIVE | SUSPENDED, CLOSED |
| SUSPENDED | ACTIVE, CLOSED |
| CLOSED | (none) |

## Wiring

- Shared `InMemoryCustomerRepository` instance for `CustomerValidator` + `DefaultCustomerService`
- No `HashMap` / JDBC / `EntityManager` in the `service` package

## TODO

Fill after you complete the smoke demo (activate CUS-1002; illegal ACTIVE→PROSPECT leaves Amina ACTIVE).

## Concepts to Discuss
1. **Main data/request flow (facade → service → validator/repository)**\
facade -> service -> validator + repository
2. **Trust boundary: Bean Validation (shape) vs CustomerValidator (meaning)**\
Bean validation checks shape at the edge. `CustomerValidator` checks meaning one layer in, such as uniqueness.
3. **Success/failure contract for create and changeStatus**\
`create` throws an `IllegalStateException` on duplicate IDs / emails. `chanegStatus` throws an `IllegalArgumentException` if it there is an unknown ID. An `IllegalStateException` is thrown if there is an illegal transition.
4. **Stable identity (CUS-1001) vs mutable status**\
CUS-1001 is fixed. `status` is what this lab's validator governs mutations of.
5. **Retry/idempotency for changeStatus when already ACTIVE**\
Calling `changeStatus` to the same status it's already at is currently rejected. However, it could be treated as a safe no-op.
6. **In-memory repository vs future JPA behind the same interface**\
It will be the same `CustomerRepository` interface, just a different implementation. This is the whole point of the interface.
7. **Correlation ID on illegal transitions for support**\
Every `CustomerValidator` failure carries `lab-request-001` for support tracing.
8. **Two JVMs = independent memory (activation races later need DB constraints)**\
For independent memory, real activation races need a real database with proper constraints, not this in-memory Map.
9. **Why constructor DI beats service locators for Labs 17–18**\
Labs 17 and 18 can hand `DefaultCustomerService` a fake repository/validator directly through the constructor. There is no static lookup to fight with in tests.
10. **What Spring will change in wiring but not in transition tables**\
Wiring will change (`new` -> `@Autowired`). The `ALLOWED` transition table and validator logic will stay the same.
