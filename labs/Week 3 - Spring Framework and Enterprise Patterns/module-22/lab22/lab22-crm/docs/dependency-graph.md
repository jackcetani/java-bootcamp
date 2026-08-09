# Lab 22 — Dependency graph

## Bean edges

- `CrmApplication` scans `com.northstar.crm`
- `CustomerController` → `CustomerService`
- `CustomerService` → `CustomerRepository` (impl: `InMemoryCustomerRepository`)
- `CustomerService` → `NotificationService`

All beans are default **singleton** scope (one shared instance per application context).

## Fixtures

- `CUS-1001` Amina Khan ACTIVE
- `CUS-1002` Ravi Singh PROSPECT
- Correlation: `lab-request-001`


## Why constructor injection
Constructor injection makes dependencies final and madatory for the project. The object cannot exist in a half-wired state, and the compiler will catch missing collaborators before any tests run. It also means `CustomerServiceTest` can make a `new` call with zero Spring context. This makes it a fast unit test instead of a disguised integration test. Field injection hides the dependency list, allows a half-built object to exist momentarily, and forces every test through Spring's context machinery just to swap in a fake.

## Concepts to Discuss
1. **Main flow: HTTP → controller bean → service bean → repository bean**\
   HTTP request --> `CustomerController` bean --> `CustomerService` bean --> `CustomerRepository` bean. Every arrow is a constructor parameter, not a `new` call.
2. **Trust boundary: validation still at edges; DI does not replace auth**\
   DI decides how objects get wired together, not who's allowed to call them. Validation still lives at the service boundary, and authn/authz are still absent in this lab.
3. **Success/failure contracts unchanged from Labs 19–21**\
   201 on `create`, 200 on `get`, and now with this lab, `400` on blank required fields. The HTTP behavior from Labs 19-21 doesn't change, even if the wiring mechanism did.
4. **Stable bean identities (types/names) vs request fixture IDs**\
   `CustomerRepository`, `CustomerService`, and `NotificationService` are fixed types that exist for the entire lifetime of the application. `CUS-1001/1002` are data flowing through these types on a request. A bean's identity != a customer's identity.
5. **Idempotent context refresh vs request-level create idempotency**\
   Restarting the app always rebuilds the exavt same bean graph. Thus, it is idempotent at an infrastructure level. Repeating a POST for the same customer ID is business-level idempotency, and this lab doesn't implement duplicate checks still, just silent overwrite.
6. **Local in-memory @Repository vs production JDBC/JPA bean**\
   `CustomerServicce` only depends on the `CustomerRepository` interface, so swapping `InMemoryCustomerRepository` later requires zero changes to `CustomerService` or `CustomerController`. This is the benefit of depending on interfaces.
7. **Evidence: startup logs, graph doc, unit+IT surefire**\
   Startup logs showing a `CustomerService ready` line is evidence, as well as this dependency-graph document. Also, a green Surefire run is proof the graph exists and works, not just compiles.
8. **Two instances: each JVM has its own singleton graph**\
   Each running JVM builds its own independent singleton bean graph, so two app instances share no state. This includes the in-memory customer map, which is exactly why this repository is documented as local lab only.
9. **Why constructor injection beats field injection for tests**\
   A unit test can hand a Java constructor whatever fake objects it wasnts with zero framework involvement. Field-injected dependencies can only be set via reflection tricks or by booting a real Spring context, which is slower and tests more than just the class.
10. **What breaks if someone reintroduces new inside CustomerService**\
    The service would silently start talking to its own disconnected `InMemoryCustomerRepository` instance instead of the shared one the rest of the app uses. Failure experiment 5 explores this.


1. **Design decision that most affected correctness:** Choosing constructor injection over field injection for `CustomerService` — it's what makes `CustomerServiceTest`'s `new CustomerService(fakeRepo, fakeNotify)` possible at all, and it's what makes a missing collaborator fail loudly at startup (Failure Experiment 1) instead of silently as a null field the first time it's actually used.
2. **Hardest failure to diagnose:** Failure Experiment 5's dual-store bug — the test *looks* like it should pass (the fixture repo clearly has Amina in it), and the actual failure only makes sense once you realize the service quietly built its own second, disconnected repository instance instead of using the one it was handed.
3. **Evidence the graph works:** All three forms together — the pure unit test (proves the classes are genuinely decoupled), the `@SpringBootTest` IT (proves the real Spring-assembled graph behaves the same way), and the curl trace against the running app (proves it works end-to-end over real HTTP, not just inside a test harness).
4. **What breaks first at ten times the bean count or request rate:** Manually keeping `dependency-graph.md` in sync with the actual constructor signatures gets much harder at ten times the bean count — this is exactly the kind of thing that benefits from generating the graph from the Actuator `/actuator/beans` endpoint rather than hand-maintaining it, once the graph gets large enough that drift becomes likely.
5. **What should move to shared infrastructure:** A shared Spring Boot parent/starter with pre-agreed component-scan conventions (root package, stereotype expectations) across every CRM module — right now each lab's `CrmApplication` independently gets this right, but a real multi-service org would want that convention enforced once, centrally.
6. **What must change before real customer data is used:** The persistence bean needs to swap from `InMemoryCustomerRepository` to a real JDBC/JPA-backed implementation — and critically, the "no PII in notification/lifecycle logs" rule doesn't loosen at all once that happens; if anything it matters more once the customers behind those IDs are real people.
7. **Connection to Labs 18–21:** Lab 18's Mockito practice is exactly what makes `CustomerServiceTest`'s `mock(NotificationService.class)` natural instead of foreign; Lab 19 gave the HTTP endpoints this lab's controller still serves; Lab 20's correlation/PII logging rules are enforced unchanged inside `NotificationService`; Lab 21's Actuator health/metrics pattern is the same injectable-bean shape this lab formalizes for everything else.
8. **Metric or bean health signal that matters most after DI is complete:** Whether the application context starts successfully at all — a missing-bean exception is a hard, unambiguous startup failure (Failure Experiment 1), which is actually a *good* signal: it fails fast and loud rather than silently misbehaving at runtime.
9. **(Forward look) Which bean would you replace first for JDBC/JPA production persistence?** `InMemoryCustomerRepository` — and only that one. Because `CustomerService` depends on the `CustomerRepository` *interface*, not the concrete class, swapping in a JPA-backed implementation only requires a new class annotated `@Repository` implementing the same two methods; `CustomerService`, `CustomerController`, and `CustomerServiceTest`'s fake-based unit test would all need zero changes. This is precisely the setup Lab 25's overview describes explicitly ("later labs swap persistence without rewriting the service contract"), and it's the direct payoff of today's interface-based constructor injection.
