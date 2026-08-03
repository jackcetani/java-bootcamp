# Lab 16 starter — timed path (~45 minutes)

**Theme:** API exception handling — ErrorResponse + GlobalExceptionHandler + correlation

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `starter/` into your bootcamp examples tree as `lab16-crm`.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab16-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab16-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab16-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab16-crm
cp -R starter/. ~/java-bootcamp/examples/lab16-crm/
cd ~/java-bootcamp/examples/lab16-crm
```

Full GUIDE: [`../LAB-16-GUIDE.md`](../LAB-16-GUIDE.md)

## 45-minute checklist

- [ ] Implement `ErrorResponse` (+ toJson always includes errors)
- [ ] Complete `BusinessException.notFound` / `conflict` factories
- [ ] Implement `GlobalExceptionHandler` (business / validation / unexpected)
- [ ] Wire facade create/get/changeStatus to return `ApiResult` (catch BusinessException before Exception)
- [ ] Refactor validator/service to throw BusinessException; demo 400/404/409 in Main
- [ ] Finish `GlobalExceptionHandlerTest`; run smoke test

## Smoke test

```bash
mvn -B clean test
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-16/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| Handler tests green (404 / 409 / generic 500) | Pass / Fail |
| Facade Fail JSON includes correlationId lab-request-001 | Pass / Fail |
| 400 validation / 404 not-found / 409 conflict demonstrated | Pass / Fail |
| Illegal transition leaves CUS-1001 ACTIVE | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

### Manual Verification
| Test                                                               | Result |
|--------------------------------------------------------------------|--------|
| Create/get CUS-1001 still succeeds (Ok path).                      | PASS   |
| Invalid email → 400 with errors.email and correlation.             | PASS   |
| CUS-9999 → 404 payload.                                            | PASS   |
| Illegal transition → 409 payload; status unchanged.                | PASS   |
| Correlation on every failure.                                      | PASS   |
| No stack traces in client-facing JSON.                             | PASS   |
| Handler unit tests pass.                                           | PASS   |
| No secrets in Git; target/ ignored.                                | PASS   |
| README documents status choices.                                   | PASS   |
| You can explain Spring @ControllerAdvice mapping in one paragraph. | PASS   |

### Security and Production Review
1. **Which inputs are untrusted (all request fields + headers later)?**\
All request fields, and eventually headers once HTTP exists. Right now the only trusted data in the system is what our own demo code creates.
2. **Where are authn/authz/validation enforced (validation/business now; auth still absent)?**\
Validation happens via teh Jakarta Bean Validation, and business rules are enforced one layer deeper in `CustomerValidator`/`DefaultCustomerService`. Auth and authz are still absent in this lab/
3. **Which values are sensitive—never in ErrorResponse?**\
None of the values should be in `ErrorResponse`, they are all considered sensitive. Only the fixed fictional CUS-1001/1002 data should appear in logs or output.
4. **What can be retried safely (reads; not blind repeat of conflicts)?**\
`getById` is always safe to retry. `create` is not safe to blindly retry, since it could create a duplicate-ID and return a 409. Although it is a safe failure, it is not a success.
5. **What happens after partial failure (Fail payload; no silent success)?**\
Every failure path returns an error response, there is never a silent success or partial changes. For example, a rejected 409 leaves the record exactly the same, no half-updated state.
6. **What would an operator monitor (400/404/409 rates by correlation)?**\
An operator would likely monitor request volume and error rate by status code (400/404/409/500). Correlation ID would be the next step if tracing a specific client is required.
7. **Which local default is unacceptable in production (stack traces to clients; empty correlation)?**\
Leaking `ex.getMessage()` or a stack trace to a client is unacceptable. Also, letting a blank or missing correlationId flow through silently would also be unacceptable.
8. **How are error contracts versioned when field names change?**\
In this lab, renaming an `ErrorResponse` field (like `error1 to `errorCode`) would break any client parsing the JSON. It is the same discipline Lab 13 established with WSDL namespace versioning.