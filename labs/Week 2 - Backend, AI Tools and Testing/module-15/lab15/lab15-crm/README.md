# Lab 15 starter — timed path (~45 minutes)

**Theme:** Service layer + CustomerValidator status transitions (no persistence leak)

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `starter/` into your bootcamp examples tree as `lab15-crm`.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab15-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab15-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab15-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab15-crm
cp -R starter/. ~/java-bootcamp/examples/lab15-crm/
cd ~/java-bootcamp/examples/lab15-crm
```

Full GUIDE: [`../LAB-15-GUIDE.md`](../LAB-15-GUIDE.md)

## 45-minute checklist

- [ ] Implement `InMemoryCustomerRepository` (private Map)
- [ ] Fill `CustomerValidator` ALLOWED transitions + validateNew / validateTransition
- [ ] Implement `DefaultCustomerService` constructor DI + changeStatus (validate before mutate)
- [ ] Main: activate CUS-1002; reject ACTIVE→PROSPECT on CUS-1001; prove status unchanged
- [ ] Finish `CustomerValidatorTest`; fill `docs/service-layer-notes.md`
- [ ] Run smoke test

## Smoke test

```bash
mvn -B clean test
# After TODOs: run Main from IntelliJ, or:
# mvn -B -q -DskipTests package && java -cp "target/classes;target/dependency/*" com.northstar.crm.Main
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-15/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| Validator tests green (legal + illegal + duplicate) | Pass / Fail |
| Main activates CUS-1002 to ACTIVE | Pass / Fail |
| Illegal transition leaves CUS-1001 ACTIVE; message includes lab-request-001 | Pass / Fail |
| No HashMap / JDBC in service package | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

## Security and Production Review
1. **Which inputs are untrusted (all client fields reaching the service)?**\
All client fields reaching the service are considered untrusted.
2. **Where are authn/authz/validation enforced (shape at facade; meaning in validator; auth still absent)?**\
Shape is enforced at the facde, meaning enforced in `CustomerValidator`. Auth is still missing in this lab.
3. **Which values are sensitive, and where stored?**\
None are sensitive beyond the sample emails, they are held in memory only.
4. **What can be retried safely (findById; changeStatus depending on your noop policy)?**\
`findById` is always safe to rety. `changeStatus` is only safe if changing status to the current status is considered safe. However, in this lab, it is documented as unsafe and to reject.
5. **What happens after partial failure (no status write if validation fails)?**\
No status write happens if `validateTransition` throws. The order matters and is enforced, you must validate before mutation.
6. **What would an operator monitor (rejected transition counts, correlation IDs)?**\
Operators would monitor rejected-transition counts and correlation ids on failures.
7. **Which local default is unacceptable in production (in-memory; no auth)?**\
Local defaults unacceptable in prod are in-memory storage and no auth on `changeStatus`.
8. **How are transition policies versioned when product changes KYC rules?**\
When product changes HYC rules, the `ALLOWED` static block is the single place to update. It needs its own change-review process since it's a business policy, not just code.