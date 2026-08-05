# Lab 20 — logging contract

## MDC keys

| Key | Meaning |
| --- | ------- |
| corr | X-Correlation-Id |
| cust | customerId |
| op | create / get |

## Rules

- Never log fullName or email
- Always `MDC.clear()` in filter `finally`

## Logging contract
- Required MDC: correlationId, customerId (when known), op
- Allowed: customerId, status, reason codes, durations, HTTP status
- Forbidden: fullName, email, phone, address, passwords, tokens, PAN
- Correlation header: X-Correlation-Id (example lab-request-001)
- Levels: INFO success path; WARN business reject; ERROR unexpected
- Production: ship to central store; never embed secrets in patterns

## Sample INFO lines 
2026-08-05 14:24:49,369 INFO  [http-nio-auto-1-exec-2] o.a.c.c.C.[Tomcat].[localhost].[/] corr= cust= op= - Initializing Spring DispatcherServlet 'dispatcherServlet'
2026-08-05 14:24:49,369 INFO  [http-nio-auto-1-exec-2] o.s.web.servlet.DispatcherServlet corr= cust= op= - Initializing Servlet 'dispatcherServlet'
2026-08-05 14:24:49,369 INFO  [http-nio-auto-1-exec-2] o.s.web.servlet.DispatcherServlet corr= cust= op= - Completed initialization in 0 ms
2026-08-05 14:24:49,402 INFO  [http-nio-auto-1-exec-2] c.n.crm.service.CustomerService corr=lab-request-001 cust=CUS-1001 op=customer.get - Loading customer
2026-08-05 14:24:49,402 INFO  [http-nio-auto-1-exec-2] c.n.crm.service.CustomerService corr=lab-request-001 cust=CUS-1001 op=customer.get - Customer lookup complete durationMs=0 found=true

## Concepts to Discuss
1. **Main flow: request → filter MDC → controller → service → appender**\
Request hits `CorrelationFilter` -> sets `corr` in MDC -> `CustomerController` -> `CustomerService` -> logback console appender.
2. **Trust boundary: what is safe to log vs PII**\
It is safe to log `customerId`, `status`, reason codes, HTTP status, and durations. Things NOT safe to log include `fullName`, `email`, `phone`, `address`, and anything from an `Authorization` header.
3. **Success/failure contracts at INFO vs WARN vs ERROR**\
INFO means the op completed as requested. WARN means a business rule rejected the request, such as a blank name. ERROR means something unexpected broke, like a repository failure.
4. **Stable identity (customerId, correlation) vs free-text names**\
CUS-1001 and lab-request-001 are the kind of values worth putting in MDC. Free text should never enter the MDC or a log message as it could be sensitive.
5. **Idempotent create logging (duplicate WARN without payload dump)**\
A repeated `create` for an ID that already exists will log the exact same way every time. It will throw a WARN with `reason=duplicate` rather than silently failing on the retry.
6. **Local console DEBUG vs production INFO + central shipping**\
Dropping DEBUG on `com.northstar.crm` while developing is fine locally. However in production, root and package level should default to INFO. Also, logs get shipped to a central store rather than living only in a console somebody has to be watching live.
7. **Evidence operators need (search by lab-request-001)**\
They would need evidence lik create attempts, validation outcomes, and results, i.e. the full story. Thus, having the ability to search for `lab-request-001` without needing a stake trace or database query is crucial for operators.
8. **Two instances: MDC must not leak across threads/requests**\
Since Tomcat reuses a pool of threads across requests, anything left in MDC after a request finishes can be seen by the request the same thread picks up next. This is why you must clear MDC in a `finally` at the end.
9. **Why %X{...} patterns beat ad-hoc string concatenation**\
Using placeholders like `%X{corr}` creates consistency in logger outputs, regardless of the call that produced it. This makes output more parseable, and sometimes more readable, than with basic string concatenation.
10. **What Lab 21 will add (metrics) without putting IDs in metric tags**\
Lab 21 will add metrics and Actuator health data layer on top of these exact logs. CustomerID will stay in the logs since they're safe, but they must never become Micrometer tags.

