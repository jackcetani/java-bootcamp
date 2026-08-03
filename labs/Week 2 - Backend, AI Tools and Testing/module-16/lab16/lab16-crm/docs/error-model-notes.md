# Lab 16 — error model notes

| Status | Code | When |
| ------ | ---- | ---- |
| 400 | VALIDATION_FAILED | Bean Validation on request DTO |
| 404 | CUSTOMER_NOT_FOUND | Unknown customer id |
| 409 | BUSINESS_CONFLICT | Illegal status transition / duplicate |

Correlation id: `lab-request-001`

## 409 vs. 422

Document why 409 (not 422) for illegal transitions after you finish the demos.

We chose 409 over 422 because the request itself is well-formed and passes field validation.
The failure is that it conflicts with the customer's current state (an ACTIVE customer can't
move back to PROSPECT), which is exactly what HTTP 409 Conflict means. 422 is reserved here for
malformed but parseable request bodies, which our 400/VALIDATION_FAILED path already owns.

### Concepts to Discuss
1. **Main error flow (throw/fail → handler → ErrorResponse)**\
throw/Fail -> `GlobalExceptionHandler` -> `ErrorResponse`
2. **Trust boundary: what clients may see vs what logs may hold**\
Clients only see the fields for `ErrorResponse`. Full stack traces stay server-side, so they are logged, not returned.
3. **Success vs 400 vs 404 vs 409 contracts**\
Success mean `ApiResult.Ok`. 400 means the shape is wrong. 404 means doesn't exist. 409 means exists but the requested change conflicts with a policy.
4. **Stable identity in messages (CUS-9999) without dumping entities**\
`"Customer not found: CUS-9999"` names the ID without dumping the entity object. 
5. **Retry implications (404/409 often not blindly retried; 500 maybe)**\
404 and 409 shouldn't be blindly retried. 500 might be alright with one retry since it could be transient.
6. **Why one JSON shape beats ad-hoc ex.getMessage() strings for React**\
React can reliably branch on `status`/`error` fields. Parsing arbitrary exception text is fragile and breaks on message wording changes.
7. **Correlation ID as the support join key across services**\
`lab-request-001` is what support pastes into logs. This can be used to find every layer's record of one request.
8. **Two instances: correlation still works without shared memory**\
Correlation still works without chared memory. It is just a string carried through the call, not shared state.
9. **Why 500 messages must be generic**\
Any real detail is internal information that shouldn't reach an external client. This includes DB error text, the stack trace, etc.
10. **What Spring @ControllerAdvice will wrap without changing the payload fields**\
It wraps the HTTP transport and annotation wiring change. The `ErrorResponse` fields and `BusinessException` code stays the same.