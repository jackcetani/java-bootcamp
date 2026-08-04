## Exercise 03 - MDC Clear

### Goal

Document why uncleared MDC corrupts the next CRM request on a thread.

### Step 1 — Bug story

Request A sets lab-request-001; without clear, request B logs wrong correlation.

### Step 2 — Fix

Filter/interceptor finally clears MDC.

### Step 3 — Test idea

Later IT: assert MDC empty after request (conceptual).

```java
Request A: MDC.put("correlationId", "lab-request-001") // no clear() -> thread returns to pool still holding it
Request B (same thread): MDC.get("correlationId") // -> leaked!
// Fix: filter/inceptor always runs MDC.clear() in finally
```
