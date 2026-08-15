# Lab 29 Answers

## Reflection Questions

1. Which design decision most affected correctness (where validation runs)?
The most crucial design decision was putting bean validation constraints directly on the DTO rather than as manual `if` checks inside the service. It's what makes `@Valid` sufficient alone to reject invalid shape before the service even runs.
2. Which failure was hardest to diagnose (missing `@Valid`, advice not scanned)?
The hardest failure to diagnose is the missing `@Valid` annotation producing a successful 201 rather than an error. A missing annotation is a silent failure, which is exactly why failure experiment 1 shows us the risk of missing this.
3. What evidence proves the error contract is stable?
Evidence the error contract is stable is running the same invalid POST twice and getting the same error response. Failure experiment 5 shows this, and is strong proof of stable contract rather than a single test run.
4. What breaks first at ten times the invalid-request rate?
The log volume from the 500 fallback's `log.error()` calls would brak first at ten times the invalid-request rate. If a client bug started sending malformed requests at a high volume, any code path that still reaches the generic 500 fallback would flood logs with full stack traces.
5. Which concern should move to shared infrastructure (problem+json standards)?
The main concern that should move to shared infrastructure is the `application/problem+json` standard across every service in the platform. It is a cross-cutting concern whose contract shouldn't be reinvented per service.
6. What must change before real customer data is used (PII in violations)?
The `rejectedValue` in violations needs a stricter audit before real customer data is used. A real email or name accidentally echoed back in a validation error is a real PII leak risk. This lab makes a leak acceptable with our fictional stable fixtures, but in a real prod system it is unacceptable.
7. How does this lab connect to Labs 14, 16, 25, 27, and 28?
Lab 14's DTO validation and Lab 16's centralized exception handling is the same idea we've employed here, just put together in a single lab. Lab 25's layering is what makes `CustomerService` easy to test independent of the HTTP layer. Lab 27's transaction discipline is what we use to check duplicates here, and Lab 28's security work needs to coexist with this JSON error contract, never fall back to an HTML login page for a validation failure.
8. What metric or log field matters most for API support?
The most important field is the `correlationId` on every error body. It's the field that allows support to trace a specific complaint back to the exact request across every log line that mentions it.
9. (Forward look) How should Kafka consumers (Labs 30–31) emit correlated errors without HTML?
A Kafka consumer has no HTTP response, so the equivalent discipline would be to never let a malformed message crash the consumer thread with an unstructured stack trace. Instead, it should be caught and logged in a structured record carrying the same `correlationId` conventions, and route the failed message to a dead-letter topic with an `ErrorResponse` payload. 