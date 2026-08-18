# Lab 33 starter — timed path (~45 minutes)

**Theme:** React components — StatusBadge, CustomerCard, list

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `/` into your bootcamp examples tree as `lab33-crm`.

Starter includes `crm-ui/` Vite React-TS scaffold.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab33-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab33-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab33-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab33-crm
cp -R starter/. ~/java-bootcamp/examples/lab33-crm/
cd ~/java-bootcamp/examples/lab33-crm
```

## 45-minute checklist

- [ ] cd crm-ui && npm install
- [ ] Complete `Customer` types + seed fixtures CUS-1001 / CUS-1002
- [ ] Implement StatusBadge, CustomerCard, CustomerList (key=customerId)
- [ ] Wire App with list + empty/loading shells
- [ ] Run `npm run test -- --run` and `npm run build`

## Smoke test

```bash
cd crm-ui
npm install
npm run test -- --run
npm run build
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-33/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| Dashboard shows Amina and Ravi | Pass / Fail |
| List keys use customerId (not index) | Pass / Fail |
| RTL test queries by role | Pass / Fail |
| build succeeds | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

## Manual Verification

| Test                                                                       | Result |
|----------------------------------------------------------------------------|--------|
| Vite app titled Customer Management Platform loads at :5173.               | PASS   |
| Amina (CUS-1001) and Ravi (CUS-1002) cards render with text status labels. | PASS   |
| Empty list shows “No customers yet” (force customers={[]} briefly).        | PASS   |
| Form fields found by getByLabelText; errors use role="alert".              | PASS   |
| Exactly one main landmark; usable at 375px width.                          | PASS   |
| Edit on Amina invokes callback with "CUS-1001".                            | PASS   |
| RTL suite ≥3 tests green twice consecutively.                              | PASS   |
| npm run build succeeds.                                                    | PASS   |
| No secrets or node_modules staged.                                         | PASS   |
| You can explain why customerId is the React key.                           | PASS   |

## Security and Production Review

1. **Which inputs are untrusted (browser DOM; fixtures only this lab)?**\
   The browser DOM and anything coming back from the API (once a real fetch is added) are untrusted. This lab has no network input at all, only the fixed `seedCustomers` array and whatever a user types into the form.
2. **Where are authn/authz/validation enforced (not yet—Lab 35–36; forms presentational)?**\
   None are enforced in this lab yet. This lab is presentation-only. Validation comes in during lab 34 and API auth/authz later.
3. **Which values are sensitive—never commit real emails/phones beyond samples?**\
   Only the fictional emails and phone numbers like `amina@example.com` and `+1-555-0101` should be used. Real customer PII must never be used in fixtures or screenshots.
4. **What can be retried safely (`npm test` / `npm run build`)?**\
   `npm test` and `npm run build` can be retried safely. Both are pure and side effect free commands that are safe to run any number of times.
5. **What happens after partial failure (red build blocks merge)?**\
   A red build or failing test blocks merge outright. There's no partial-success state in this lab since nothing here talks to a real backend.
6. **What would an operator/lead monitor (CI test + build; a11y regressions)?**\
   An operator would monitor the CI test and build status, as well as any accessibility regression in landmark structure or label association. They are two hard gates this lab enforces.
7. **Which local default is unacceptable (index keys, color-only status, secrets in repo)?**\
   Index-based list keys, color-only status badges, and any commited secret to `node_modules` or `dist` are all unacceptable in prod. They are not just style preferences but actual failures.
8. **How are UI contracts versioned with DTO changes (shared types; Lab 35 aligns)?**\
   `Customer` and `CustomerDraft` are the single shared source of truth for this UI. Lab 35 will align these types directly against the real DTOs rather than letting the two drift.
