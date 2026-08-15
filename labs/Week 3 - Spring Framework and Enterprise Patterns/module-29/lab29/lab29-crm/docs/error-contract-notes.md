# Lab 29 - Error Contract Notes

| Case | Status | Notes |
| ---- | ------ | ----- |
| Validation | 400 | TODO: violations[] |
| Not found | 404 | CUS-9999 |
| Duplicate | 409 | CUS-1001 |
| Unexpected | 500 | no stack traces |

## Concepts to Discuss

1. Main flow when validation fails versus when a domain exception is thrown
   A validation failure should never reach the controller body. Spring intercepts it before a `create` runs. A domain exception is thrown from inside the service after the request already passes shape validation.
2. Trust boundary: DTO vs service vs database constraints
   The DTO layer rehects malformed shapes with `@Valid`, while the service layer rejects invalid business state. The database would be the last line of defense for constraints.
3. Success/failure contracts (`ErrorResponse` fields and HTTP statuses)
   Every error path returns the same `ErrorResponse` with `timestamp`, `status`, `error`, etc. regardless of which handler produced it. This is the whole point of centralizing this in one `@RestControllerAdvice`.
4. Stable identity (`CUS-1001`) in error payloads for support
   The ID is the most important field in error payloads, as it lets support know which record collided without needing to grep anything else. For example, `CUS-1001` in a 409 `message` would be exactly what support looks at.
5. Retry implications for 400/404/409 vs transient 500/503
   400/404/409 are not safe to retry right now. The request is wrong and retrying would reproduce the same exact failure. A 500/503 might be fine for one retry on the other hand.
6. Local shortcut versus production (localization, problem+json)
   The `message` string in this lab is alright for testing in a local lab environment. Production would need a more formal error-type field alongside the message.
7. Evidence operators need (`lab-request-001`, timestamp, path)
   Evidence operators need are the `correlationId`, `timestamp` and `path`. Together, they let an operator find the exact request in logs without needing the client to describe anything.
8. Two app instances: stateless error mapping (same envelope everywhere)
   Because `GlobalExceptionHandler` has no instance state at all, every running instance produces identical error shapes for the same failures. A client never sees a different error contract no matter what instance handled the request.
9. Why forgetting `@Valid` is a production foot-gun
   The annotations in `CustomerRequest` do nothing without `@Valid`. A bad email would silently reach `create` and get persisted, since nothing else in the path re-checks the shape. Failure experiment 1 shows this.
10. How Lab 14/16 ideas map onto Boot without divergent SOAP/REST semantics
    Lab 14's DTO constraints and Lab 16's exception handler are now the same mechanism we've implemented here. `@Valid` and bean validation on the DTO and `@RestControllerAdvice` for the handler, rather than two separate patterns in two different labs.


## Lab 14/16 unify note (Step 9)

Lab 14 established that validation belongs on the request DTO, not buried in
undocumented `if` checks in `CustomerService`. `CustomerRequest`'s `@NotBlank`/`@Email`/`@Pattern`
annotations here are that exact idea, just expressed with Jakarta Bean Validation
instead of hand-written checks. Lab 16 established that exception handling should be
centralized rather than scattered per-controller. `GlobalExceptionHandler` here is
that same idea, applied via Spring's `@RestControllerAdvice` instead of a manually
wired dispatcher. Neither pattern changed conceptually between the two labs and this
one, Spring just gave both patterns first-class framework support.

## SOAP / Spring-WS alignment note (Step 7)

The same `BusinessException` subtypes  that map to REST status codes here should map to SOAP faults the same way Lab 24's
`SoapFaultMappingExceptionResolver` did. As in, `CustomerNotFoundException` to a CLIENT fault
with a not-found reason just as before. The goal is one set of domain exception
types with two thin protocol-specific mapping layers (this `GlobalExceptionHandler` for
REST, Lab 24's fault resolver for SOAP), not two independently-invented error
vocabularies that could drift apart. Implementing the SOAP side isn't required here, just documenting that REST and SOAP should never disagree about what a given business
failure means.

