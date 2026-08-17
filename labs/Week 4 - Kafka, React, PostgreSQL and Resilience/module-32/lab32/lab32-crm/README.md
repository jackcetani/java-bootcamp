# Lab 32 starter — timed path (~45 minutes)

**Theme:** Resilience4j — retry, circuit breaker, time limiter

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `/` into your bootcamp examples tree as `lab32-crm`.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab32-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab32-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab32-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab32-crm
cp -R starter/. ~/java-bootcamp/examples/lab32-crm/
cd ~/java-bootcamp/examples/lab32-crm
```

## 45-minute checklist

- [ ] Review `application.yml` Resilience4j instances named `accountProfile`
- [ ] Annotate `AccountProfileService.find` (CB + Retry + TimeLimiter)
- [ ] Implement truthful `fallback` → `AccountSummary.unavailable`
- [ ] Complete WireMock-based `AccountProfileResilienceTest` scenarios
- [ ] Run `mvn -B test`; note OPEN fails-fast behavior

## Smoke test

```bash
mvn -B test
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-32/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| Healthy stub returns available=true for CUS-1001 | Pass / Fail |
| Fallback returns available=false (not fake success) | Pass / Fail |
| OPEN / timeout scenarios covered in test | Pass / Fail |
| resilience-notes.md mentions instance name accountProfile | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

## Manual Verification

| Test                                                                             | Result |
|----------------------------------------------------------------------------------|--------|
| Healthy account call for CUS-1001 returns available=true (or accounts present).  | PASS   |
| 503 recovery path retries then succeeds (or degrades honestly if still failing). | PASS   |
| CB OPEN fails fast; WireMock journal does not grow on OPEN probes.               | PASS   |
| 3s stub triggers TimeLimiter near 1500ms then fallback.                          | PASS   |
| Fallback body uses AccountSummary.unavailable (available=false).                 | PASS   |
| Writes are not marked successful via the same fallback pattern.                  | PASS   |
| Actuator shows state transitions / metrics.                                      | PASS   |
| Resilience tests pass twice consecutively.                                       | PASS   |
| lab-request-001 / customer IDs appear in useful logs (no secrets).               | PASS   |
| Docs warn lab thresholds are not production defaults.                            | PASS   |

## Security and Production Review

1. **Which remote/network inputs are untrusted (account JSON)?**\
   The entire response body from the account service is untrusted input. This includes the status code, headers, and JSON payload.
2. **Where are authn/authz enforced for CRM vs outbound account calls (propagate tokens carefully)?**\
   CRM auth protects the agent-facing API. The outbound call to the account service in this lab carries only a correlation header, not a token. Prod would need to decide whether to propagate or mint a separate credential.
3. **Which values are sensitive on outbound headers?**\
   The `X-Correlation-Id` is the only sensitive thing sent outbound. No JWT or PII is forwarded, since Lab 51's auth work hasn't been implemented yet.
4. **What can be retried safely (idempotent GETs only)?**\
   Only the GET to `/accounts/{customerId}` can be retried safely. Nothing else in this lab's outbound path is retried.
5. **What happens after partial failure (CRM OK, accounts unavailable)?**\
   The customer record is unaffected after a partial failure. A degraded account read never blocks or corrupts the core CRM read, the agent just sees a banner instead of an accounts list.
6. **What would an operator monitor (CB state, timeout rate, fallback rate)?**\
   An operator would moniter the circuit breaker state with `circuitbreakerevents`, the timeout-fallback rate, and how often `account_profile_degraded` was showing up in the logs.
7. **Which local default is unacceptable (tiny windows, endless retries, false write success)?**\
   The 10 second open-wait and 10-call sliding window are too small and fast for real traffic volume. They'd flap open and closed under normal noise, but prod values need real SLO tuning.
8. **How are degraded-response contracts versioned for React?**\
   This lab keeps a fixed contract with the `available` boolean and `accounts` list. Any future change to that shape would need explicit versioning so React doesn't silently misread a changed field.
