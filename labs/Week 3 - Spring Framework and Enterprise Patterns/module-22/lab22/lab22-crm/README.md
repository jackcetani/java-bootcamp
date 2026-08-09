# Lab 22 starter — timed path (~45 minutes)

**Theme:** @Component/@Service DI wiring

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `/` into your bootcamp examples tree as `lab22-crm`.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab22-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab22-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab22-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab22-crm
cp -R starter/. ~/java-bootcamp/examples/lab22-crm/
cd ~/java-bootcamp/examples/lab22-crm
```

## 45-minute checklist

- [ ] Annotate repository/service stereotypes (`@Repository` / `@Service`)
- [ ] Wire `CustomerService` with constructor injection (`final` fields)
- [ ] Add `@PostConstruct` / `@PreDestroy` lifecycle logs on `CustomerService`
- [ ] Fill `docs/dependency-graph.md` bean edges
- [ ] Run smoke test; capture evidence for CUS-1001

## Smoke test

```bash
mvn -B test
mvn -B spring-boot:run
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-22/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| App starts; no missing-bean errors | Pass / Fail |
| POST/GET CUS-1001 works (or unit/IT green) | Pass / Fail |
| Constructor DI + stereotypes present (no `new` of collaborators in service) | Pass / Fail |
| dependency-graph.md names CustomerService → repository/notifier | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

## Manual Verification

| Test                                                                                         | Result |
|----------------------------------------------------------------------------------------------|--------|
| Application starts with Spring-managed CRM beans.                                            | PASS   |
| Controller and service use constructor injection (no primary field @Autowired pattern).      | PASS   |
| Repository and notification services are stereotype beans—not new’d in the service.          | PASS   |
| POST/GET CUS-1001 works with lab-request-001.                                                | PASS   |
| Notification logs include customer ID + correlation without PII names if required by Lab 20. | PASS   |
| @PostConstruct runs once per context; @PreDestroy on graceful stop.                          | PASS   |
| Pure unit test constructs CustomerService with fakes/mocks.                                  | PASS   |
| Spring IT proves the real graph.                                                             | PASS   |
| dependency-graph.md matches constructors.                                                    | PASS   |
| No Spring-managed collaborator is instantiated with new inside CustomerService.              | PASS   |

## Security and Production Review
1. **Which browser, network, or API inputs are untrusted?**\
Every field a caller sends in the `create` body is untrusted, same as every other lab so far. DI changes how objects are wired together, not what is trusted. Also, the correlation-Id header is also untrusted.
2. **Where are authn/authz/validation enforced (DI does not replace them)?**\
Validation is still enforced entirely in `CustomerService`. DI does not replace or relocate that responsibility. Still no authn/authz in this lab yet.
3. **Which values are sensitive in notification/lifecycle logs?**\
`CustomerService` and `NotficationService`'s logs should never include `fullName` or `email`. Only IDs, correlation, and lifecycle status words should be included always.
4. **What can be retried safely (GET; careful create)?**\
GET is always trivially safe to retry. POST is not, which can be seen in failure experiment 3. A repeated `create` for the same ID silently overwrites rather than rejecting. Not safe.
5. **What happens after partial failure (create saved but notify fails—document)?**\
This lab doesn't wrap the notification call in its own try/catch, so a notification failure currently appears as if the whole `create` failed even though the data actually persisted. 
6. **What would an operator monitor (startup failures, missing beans, Actuator)?**\
An operator would monitor application startup failures and missing bean exceptions, which is easy because they fail loudly. With Lab 21's Actuator, they would also monitor the `health` and `beans` endpoints to check the expected graph loaded properly.
7. **Which local default is unacceptable (field injection as standard; open beans endpoint publicly)?**\
As per the rubric, `@Autowired` being the primary wiring pattern is an explicit mistake. It would hide the required dependencies and make unit-testing without a container a lot harder. Also, leaving an Actuator endpoint open without authn is unacceptable, same as before.
8. **How are bean/API contracts versioned when constructors change?**\
Adding a new required constructor param to `CustomerService` would break the app for anyone directly instantiating it in a test. Spring adapts automatically via autowiring, but hand built test fixtures need to be updated in the same commit. This is why keeping `dependency-graph.md` in sync with the constructor signatures is crucial.
