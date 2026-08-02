## Lab 12 Answers

### Concepts to Discuss
1. **Main data flow after refactor (create / get / update status)**\
create -> validate blank / duplicate -> store in Map; get -> lookup or throw; update -> lookup, mutate status, return.
2. **Trust boundary and where validation lives after cleanup**\
Validation now lives in the extracted helpers inside `CustomerService`. Still in the service layer, just readable instead of buried in a 50-line method.
3. **Success/failure contract (duplicate ID, unknown ID, blank name)**\
Duplicate ID means an `IllegalStateException`. Unknown ID means an `IllegalArgumentException`. Blank name / ID also means an `IllegalArgumentException`.
4. **Stable identity (CUS-1001) vs mutable fields (status, email)**\
`CUS-1001` never changes once assigned. `status`//`email` can and do change over a customer's lifecycle.
5. **Retry/idempotency implications for create vs get**\
`getCustomer` is safe to retry, while `createCustomer` is not. Retrying a `create` risks a duplicate-ID exception on the second attempt. While it is at least a safe failure, it is not idempotent.
6. **Local in-memory shortcut vs production persistence**\
It is fine for this lab, but a restart would wipe everything. That's explicitly a now-only shortcut.
7. **Logs/evidence for support (lab-request-001)**\
`correlationId=lab-request-001` on eery exception message is what a support engineer would look through logs for.
8. **Two JVM instances = independent memory (conflict risk)**\
Completely separate Maps and no shared state. A customer created on instance A does not exist on instance B.
9. **Which SOLID ideas fit this lab’s size, and which are deferred?**\
Single Responsiblity with the extracted helpers. Also Open/Closed when a status is a typed enum, making it easy to extend. Also, Dependency Inversion with a `CustomerRepository` is deferred to Lab 15.
10. **Why freezing a before snapshot matters more than “I rewrote it cleanly”?**\
Without it, nobody can tell whether this was a genuine refactor or a from-scratch rewrite. The snapshot is the only proof to measure improvement.

### Security and Production Review
1. **Which inputs are untrusted (customer fields from callers)?**\
Every field a caller passes into `createCustomer` and `updateStatus` are considered untrusted.
2. **Where are authn/authz/validation enforced after refactor (service helpers—auth still absent)?**\
Validation is enforced in the service layer still, but now inside the service helpers. Authn and authz are still absent in this lab.
3. **Which values are sensitive, and where stored (none beyond samples)?**\
None of the values beyond the sample emails are sensitive, and they are stored only in memory.
4. **What can be retried safely (get; create is not silently idempotent)?**\
`getCustomer` is safe to retry. `create` is not silently idempotent as it risks a duplicate on the second try.
5. **What happens after partial failure (exceptions; no half-written silent null)?**\
An exception is always thrown, nothing is partially handled. Thus, there is no half-written silent null state.
6. **What would an operator monitor later (correlation ID, error rates)?**\
An operator might monitor correlation ID hits. Also, they would likely monitor error rates by exception type.
7. **Which local default is unacceptable in production (in-memory; System.out logging)?**\
In-memory storage and raw `System.out` are unacceptable in production. It is removed here, but will be true architecturally until a later lab.
8. **How are contracts versioned later (Lab 13+ WSDL/OpenAPI; stable method names help)?**\
Lab 13's WSDL and Lab 14's DTOs will version contracts later on. Stable method names on this service help both stay aligned.

### Reflection Questions
1. **Which design decision most affected correctness?**\
Switching from `==` to `Map`-keyed lookup was the most important design decision for correctness. It fixes a real production-class bug, it's not just a style change.
2. **Which smell was hardest to justify removing?**\
The "UPDATE" magic branch because it was hard to tell a smell was actually there since it does technically work. Removing working code that secretly could break your program takes more attention that fixing already clearly broken code.
3. **What evidence proves the refactor preserves intended behavior?**\
The same target-API tests, red before refactor and green after, and runs against the exact same fixtures (CUS-1001/CUS-1002) are evidence of the refactor preserves behavior.
4. **What breaks first at ten times method length if smells return?**\
The mixed-responsibility problem gets much worse. A 500-line equivalent method would be unreviewable by humans.
5. **Which concern should move to shared infrastructure (logging, IDs)?**\
The correlation ID mechanism should move to shared infrastructure. Right now, it's a field on one service, but a real system wants it threaded through a logging framework automatically.
6. **What must change before real customer data is used?**\
Persistence off in-memory and validation needs ot become genuinely comprehensive. Right now, only checking for duplicate IDs/blank names is not enough for the future for real data.
7. **How does this lab connect to Labs 8–11 standards and Lab 13 contracts?**\
This is Lab 8's naming/layer standards actual enforced. It hands Lab 13 a domain worth designing a stable SOAP contract around.
8. **What metric, log field, or support clue matters most after refactor?**\
The correlation ID on failure messages matters most after a refactor. It's the field a support engineer would look for first and gives the most insight.
9. **(Forward look) Which deferred SOLID step (e.g. repository DIP) comes next—and why not today?**\
Full repository Dependency Inversion principle is deferred in this lab. Lab 15 is where a 'CustomerRepository' interface should be implemented, because doing it early now would be out of the scope for this labs focus on naming and method boundaries.