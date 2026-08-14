# Lab 28 Answers

## Reflection Questions

1. Which design decision most affected correctness (stateless JWT vs session)?
The most crucial design decision was choosing stateless HWT over server-side sessions. It's what makes horizontal scaling easy, so any instance with a shared secret can verify any token.
2. Which failure was hardest to diagnose (401 vs 403 vs filter order)?
Distinguishing a 401 from a 403 when the entry point and access handlers aren't explicitly wired. Without that, Spring's fallback would have returned a 403 for both missing auth and wrong role.
3. What evidence proves role separation works?
The evidence that proves role separation works is the `admin_forbidden_for_agent` and `admin_allowed_for_admin` in `SecurityIntegrationTest` run back to back with the same endpoint, but different tokens. It's the same route and request shape, but a different outcome solely because of role.
4. What breaks first at ten times the login rate?
The `AuthenticationManager`'s BCrypt comparison would break first at ten times the login rate. Bcrypt is slow on purpose so a large volume of concurrent logins would show up first.
5. Which concern should move to shared infrastructure (IdP, key vault)?
The identity provider and key management should both move to shared infrastructure. Both are the type of thing that should exist once and centrally, rather than being reinvented per service.
6. What must change before real customer data is used?
A real identity provider, rate-limited login, and refresh-token flow must be implemented before real customer data is used. 
7. How does this lab connect to Lab 25 APIs and Lab 29 error bodies?
Lab 25 made the REST endpoints we're protecting in this lab. Lab 29 adds a unified `ErrorResponse` JSON body for validation failures without touching how 401 and 403 already work in this lab.
8. What metric or log field matters most for auth support?
The most important log field / metric is the correlation ID on a failed request. It's the one field alone that lets support trace a specific failure back to the exact attempt.
9. (Forward look) How will Lab 29 keep validation errors as JSON under Security?
Lab 29 will add an `ErrorRepsonse` wrapper that needs to sit behind the security filter chain. A request that fails authn or authz never reaches the validation logic, so its JSON error body only applies to requests that already passed 401/403 checks.