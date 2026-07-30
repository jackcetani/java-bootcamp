# Lab 8 Answers

## Layer table

| Layer concept | Package folder | Owns | Must NOT own |
| -------------- | --------------- | ---- | ------------- |
| Presentation | `controller` | Accept/return DTOs; map calls | SQL, business rules |
| Business | `service` | Rules, orchestration | HTTP headers, JDBC details |
| Persistence | `repository` | Save/find | REST mapping |
| Domain | `entity` | Customer fields | Request JSON shapes |
| Contracts | `dto` | Request/response | Persistence annotations |
| Cross-cutting | `config`, `exception` | Wiring, failure types | Happy-path create logic |

## Concepts to Discuss

1. **Main data/request flow once create-customer is implemented:**\
Client → `CustomerController.createCustomer` → `CustomerService.create` (assigns ID, defaults status) → `CustomerRepository.save` (persists) → `CustomerService` maps the saved entity to a `CustomerResponse` → back to the client. Today every step past the controller throws `UnsupportedOperationException` — this describes the intended path, not current behavior.
2. **Trust boundary / who owns input validation later:**\
The trust boundary is the controller, since that's the first code the outside world reaches. Validation logic will likely live right at or just inside that boundary in `CustomerController`/`CustomerService`, so nothing downstream ever sees an unvalidated request.
3. **Success/failure contract for "create customer":**\
Happy path returns a `CustomerResponse` with a generated `CUS-####` ID and `status=ACTIVE`. Failure paths will eventually throw named exceptions like `CustomerNotFoundException`'s siblings, caught at the controller boundary once error mapping is wired.
4. **Stable identity vs display name:**\
`CUS-1001` is the stable identity. It never changes and is what other systems key off of. `Amina Khan` is a name that could possibly change without altering which customer record it points to.
5. **Retry and idempotency at the repository boundary:**\
A `save()` call retried after a network blip could create a duplicate customer unless the repository can recognize the specific ID was already saved and treat a retry as a no-op. The logic belongs in `repository` since only the persistence layer knows what's actually been durably stored, not `service`.
6. **Local dev shortcut vs production design:**\
An in-memory `List` in `CustomerRepository` is fine for now, but it loses all data on restart and can't be shared across multiple app instances. Production needs PostgreSQL or something similar so state survives restarts and multiple instances see the same data.
7. **Logs/metrics/UI evidence needed once APIs exist:**\
Once real APIs exist, correlation IDs threaded through every log line will be needed for tracing one request across layers.
8. **Behavior with two app instances sharing the same customer IDs:**\
With this lab's in-memory stub design, two instances would have completely separate, disconnected customer lists. For example, instance A's `CUS-1001` wouldn't exist on instance B. This is why in-memory storage is only a temporary solution and a shared database is needed before running more than one instance.
9. **Why entity must not import controller:**\
`Customer` is meant to be reusable by anything that needs the domain model. This includes repository and service for now, neither of which should need to know HTTP or transport concepts. If `entity` imported `controller`, the domain model would be coupled to a specific delivery mechanism that has nothing to do with what a customer is.
10. **DTO vs entity for the same Amina Khan create request:**\
`CustomerRequest` holds exactly what the client sends (name, email) and nothing more. `Customer` holds what's actually persisted, including fields the client never sends directly.

## Manual Verification 

_Run each item on your laptop and mark Pass/Fail — these are the expected results if everything above is wired correctly._

| # | Check | Expected | Your result |
| - | ----- | -------- |-------------|
| 1 | `pwd` ends in `lab8-crm` | Confirmed before every Maven command | PASS        |
| 2 | `mvn clean compile` | `BUILD SUCCESS` |         PASS     |
| 3 | `find src/main/java -name '*.java' \| sort` | Lists 9 files: `Main`, `AppConfig`, `CustomerController`, `CustomerRequest`, `CustomerResponse`, `Customer`, `CustomerNotFoundException`, `CustomerRepository`, `CustomerService` |     PASS         |
| 4 | `java -cp target/classes com.northstar.crm.Main` | Three banner lines (see above) |     PASS         |
| 5 | `docs/CODING-STANDARDS.md` and `docs/layer-flow.md` exist | Both present, both name the seven layers |     PASS         |
| 6 | `rg springframework src` (or `grep -r springframework src`) | No matches |     PASS         |
| 7 | `git check-ignore -v target` | `target/` shown as ignored |       PASS       |
| 8 | Throwaway harness calling `new CustomerRepository().findById("CUS-1001")` | Throws `UnsupportedOperationException` |      PASS        |
| 9 | `mvn clean compile` run twice in a row | `BUILD SUCCESS` both times |         PASS     |
| 10 | Notes include `lab-request-001` and NOW vs FUTURE boundaries | Present in `docs/layer-flow.md` |        PASS      |

