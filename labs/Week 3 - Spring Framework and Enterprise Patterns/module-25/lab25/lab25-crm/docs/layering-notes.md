# Lab 25 — Layering notes

## Concepts to Discuss

1. **Main flow: HTTP → controller → service → repository**\
   HTTP Request --> `CustomerController` --> `CustomerService` business rules --> `CustomerRepository` --> `InMemoryCustomerRepository`
2. **Trust boundary: JSON → DTO validation → domain**\
   Incoming JSON going to `CustomerRequest` and `StatusUpdateRequest` DTOs is validated at that boundary, with `@NotBlank` for example. Then, it is converted to the domain `Customer` before the service ever sees it. The service should NEVER touch raw JSON.
3. **Success/failure contracts for create/get/list/status**\
   `create` is a 201, or 409 for a duplicate. `get` is 200, or 404 if not found. `status` update is 200, or 409 on illegal transition.
4. **Stable business IDs (CUS-1001) vs generated DB keys later**\
   CUS-1001 is the identity that will never change even after JPA and PostgreSQL swap. An auto-generated primary key would be an internal storage detail, never something a caller should depend on.
5. **Idempotency: GET vs POST create**\
   GET is always safe to retry. POST `create` is not. Failure experiment 4 shows a repeat create with the same ID will be rejected with a 409. This is now a safe failure in this lab, no silent overwrites.
6. **In-memory shortcut vs JPA + PostgreSQL production design**\
   The in-memory map is fine for this lab, but it loses all data on restart and can't be shared across instances. This is why the `CustomerRepository` interface exists, so the swap can happen without touching the service or controller.
7. **Correlation lab-request-001 as support evidence**\
   Every create and get call threads the `lab-request-001` header through. Thus, an engineer can trace one request across every layer's evidence, even though the header itself never touches business logic.
8. **Two instances: maps do not share**\
   Each running JVM has its own independent map. Two instances behind a load balancer would each have a different customer registry. This gap can only be closed with real persistence.
9. **False-confidence AI: ResponseEntity in service, repo using Servlet types**\
   An AI suggestion that returns `ResponseEntity` from `CustomerService` or has `HttpServletRequest` import would look like it works, but silently it breaks the whole point of layering in the first place. Failure experiment 5 explains why this must be rejected immediately.
10. **What Lab 27 adds (transactions) without rewriting controller routes**\
    Lab 27 will add `@Transactional` behavior on related account entities. Since the controller and service contracts are already stable and don't leak persistence details, Lab 27 can add transaction boundaries without touching a signature.

## JPA readiness note (Step 8)

When `InMemoryCustomerRepository` is replaced with a real JPA-backed implementation:
`CustomerRepository` becomes `public interface CustomerRepository extends
JpaRepository<Customer, String>` (Spring Data auto-implements `save`/`findById`, and
`findByCustomerId`/`existsByCustomerId`/`deleteByCustomerId` become Spring Data derived
query methods matching the same names). `Customer` gains JPA annotations
(`@Entity`, `@Id` on `customerId`). **Nothing in `CustomerService` or
`CustomerController` needs to change**!!! Both depend only on the `CustomerRepository`
interface's method signatures, which stay identical. This is the entire payoff of
today's layering work.
