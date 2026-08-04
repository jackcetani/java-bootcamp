## Exercise 02 - Safe Logs

### Goal

Turn unsafe Customer logs into id+status+correlation lines.

### Unsafe

**Example bad:** log full Customer toString including email/phone if present.

### Safe

**Rewrite:** customerId=CUS-1001 status=ACTIVE correlation=lab-request-001.

### Ravi line

Write a safe activate start line for CUS-1002 PROSPECT.

```java
log.info("Customer {}", customer); // BEFORE: unsafe
log.info("customerId={} status={} corr={}",  // AFTER: safe
    "CUS-1001", "ACTIVE", :lab-request-001");
```
