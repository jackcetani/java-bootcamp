## Lab 16 Answers

### Concepts to Discuss

### Security and Production Review

### Reflection Questions
1. **Which design decision most affected correctness?**\
The most important design decision was catching `BusinessException` before the generic `Exception` catch. If this were reverse, every business conflict silent becomes a 500.
2. **Which failure was hardest to diagnose?**\
The hardest failure to diagnose was the one mentioned above. A 409 that appears as a 500 gives no hints about what actually went wrong.
3. **What evidence proves the implementation works?**\
The three JSON payloads (400/404/409) all carrying `lab-request-001` and `GlabalExceptionHandlerTest` passing are both evidence of it working.
4. **What breaks first at ten times the error volume?**\
Manually going through console JSON becomes difficult. You'd want structured logging/metrics aggregation.
5. **Which concern should move to shared infrastructure (logging/MDC)?**\
Correlation propagation should move to shared infrastructure. An MDC/logging framework instead of a manually-threaded string parameter would be needed.
6. **What must change before real customer data is used?**\
A stricter audit on `ErrorResponse.message` to guarantee it never echoes back anything sensitive from a real customer record.
7. **How does this lab connect to Labs 14–15 and Spring advice later?**\
The connection is Lab 14's violations and Lab 15's `BusinessException`. Failures funnel into this one handler, and `@ControllerAdvice` just changes who calls it later on.
8. **What metric or log field matters most when correlating client complaints?**\
The correlation ID matters most. You would ask a client for their ID, then grep logs.
9. **(Forward look) Which ErrorResponse fields must stay stable when HTTP arrives?**\
`status`, `error`, `correlationId` and `error` all must stay stable once HTTP arrives. A React client will hard-code parsing against these.