## Exercise 03 - MDC LifeCycle

### Goal

Sketch put → use → clear MDC for correlation across a request.

### Step 1 - Put

On request entry: MDC.put("correlationId", "lab-request-001").

### Step 2 — Use

Service logs automatically include correlation via pattern.

### Step 3 — Clear

finally { MDC.clear(); } or remove key — prevent leak to next request.

### Step 4 — Boundary

Note metrics/alerts deepen in Lab 21; here focus logs/MDC.

```java
try {
    MDC.put("correlationId", "lab-request-001");
    log.info("Processing request"); // corr auto-included
} finally { MDC.clear(); } // never skip this
```