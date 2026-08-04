# Lab 18 starter — timed path (~45 minutes)

**Theme:** Mockito isolation — stub / verify / never / ArgumentCaptor + BDDMockito

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `starter/` into your bootcamp examples tree as `lab18-crm`.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab18-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab18-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab18-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab18-crm
cp -R starter/. ~/java-bootcamp/examples/lab18-crm/
cd ~/java-bootcamp/examples/lab18-crm
```

Full GUIDE: [`../LAB-18-GUIDE.md`](../LAB-18-GUIDE.md)

## 45-minute checklist

- [ ] Wire `CustomerServiceMockitoTest` with `@Mock` repo + real validator
- [ ] Implement activate stub/verify; not-found `never().save`; ArgumentCaptor on add
- [ ] Complete `CustomerServiceBddMockTest` with given/then/should
- [ ] Fill `docs/isolation-policy.md`
- [ ] Run smoke test twice (full suite green)

## Smoke test

```bash
mvn -B clean test
mvn -B test -Dtest=CustomerServiceMockitoTest,CustomerServiceBddMockTest
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-18/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| Mockito suite green (stub/verify/captor) | Pass / Fail |
| BDDMockito suite green | Pass / Fail |
| not-found path never calls save | Pass / Fail |
| isolation-policy.md filled | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

## Manual Verification
| Test                                                                          | Result |
|-------------------------------------------------------------------------------|--------|
| CustomerServiceMockitoTest isolates DefaultCustomerService from the real Map. | PASS   |
| Activate-Ravi stubs find/save and asserts ACTIVE with lab-request-001.        | PASS   |
| Unknown ID verifies never().save.                                             | PASS   |
| ArgumentCaptor asserts Amina’s ID/name/status on save.                        | PASS   |
| BDDMockito test demonstrates equivalent semantics.                            | PASS   |
| Lab 17 tests still pass (or intentionally migrated with notes).               | PASS   |
| Illegal transition stub path never saves (optional but recommended).          | PASS   |
| No sensitive values in tests or Git.                                          | PASS   |
| Two consecutive mvn test runs match.                                          | PASS   |
| README documents which suites are mocked vs real-repo.                        | PASS   |

## Security and Production Review
1. **Which inputs are untrusted (production API inputs; tests use fixtures/stubs only)?**\
Every request field is untrsuted in production. Tests that use fixtures and stubs we write aren't considered untrusted, as it's something we control and is predictable.
2. **Where are authn/authz/validation enforced (still service/validator; mocks don’t replace auth)?**\
Validation is enforced still in `CustomerValidator` and `DefaultCustomerService`. Mocking the repo doesn't affect enforcement. Auth and authz remain absent in this lab.
3. **Which values are sensitive—never in test code beyond sample emails?**\
Real credentials, PII, and tokens are sensitive and should never appear in test code. Only the two fictional sample fixtures we created belong in the test files. Real data should never be tested with. 
4. **What can be retried safely (mvn test repeatedly)?**\
`mvn test` is idempotent so it is safe to retry. Mocks are rebuilt in `@BeforeEach`, so you will always get identical results form rerunning the suite.
5. **What happens after partial failure (red verify blocks merge)?**\
A failing test stops the build form succeeding. Nothing will merge or partially pass. 
6. **What would an operator/lead monitor (CI test job, isolation policy drift)?**\
They would monitor the `mcn test` pass and fail rates over time. Also, they would ensure there were not drifts in the isolation policy.
7. **Which local default is unacceptable (sleeps, mocking class-under-test, committed secrets)?**\
Mocking the class a test is under instead of its collaborator is the worst mistake to make. Using `Thread.sleep` or commiting secrets are also bad.
8. **How are stub contracts versioned when repository method signatures change?**\
If a method signature ever changes, every `when()` and `given()` stubs referencing that method need to be updated in the same commit. Stubs are only as current as the interface they're mocking.