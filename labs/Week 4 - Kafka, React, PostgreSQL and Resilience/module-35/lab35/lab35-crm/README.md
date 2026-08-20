# Lab 35 starter — timed path (~45 minutes)

**Theme:** Frontend ↔ API — http client, hooks, CORS notes

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `/` into your bootcamp examples tree as `lab35-crm`.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab35-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab35-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab35-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab35-crm
cp -R starter/. ~/java-bootcamp/examples/lab35-crm/
cd ~/java-bootcamp/examples/lab35-crm
```

## 45-minute checklist

- [ ] cd crm-ui && npm install; copy `.env.example` → `.env`
- [ ] Complete `http.ts` (base URL, AbortSignal, ApiError mapping)
- [ ] Implement `customers.ts` list/get/create against Spring API
- [ ] Wire `useCustomers` + page loading/error states
- [ ] Smoke: list CUS-1001 with backend up; run unit tests

## Smoke test

```bash
cd crm-ui
npm install
npm run test -- --run
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-35/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| ApiError distinguishes network vs 4xx/5xx | Pass / Fail |
| customersApi list uses VITE_API_BASE_URL | Pass / Fail |
| AbortController wired on unmount or navigation | Pass / Fail |
| api-integration-notes.md mentions CORS origin | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

## Manual Verification

| Test                                                   | Result |
|--------------------------------------------------------|--------|
| curl list/create matches SPA types.                    | PASS   |
| SPA lists Amina/Ravi from API (or seeded equivalents). | PASS   |
| Loading/empty/error states are distinct.               | PASS   |
| Unmount/abort does not crash or warn.                  | PASS   |
| Create returns server id; double-click → one POST.     | PASS   |
| Forced 400 maps to Email (or relevant) field.          | PASS   |
| CORS allows `:5173`; evil Origin denied.               | PASS   |
| Correlation header present on writes.                  | PASS   |
| Response-class tests + build green twice.              | PASS   |
| You can explain why secrets never go in `VITE_*`.      | PASS   |

## Security and Production Review

1. **Which inputs are untrusted (all browser payloads; Origin header)?**\
   Every browser payload and the `Origin` header are untrusted. CORS exists because the browser will send requests from any origin unless the server refuses them.
2. **Where are authn/authz/validation enforced (server validation now; auth Lab 36)?**\
   Server-side validation runs on every write regardless of client checks. Authentication will come into play in lab 36.
3. **Which values are sensitive—never in `VITE_*` or repo?**\
   Nothing should ever go in a `VITE_*` variable that isn't meant to be public. Those values are compiled directly into the browser bundle and visible to anyone who opens DevTools.
4. **What can be retried safely (GET list; POST only with idempotency keys later)?**\
   `GET /customers` can be retried any number of times. `POST /customers` only once per user intent, guarded by the `saving` flag.
5. **What happens after partial failure (ApiError UI; no silent empty)?**\
   A failed request becomes a visible `ApiError` UI state. Never a silently empty list.
6. **What would an operator monitor (API 5xx rate, CORS rejects, correlation IDs)?**\
   An operator would monitor the API 5xx rate, the CORS rejection rate, and whether the `X-Correlation-Id` is actually present on writes. A missing corr ID makes incident tracing a lot hard.
7. **Which local default is unacceptable (`*` CORS, secrets in Vite env)?**\
   `allowedOrigins("*")` is the first default that's unacceptable in prod. Also, any secret placed in a `VITE_*` variable.
8. **How are API contracts versioned with DTO changes (shared types + curl snapshots)?**\
   The frontend `Customer` and `CustomerDraft` types and the backend `CustomerRequest` and `CustomerResponse` DTOs need to be kept in sync manually. There is no shared schema source of truth yet.
