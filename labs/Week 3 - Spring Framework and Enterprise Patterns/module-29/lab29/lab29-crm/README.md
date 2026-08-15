# Lab 29 starter — timed path (~45 minutes)

**Theme:** @Valid + GlobalExceptionHandler

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `/` into your bootcamp examples tree as `lab29-crm`.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab29-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab29-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab29-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab29-crm
cp -R starter/. ~/java-bootcamp/examples/lab29-crm/
cd ~/java-bootcamp/examples/lab29-crm
```

## 45-minute checklist

- [ ] Add Bean Validation annotations on `CustomerRequest`
- [ ] Put `@Valid` on controller create method
- [ ] Implement `GlobalExceptionHandler` for 400/404/409 (+ safe 500)
- [ ] Shape `ErrorResponse` (+ field violations)
- [ ] Smoke: bad email → 400; CUS-9999 → 404; duplicate CUS-1001 → 409

## Smoke test

```bash
mvn -B test
mvn -B spring-boot:run
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-29/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| Invalid POST returns 400 with violations | Pass / Fail |
| Missing customer returns 404 envelope | Pass / Fail |
| Duplicate returns 409 | Pass / Fail |
| Happy GET CUS-1001 / CUS-1002 still 200 | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

## Manual Verificaiton

| Test                                                                  | Result |
|-----------------------------------------------------------------------|--------|
| Valid GET Amina (CUS-1001) / Ravi (CUS-1002) still succeed.           | PASS   |
| Invalid create returns 400 ErrorResponse with violations.             | PASS   |
| CUS-9999 returns 404 envelope with correlation ID.                    | PASS   |
| Duplicate CUS-1001 returns 409.                                       | PASS   |
| Illegal status transition returns mapped 400/422 (not 500).           | PASS   |
| Fallback 500 does not leak stack traces to clients.                   | PASS   |
| Correlation lab-request-001 present on error bodies when header sent. | PASS   |
| MockMvc covers validation and not-found.                              | PASS   |
| Two consecutive mvn test runs match.                                  | PASS   |
| Lab 14/16 unify note documented; no secrets committed.                | PASS   |

## Security and Production Review

1. Which inputs are untrusted (JSON bodies, path IDs, headers)?
   The JSON body, path IDs, and the X-Correlation-Id header are all untrusted. `@Valid` catches shape problems, and the service catches business-state problems. The correlation header is only ever echoed back, never trusted for anything structural.
2. Where are authn (Lab 28), authz, and validation enforced?
   Since I did not copy my Lab 28 and worked off the starter for this lab, only validation is enforced in this lab. Authn and authz are Lab 28's responsibility and validation still needs to return JSON even when security is active.
3. Which values are sensitive — never in `rejectedValue` or client 500 bodies?
   The `rejectedValue` in a `FieldViolation` must never echo a password or JWT. Only the fiction fixture data should appear in this lab's violations. Failure experiment 4 proves the 500 fallback never leaks connection strings or secrets to the client too.
4. What can be retried safely (which statuses)?
   None of the 400/404/409 family can be retried safely. The request is either malformed or conflicts with existing state, and retying just repeats the same exact failure. A 500/503 might be safe for a retry.
5. What happens after partial failure (client retries after 409)?
   Nothing changes on the server side from a partial failure attempt. The duplicate check runs before any persistence, so a 409 would leave the state untouched. The client needs to change its request, not just resend the same one.
6. What would an operator monitor (validation failure rate, 404 rate)?
   An operator would likely monitor the validation failure rate and 404 rate separately. A spike in 400s means a client side bug usually, while a spike in 404s might mean stale client caching or broken ID generation.
7. Which local default is unacceptable (leaky 500 messages, default correlation always `lab-request-001` in prod)?
   Defaulting the `correlationId` to the actual string `lab-request-001` would be unacceptable in production. It is fine for a lab environment, but a real prod system would need to generate a unique correlation ID per request, not a hardcoded constant.
8. How is the `ErrorResponse` contract versioned when fields change?
   In this lab, nothing versions it yet. Renaming `violation` would break every client parsing the current shape. A real API would need versioning or clear documentation process before ever renaming or removing a field.
