# Lab 34 starter — timed path (~45 minutes)

**Theme:** React state & events — lift state, validation, flows

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `/` into your bootcamp examples tree as `lab34-crm`.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab34-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab34-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab34-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab34-crm
cp -R starter/. ~/java-bootcamp/examples/lab34-crm/
cd ~/java-bootcamp/examples/lab34-crm
```

## 45-minute checklist

- [ ] cd crm-ui && npm install
- [ ] Lift customers + mode into App; wire create/edit/cancel handlers
- [ ] Complete `customerValidation` field errors
- [ ] Disable Save while saving; discard draft on cancel
- [ ] Green `App.test.tsx` flow tests + `npm run test -- --run`

## Smoke test

```bash
cd crm-ui
npm install
npm run test -- --run
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-34/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| Create path adds a customer in UI state | Pass / Fail |
| Edit path updates existing by customerId | Pass / Fail |
| Invalid submit shows field errors | Pass / Fail |
| Cancel discards draft | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

## Manual Verification

| Test                                                 | Result |
|------------------------------------------------------|--------|
| Seeds show Amina (`CUS-1001`) and Ravi (`CUS-1002`). | PASS   |
| Search `amina` → one card; clear → two cards.        | PASS   |
| Create valid customer → appears once; mode closes.   | PASS   |
| Invalid submit → field alerts; list unchanged.       | PASS   |
| Edit Ravi → only Ravi changes.                       | PASS   |
| Cancel discards draft; saved rows intact.            | PASS   |
| Title tracks visible count.                          | PASS   |
| No filtered-state effects in `App`.                  | PASS   |
| ≥8 tests green twice; build green.                   | PASS   |
| You can explain why filter is derived during render. | PASS   |

## Security and Production Review

1. **Which inputs are untrusted (all form fields; still client-only)?**\
   All the form fields are considered untrusted. Client-side validation is a UX convenience, not a trust boundary. Nothing here is persisted or sent anywhere yet.
2. **Where are authn/authz/validation enforced (client UX now; API later)?**\
   Only client-side UX validation exists in this lab. Real authn/authz and server-side validation come into play in later labs.
3. **Which values are sensitive—never log real PII beyond fixtures?**\
   Only the fictional seed emails / fixtures should ever appear in logs. Real PII should never be logged or committed, even accidentally through a console log.
4. **What can be retried safely (`npm test` / refresh loses memory—expected)?**\
   `npm test` and `npm run build` can be retried safely and are idempotent. Refreshing the browser on the other hand will lose all in-memory state because nothing is persisted.
5. **What happens after partial failure (validation stop; no half-written row)?**\
   Validation stops to submit entirely before any state update happens. So, there's no possibility of a half written customer in this lab, the blank name / cancel create failure experiments show this.
6. **What would an operator monitor (Lab 35: API errors; here: test suite)?**\
   An operator would likely monitor the test suite and build status in this lab. However, once real API error monitoring is in place, they would probably look to that for more information.
7. **Which local default is unacceptable (in-place mutation, duplicate filtered state)?**\
   The in-place array mutation and duplicate filtered state are both unacceptable local defaults. Failure experiments 1 and 2 show why.
8. **How are contracts versioned with Lab 35 DTO fetch (keep `Customer` shape)?**\
   `Customer` and `CustomerDraft` stay the same shape wise into lab 35. Only the data source (fixture vs. `fetch`) changes, so the API's DTOs need to match this shape, not the other way around.