## Failure Experiments

1. **Rename `pom.xml` temporarily, run `mvn compile`.**\
   Observed: Maven reports it cannot find a POM in the current directory (build fails)
2. **Call `new CustomerRepository().findById("CUS-1001")` from a throwaway `main`.**\
   Observed: `UnsupportedOperationException:` is thrown and propagates straight out, since nothing catches it for now.
3. **Run `mvn clean compile` twice in a row.**\
   Observed: `BUILD SUCCESS` both times.
4. **Temporarily add `import com.northstar.crm.controller.CustomerController;` inside
   `CustomerRepository.java`.**\
   Observed: this compiles without error since Java's compiler has no concept of the layer
   rule, only Maven's package structure. The layer rule is a *team convention documented in `CODING-STANDARDS.md`*, not something the build tool
   enforces automatically. A programmer would be the one to catch this.
5. **Put a `.java` file under `src/java/...` (wrong path)**\
   Observed: Maven ignores the moved `.java` file and the class is missing from `target`.

## Security and Production Review

1. **Untrusted inputs:**\
Nothing in this lab accepts external input yet. However, future API inputs are the untrusted boundary once HTTP exists.
2. **Where auth/authz/validation will be enforced:**\
Authentication and authorization will sit in front of or inside `controller`. Field-level validation will sit in `controller`/`service`.
3. **Sensitive values and storage:**\
None yet in this lab.
4. **What can be retried safely:**\
`mvn compile` is fully safe to retry any number of times. "Create customer" is not safe to retry blindly once implemented, since a retry could risk making a duplicate customer.
5. **What happens after a partial failure:**\
In this lab, every stub method throws before anything is stored. No partial failures possible yet. 
6. **What an operator would monitor later:**\
API request latency and error rate per endpoint, database connections, and, once correlation IDs are wired, the ability to trace a single correlation ID across logs.
7. **Local default that's unacceptable in production:**\
Nothing in this lab yet.
8. **How contracts get versioned later:**\
Package structure (`dto` vs `entity`) is the first place to start, with formal versioning becoming possible with WSDL contracts.

## Reflection Questions

1. **Which design decision most affected correctness of the skeleton?**\
Keeping `dto` and `entity` as two separate packages/classes was a crucial decision. It becomes hard to change this aspect later.
2. **Which failure was hardest to diagnose?**\
The layer-direction violation was the hardest because it does not appear during compilation. Since there's no error message at all to point you to the problem, it needs a documented convention and a human catching it instead of the compiler.
3. **What evidence proves the layered structure is real, not only aspirational?**\
`mvn clean compile` succeeding with all seven packages present, the `find src/main/java -name '*.java'` listing matching the expected file set, and the fact that `CustomerController` calls through to `CustomerService`.
4. **What breaks first at ten times the team size if packages are messy?**\
Code review does. Without agreed upon layer boundaries, whether validation should live in the controllers or the service becomes a debated topic everytime somebody makes a pull request. This leads to merge conflicts as team members put similar logic in different layers for the same feature.
5. **Which concern should move to shared infrastructure later?**\
Logging/correlation-ID propagation and configuration management  are both things that should eventually live in shared infrastructure rather than being reinvented per service.
6. **What must change before real customer data is used?**\
Storage must move off the in-memory `List` onto a real durable database with proper access controls and `application.properties` must never contain real credentials. Validation/authentication must also be implemented rather than just planned in docs.
7. **How does this lab connect to Labs 9–12 and later CRM platform pieces?**\
Lab 9 expands the `pom.xml` with real dependencies. Labs 10–12 fill in the `entity`/`repository`/`service` stubs with actual working logic.
8. **What metric/log field/query plan/UI state matters most once APIs exist?**\
The correlation ID on every log line, so a single customer-creation request can be traced end-to-end across controller, service, and repository once those calls actually happen over a network.
9. **Why keep DTOs separate from entities for creating Amina Khan (`CUS-1001`)?**\
`CustomerRequest` only ever needs to carry what a client submits (name, email). The `Customer` entity ends up holding the generated ID, default status, and timestamp that the client never provided. Conflating them would either leak internal fields to the API or force the API shape to dictate the storage shape.
10. **(Forward look) When Spring Boot arrives, which packages stay stable vs which files change first?**\
`entity`, `dto`, and the package structure itself should stay stable. Spring doesn't change what a `Customer` or `CustomerRequest` fundamentally is. `controller`, `service`, `repository`, and `config` are the files that change first, since those are the classes whose job is to talk to a framework.