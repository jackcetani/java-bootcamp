# Lab 23 starter — timed path (~45 minutes)

**Theme:** Boot auto-config / application.yml basics

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `starter/` into your bootcamp examples tree as `lab23-crm`.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab23-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab23-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab23-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab23-crm
cp -R starter/. ~/java-bootcamp/examples/lab23-crm/
cd ~/java-bootcamp/examples/lab23-crm
```

## 45-minute checklist

- [ ] Confirm `pom.xml` has web + actuator + test starters
- [ ] Complete `application.yml` (name, port, Actuator exposure)
- [ ] Fill profile teasers `application-dev.yml` / `application-prod.yml`
- [ ] Implement create/get for CUS-1001 / CUS-1002 in service
- [ ] Smoke: health UP; note 3 auto-config gifts vs 3 ownership items in docs

## Smoke test

```bash
mvn -B test
mvn -B spring-boot:run
# then: curl http://localhost:8080/actuator/health
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-23/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| App starts on 8080 | Pass / Fail |
| /actuator/health returns UP | Pass / Fail |
| CUS-1001 create/get evidence (or IT green) | Pass / Fail |
| YAML + profile teasers present | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

## Manual Verification
| Test                                                                | Result |
|---------------------------------------------------------------------|--------|
| mvn spring-boot:run prints Started CrmApplication on 8080.          | PASS   |
| POST create returns 201 for CUS-1001 and CUS-1002 with correlation. | PASS   |
| GET /api/customers/CUS-1001 returns Amina / ACTIVE.                 | PASS   |
| GET missing ID returns 404.                                         | PASS   |
| Invalid/blank body is rejected (400) when validation is enabled.    | PASS   |
| /actuator/health is UP; info optional but documented.               | PASS   |
| dev profile changes log/detail behavior versus prod file intent.    | PASS   |
| Two consecutive mvn test runs match.                                | PASS   |
| README documents run/cleanup and auto-config ownership notes.       | PASS   |
| No sensitive values in YAML or Git.                                 | PASS   |

## Security and Production Review
1. **Which inputs are untrusted (JSON body, headers)?**\
   Both the JSON request body and the `X-Correlation-Id` header are untrusted. Antyhing a caller sends could be malformed, malicious, oversized, or just a wrong input. `@Valid` is the only thing standing between a bad request and the service layer, so caution is required.
2. **Where are authn/authz/validation enforced (validation now; full security later)?**\
   Bean Validation is enforced in the Controller. Authn/authz is still out of scope for this lab, this will be a security focus for a later lab.
3. **Which values are sensitive — never commit API keys or real DB passwords?**\
   Real credentials, PII, or any API keys should never be commited. We must be sure no API keys are in the project `.yml` files, as real secret handling will be necessary in a later lab.
4. **What can be retried safely (GET, health; careful with duplicate POST create)?**\
   `GET` and `health` are safe to retry always. `POST` on the other hand is not necessarily safe. As of now, creating a duplicate customer ID silently overwrites rather than failing, which can have unexpected or unwanted behavior.
5. **What happens after partial failure (in-memory put is atomic per key; no multi-row TX yet)?**\
   The in-memory `put` is atomic per key, but there's no multi-record transaction history yet. Creating two related records atomically isn't supported in this lab, and shouldn't be attempted.
6. **What would an operator monitor (health, startup errors, HTTP 4xx/5xx)?**\
   An operator would likely monitor the `actuator/health` status, application startup logs for exceptions/failures, and the HTTP 4xx/5xx rates on `api/customers`. The `health` status would tell you the process is actually, alive, while the others would tell a monitor if everything is working correctly.
7. **Which local default is unacceptable in production (open Actuator, debug everywhere)?**\
   Leaving the Actuator wide open (with an incorrect `include`, for example) or anything reachable outside the local environment is a big mistake. The `application-prod.yml` in this lab specifically attempts to narrow exposure to just `health` for this reason.
8. **How are API contracts versioned when Lab 24 adds SOAP?**\
   The REST paths, JSON field names, status codes, and correlation-header behavior in this lab should stay the exact same for Lab 24. We are only adding a parallel SOAP endpoint in Lab 24, so changing the contracts would also break whatever depends on the service behavior.
