# Lab 28 starter — timed path (~45 minutes)

**Theme:** SecurityFilterChain / JWT stubs

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `/` into your bootcamp examples tree as `lab28-crm`.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab28-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab28-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab28-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab28-crm
cp -R starter/. ~/java-bootcamp/examples/lab28-crm/
cd ~/java-bootcamp/examples/lab28-crm
```

## 45-minute checklist

- [ ] Complete `SecurityFilterChain` matchers (public login, AGENT customers, ADMIN admin)
- [ ] Fill `JwtService` issue/parse stubs
- [ ] Wire `JwtAuthenticationFilter` into the chain
- [ ] Prove login → Bearer GET CUS-1001; missing token → 401; agent on admin → 403
- [ ] Note IdP/key-rotation in docs/security-notes.md

## Smoke test

```bash
mvn -B test
mvn -B spring-boot:run
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-28/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| SecurityFilterChain bean present and stateless | Pass / Fail |
| Login endpoint issues a token stub | Pass / Fail |
| 401 vs 403 distinguished in notes or tests | Pass / Fail |
| No real secrets committed | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

## Manual Verification

| Test                                                                     | Result |
|--------------------------------------------------------------------------|--------|
| Login as agent1 returns an access token.                                 | PASS   |
| GET /api/customers/CUS-1001 with Bearer succeeds (Amina / ACTIVE).       | PASS   |
| GET /api/customers/CUS-1002 with AGENT token succeeds (Ravi / PROSPECT). | PASS   |
| Missing or malformed Authorization → 401.                                | PASS   |
| Wrong password on login → 401 without verbose credential hints.          | PASS   |
| AGENT on /api/admin/** → 403; ADMIN → 200.                               | PASS   |
| Correlation lab-request-001 appears in logs without bearer tokens.       | PASS   |
| MockMvc suite covers authenticated and forbidden paths.                  | PASS   |
| Two consecutive mvn test runs match.                                     | PASS   |
| No JWT secret, password, or .env committed.                              | PASS   |

## Security and Production Review

1. Which inputs are untrusted (credentials, Authorization header, customer IDs)?
   The `Authorization` header and the customer ID path var are untrusted. Credentials are verified once at login, but every other request trusts only the JWT signature and expiry.
2. Where are authn/authz enforced (filter chain, method security)?
   Authentication happens in `JwtAuthenticationFilter` and `AuthController` for logins. Authorization happens only in `SecurityConfig` and `AdminController`.
3. Which values are sensitive (JWT secret, passwords, bearer tokens) and where stored?
   Secrets like `JWT_SECRET`, plaintext passwords, and bearer tokens are sensitive. They should never appear in any commited file or logs.
4. What can be retried safely (GET with token; login rate-limits)?
   GET requests are always safe with a valid token. Login itself should be rate-limited at some point, but it is fine for this lab.
5. What happens after partial failure (login succeeded, client lost token)?
   The token is stateless and issued already, so the client simply logs in again to get a new one. Nothing server-side needs to change.
6. What would an operator monitor (failed logins, 401/403 rates)?
   An operator would likely monitor the failed login rate for spikes. They would also look at the 401/403 ratio for sudden jumps.
7. Which local default is unacceptable in production (in-memory users, shared HS256 lab secret)?
   For production, the in memory `UserDetailsService` and a shared HS2456 secret would need to be addressed. Production needs a real secret manager.
8. How are token claim contracts versioned when roles or claim names change?
   As of now, nothing versions the claim shape. So a rename would break every client parsing the current shape.
