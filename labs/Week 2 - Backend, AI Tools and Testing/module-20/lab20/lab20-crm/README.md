# Lab 20 starter — timed path (~45 minutes)

**Theme:** Structured logging — Logback pattern, CorrelationFilter MDC, PII-free service logs

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `starter/` into your bootcamp examples tree as `lab20-crm`.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab20-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab20-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab20-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab20-crm
cp -R starter/. ~/java-bootcamp/examples/lab20-crm/
cd ~/java-bootcamp/examples/lab20-crm
```

Full GUIDE: [`../LAB-20-GUIDE.md`](../LAB-20-GUIDE.md)

## 45-minute checklist

- [ ] Confirm `logback-spring.xml` pattern includes %X{corr}/%X{cust}/%X{op}
- [ ] Implement `CorrelationFilter` (MDC put + finally clear + header echo)
- [ ] Instrument `CustomerService` create/get (INFO; no fullName/email)
- [ ] Complete `CustomerLoggingIT` assertions
- [ ] Fill `docs/logging.md`; run smoke test

## Smoke test

```bash
mvn -B -Dtest=CustomerLoggingIT test
mvn -B clean verify
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-20/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| CustomerLoggingIT green | Pass / Fail |
| Logs include lab-request-001 and customer ids | Pass / Fail |
| Logs do not contain Amina / email PII | Pass / Fail |
| MDC cleared after request (no bleed across requests) | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

## Manual Verification
| Test                                                                   | Result |
|------------------------------------------------------------------------|--------|
| Logback pattern includes corr, cust, and op MDC keys.                  | PASS   |
| Filter defaults missing correlation to lab-request-001 and clears MDC. | PASS   |
| Create CUS-1001 logs contain customer ID and correlation, not “Amina”. | PASS   |
| Get path logs op=customer.get with cust=CUS-1001.                      | PASS   |
| Blank-name create produces WARN reason without body dump.              | PASS   |
| Duplicate create (if applicable) WARNs with reason=duplicate.          | PASS   |
| CustomerLoggingIT passes with doesNotContain(“Amina”).                 | PASS   |
| No emails/phones in committed evidence excerpts.                       | PASS   |
| Two consecutive mvn test / verify runs match.                          | PASS   |
| docs/logging.md forbidden-field list matches practice.                 | PASS   |

## Security and Production Review
1. **Which browser, network, or API inputs are untrusted?**\
Every field a caller sends in a create body is untrusted, as well as the correlation ID header itself. This includes `customerId`, `fullName`, `email`, `status`. Callers can send whatever they want through these inputs.
2. **Where are authn/authz/validation enforced (logs do not replace them)?**\
Validation still is enforced in `CustomerService` and `CustomerController`. Logging has added visibility to what happened, but doesn't replace or add any enforcement. Thus, no real authn / authz in this lab yet.
3. **Which values are sensitive—forbidden in logs/MDC?**\
PII like full names, email, phone, address, passwords, and tokens must never appear in a log message or MDC key. This labs purpose is to create logging, but also be structured in a way to make that boundary hard to accidentally cross.
4. **What can be retried safely (read paths; careful create)?**\
GET is always safe. POST is not safe to retry with the same ID, as this service has no duplicate ID safeguard yet and will silently overwrite rather than reject a repeated POST.
5. **What happens after partial failure (ERROR + correlation for search)?**\
After a partial error, an ERROR log with a correlation ID will tell what to search for. You can view what happened in `lab-request-001` without needing database or code access to get the story.
6. **What would an operator monitor (error rate by op; search by correlation)?**\
An operator would monitor error rate broken down by `op`. For example, looking if `create` fails more than `get` for `customer`. Also, the ability to search by corr ID across the logs is crucial for an operator.
7. **Which local default is unacceptable (DEBUG with payloads, PII in MDC)?**\
A big mistake would be running with DEBUG logging that dumps full bodies. It is a very bad practice and risks letting PII or other sensitve info into the logs, which should NEVER happen. Also, putting anything from an `Authorization` header into MDC is just as bad.
8. **How are logging contracts versioned when ops rename?**\
If an operation gets renamed all the dashboards, alert rules, and support runbooks of the logs would stop finding results, as they are searching for the old operation name. That's why a reviewable contract like `logging.md` exists.