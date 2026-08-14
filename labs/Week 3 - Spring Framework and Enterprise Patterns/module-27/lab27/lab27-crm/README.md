# Lab 27 starter — timed path (~45 minutes)

**Theme:** @Transactional boundaries

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `starter/` into your bootcamp examples tree as `lab27-crm`.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab27-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab27-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab27-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab27-crm
cp -R starter/. ~/java-bootcamp/examples/lab27-crm/
cd ~/java-bootcamp/examples/lab27-crm
```

## 45-minute checklist

- [ ] Add `@Transactional` on `TransferService.transfer` (service boundary)
- [ ] Implement debit/credit + `TransactionLog` write inside the TX
- [ ] Force rollback when destination is `ACC-FORCE-FAIL`
- [ ] Fill ACID table in `docs/acid-notes.md`
- [ ] Smoke: happy MAIN→LOYALTY; failure leaves MAIN unchanged

## Smoke test

```bash
mvn -B test
mvn -B spring-boot:run
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-27/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| Happy transfer updates both balances + log | Pass / Fail |
| Forced fail rolls back (no log row / MAIN unchanged) | Pass / Fail |
| `@Transactional` on service method (not controller) | Pass / Fail |
| ACID notes cite evidence | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

## Manual Verification

| Test                                                                         | Result |
|------------------------------------------------------------------------------|--------|
| Seeds show MAIN 1000 / LOYALTY 100 / Ravi 250 before demos (or known reset). | PASS   |
| Happy transfer MAIN→LOYALTY updates both balances.                           | PASS   |
| TransactionLog stores lab-request-001.                                       | PASS   |
| ACC-FORCE-FAIL returns error and leaves MAIN unchanged.                      | PASS   |
| No success log row for forced failure.                                       | PASS   |
| Insufficient funds leaves destination unchanged.                             | PASS   |
| @Transactional lives on service, not controller.                             | PASS   |
| ACID section cites lab evidence + H2 durability caveat.                      | PASS   |
| Two consecutive mvn test runs match.                                         | PASS   |
| AI review exists if Copilot was used; no secrets in Git.                     | PASS   |

## Security and Production Review

1. **Which inputs are untrusted (amount, account IDs, headers)?**\
The transfer amount, the `X-Correlation-ID` header, `fromAccountId`, and `toAccountId` are all untrusted.
2. **Where are authn/authz/validation enforced (Lab 28 deepens)?**\
Validation is enforced in `TransferService`. Authn and authz are absent in this lab, so anyone who can reach the endpoint can move money between any two accounts. Lab 28 intends to close this gap.
3. **Which values are sensitive (balances), and where stored?**\
Account balances are considered sensitive business data, so they should never be logged in bulk. Only the fictional balances we created belong anywhere in evidence.
4. **What can be retried safely (reads vs transfers)?**\
Reads are always safe to retry. Transfers on the other hand are not. Failure experiment 3 shows a repeat post double-debits the amount instead of no-oping.
5. **What happens after partial failure before commit?**\
Nothing will persist after a partial failure. The check for insufficient funds runs before any `save` call and the `ACC-FORCE-FAIL` throw happens before the destination account is even looked up.
6. **What would an operator monitor (rollback rate, transfer latency)?**\
An operator would want to monitor the rollback rate for spikes, suggesting an upstream or client input problem. Also, the transfer latency. Failure experiment 4 shows why a slow transaction is risky, not just a UX problem.
7. **Which local default is unacceptable in production (blank H2 password, mem DB as ledger)?**\
A blank H2 password and the in-memory H2 database itself are unacceptable in production. These are only okay in labs. A real ledger needs a durable database with actual credentials.
8. **How are schema/API contracts versioned for transfer payloads?**\
As of now, nothing versions the `TransferRequest` shape. Any field rename would break every existing client. A real prod API would need a versioned endpoint path or field policy documented.
