# Lab 19 starter — timed path (~45 minutes)

**Theme:** Selenium + HTTP integration — API IT, Page Object UI, regression evidence

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `starter/` into your bootcamp examples tree as `lab19-crm`.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab19-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab19-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab19-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab19-crm
cp -R starter/. ~/java-bootcamp/examples/lab19-crm/
cd ~/java-bootcamp/examples/lab19-crm
```

Full GUIDE: [`../LAB-19-GUIDE.md`](../LAB-19-GUIDE.md)

## 45-minute checklist

- [ ] Implement `CustomerController` POST/GET with X-Correlation-Id
- [ ] Wire `customers.html` fetch + data-testid hooks
- [ ] Complete `CustomerApiIT` (get / create+correlation / 404)
- [ ] Implement `CustomerFormPage` + headless `CustomerUiIT`
- [ ] Fill `docs/regression-notes.md`; run smoke test

## Smoke test

```bash
mvn -B -Dtest=CustomerApiIT test
mvn -B -Dtest=CustomerUiIT test
# optional: mvn -B spring-boot:run
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-19/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| CustomerApiIT green | Pass / Fail |
| CustomerUiIT green (Chrome/Chromium available) | Pass / Fail |
| UI uses data-testid + Page Object (no raw sleeps) | Pass / Fail |
| Correlation header echoed on create | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

## Unit vs IT vs UI, and CI browser strategy:
Unit tests from the last two labs mock or use an in-memory repo and never touch HTTP. This is the fastest and most isolated approach. `CustomerApiIT` boots the real spring context and hits real HTTP, proving the network contract without any browser involved. `CustomerUiIT` on the other hand goes one layer further and drives an actual browser (Chrome) through the same running app, showing what the user would actually see. Locally, headed Chrome is useful for watching a test run for debugging. CI always runs headless via WebDriverManager-resolved ChromeDriver, since there's no display. No ChromeDriver binary is ever committed to the repo, and the exact same Page Object works in both.

## Manual Verification
| Test                                                                | Result |
|---------------------------------------------------------------------|--------|
| CustomerApiIT covers create/get for CUS-1001 with correlation echo. | PASS   |
| Not-found returns 404 for a missing customer ID.                    | PASS   |
| Manual or automated UI creates Amina and shows result text.         | PASS   |
| Page Object encapsulates locators (data-testid).                    | PASS   |
| Blank-name UI case asserts a validation message.                    | PASS   |
| WebDriverManager headless Chrome starts and quits cleanly.          | PASS   |
| No arbitrary Thread.sleep required for green suite.                 | PASS   |
| Regression run twice (or after trivial edit) stays green.           | PASS   |
| Deliberate broken locator produced a screenshot, then restored.     | PASS   |
| README/docs explain unit vs IT vs UI and CI browser strategy.       | PASS   |

## Security and Production Review
1. **Which browser, network, or API inputs are untrusted?**\
Everything a user types into the form (`customerId`, `fullName`, `email`, `status`) and everything the caller sends directly to `api/customers/` are untrusted. The UI performs no validation on its own, so the API-layer check is the line of defense against these untrusted inputs.
2. **Where are authentication, authorization, and validation enforced (UI is not enough)?**\
Validation is enforced in `CustomerService.create()`. Blank IDs and names both get rejected here, and the UI simply displays whatever the API decides. There is still no real authentication or authorization in this lab yet. Thus, anyone with access to the port can create or read any customer.
3. **Which values are sensitive—never in screenshots or surefire dumps?**\
Real customer names, emails, or prod credentials should never appear in a screenshot or Surefire report. Every example in this lab uses the fictional fixtures with fake names and addresses so accidentally capturing one is harmless.
4. **What can be retried safely (idempotent GET; careful POST)?**\
GET is always safe to retry. POST is not safe to retry blindly. If you were to retry a POST with the same ID, the existing record would be silently overwritten rather than rejecting the repeat. This lab has no guard against duplicate-ID creation.
5. **What happens after partial failure (red CI blocks merge; screenshot captured)?**\
A partial failure for integration or UI test will stop `mvn verify` from building successfully. A failed UI assertions, similar to the one in failure experiment 5, would leave behind a `ui-failure.png` as evidence of what the browser saw during failure. This should be removed from the target when generated if necessary (in this lab, it is...).
6. **What would an operator monitor (suite duration, flake rate, 5xx on create)?**\
An operator would monitor the suite duration, as a sudden slow could mean a wait condition regressed to something closer to a sleep. Also, the flake rate across repeated CI runs of the same commit would be useful. Lastly, the 5xx rate on the create endpoint, as a spike there mean something deeper than a validation issue.
7. **Which local default is unacceptable (headed-only CI, committed chromedriver, sleeps)?**\
Unacceptable local defaults include running CI in headed-only mode, committing a ChromeDrive binary into the repo, or relying on `Thread.sleep` to fix flakiness instead of using a wait. All three are failure experiments for this lab and thus should never be practiced.
8. **How are API/UI contracts versioned with DTO field renames?**\
In this lab, renaming a field on `Customer` would silently break the UI's `fetch` body construction and `CustomerApiIT`'s JSON assertions. This is because nothing here is contract-versioned yet. 