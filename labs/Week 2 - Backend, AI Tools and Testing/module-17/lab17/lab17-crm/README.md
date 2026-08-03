# Lab 17 starter — timed path (~45 minutes)

**Theme:** JUnit 5 service tests + parameterized transitions + JaCoCo ≥80%

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `/` into your bootcamp examples tree as `lab17-crm`.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab17-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab17-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab17-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab17-crm
cp -R starter/. ~/java-bootcamp/examples/lab17-crm/
cd ~/java-bootcamp/examples/lab17-crm
```

Full GUIDE: [`../LAB-17-GUIDE.md`](../LAB-17-GUIDE.md)

## 45-minute checklist

- [ ] Wire `CustomerServiceTests` @BeforeEach with fresh repo/validator/service
- [ ] Implement happy + negative tests (duplicate, illegal transition, not-found)
- [ ] Complete parameterized legal/illegal transition tests
- [ ] Confirm JaCoCo check on `com.northstar.crm.service` (≥0.80); optional deliberate 0.99 fail
- [ ] Fill `docs/junit-runbook.md`; run smoke test

## Smoke test

```bash
mvn -B clean test
mvn -B clean verify
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-17/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| CustomerServiceTests green | Pass / Fail |
| Parameterized transition tests green | Pass / Fail |
| mvn clean verify passes JaCoCo gate (≥80% on service package) | Pass / Fail |
| Runbook documents commands + evidence | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

## Manual Verification

| Test                                                                       | Result |
|----------------------------------------------------------------------------|--------|
| CustomerServiceTests covers add/find/activate/duplicate/illegal/not-found. | PASS   |
| Parameterized legal and illegal transitions run.                           | PASS   |
| JaCoCo service package ≥80% and check passes.                              | PASS   |
| Copilot review exists if Copilot was used.                                 | PASS   |
| Two consecutive mvn test runs match.                                       | PASS   |
| Correlation asserted where exceptions include it.                          | PASS   |
| No sensitive values in tests or Git.                                       | PASS   |
| Deliberate gate-fail evidence then restore.                                | PASS   |
| README documents verify command.                                           | PASS   |
| You can point in JaCoCo HTML to a previously red branch.                   | PASS   |

## Security and Production Review
1. **Which inputs are untrusted (production inputs; tests use fixtures only)?**\
In production, every request field is considered untrusted. Inside the test suite, nothing is untrusted. Tests only use the fixed customer fixtures we control.
2. **Where are authn/authz/validation enforced (still service/facade; tests don’t replace auth)?**\
Enforcement is still in the service/facade layers entirely. Tests only verify that enforcement behaves correctly, they don't add any new enforcement. Auth / authz still absent in this lab.
3. **Which values are sensitive—never in test code beyond samples?**\
Test code should not include any sensitive data, such as real credentials, tokens, PII, etc. The two fixed fictional customers are the only acceptable data in test files.
4. **What can be retried safely (mvn test/verify)?**\
`mvn test` and `mvn verify` are idempotent and safe to retry. They only write to the disposable `target/` output.
5. **What happens after partial failure (red build blocks merge)?**\
A failing test or coverage gate breaks the build. Nothing partially merges here, no partial failures.
6. **What would an operator/lead monitor (CI verify, coverage trend)?**\
An operator would monitor the `verify` job's pass/fail rate. Also, the coverage trend line for `com.northstar.crm.service`. A slow downward drift is usually an indicator of new code outpacing the tests.
7. **Which local default is unacceptable (sleeps, ignored gate, committed secrets)?**\
One is teh `Thread.sleep` calls hidden inside tests. They make the suite slower and less correct. Also, raising the covergae threshold or disabling the gate is unnaceptable.
8. **How are test contracts versioned with DTO/service changes?**\
Tests should eb updated in the same commit as the DTO/service changes that motivated them. For example, a `CustomerRequestDTO` field rename with no test update means the suite is out of sync with the code.