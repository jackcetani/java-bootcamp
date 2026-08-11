# Lab 25 starter — timed path (~45 minutes)

**Theme:** Repository + service layer

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `/` into your bootcamp examples tree as `lab25-crm`.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab25-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab25-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab25-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab25-crm
cp -R starter/. ~/java-bootcamp/examples/lab25-crm/
cd ~/java-bootcamp/examples/lab25-crm
```

## 45-minute checklist

- [ ] Implement `InMemoryCustomerRepository` (seed CUS-1001 / CUS-1002)
- [ ] Put create/get/duplicate rules in `CustomerService` (not controller)
- [ ] Confirm controller has zero repository imports
- [ ] Run smoke + note AI review (or N/A) in docs/lab25-001.md

## Smoke test

```bash
mvn -B test
mvn -B spring-boot:run
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-25/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| GET CUS-1001 and CUS-1002 succeed | Pass / Fail |
| Duplicate create fails in service | Pass / Fail |
| Controller does not import repository types | Pass / Fail |
| `mvn -B test` green (or documented remaining TODOs) | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

## Manual Verification

| Test                                           | Result |
|------------------------------------------------|--------|
| GET CUS-1001 returns Amina ACTIVE.             | PASS   |
| GET CUS-1002 returns Ravi PROSPECT.            | PASS   |
| Activate Ravi PROSPECT→ACTIVE via service/API. | PASS   |
| GET CUS-9999 is explicit not-found.            | PASS   |
| Duplicate create of CUS-1001 fails in service. | PASS   |
| Controller source has no repository imports.   | PASS   |
| Service source has no Spring Web imports.      | PASS   |
| AI review exists if Copilot/Cursor was used.   | PASS   |
| Two consecutive mvn test runs match.           | PASS   |
| JPA readiness note exists; no secrets in Git.  | PASS   |

## Security and Production Review

1. **Which inputs are untrusted (JSON bodies, path IDs)?**\
   All the inputs in the JSON body (`customerId`, `fullName`, `email`, `status`) and the path `customerId` should be considered untrusted. `@Vaild` and `@NotBlank` in `CustomerRequest` are the first layer of defense, and the service layer's exceptions are the second.
2. **Where are authn/authz/validation enforced (Lab 28 deepens auth)?**\
   Bean / shape validation is enforced at the controller boundary, and business validation in the service. Lab 28 explicitly where real authn/authz comes in, not in this lab yet.
3. **Which values are sensitive — never log emails/phones as PII dumps?**\
   Any PII should never appear in logs or error messages. So `fullName` and `email` can never show up, especially when we move from test fixtures to real data. Nothing should appear beyond what's already returned to the caller who legitimately requested that record.
4. **What can be retried safely (GET vs POST create)?**\
   GET is always safe to retry. POST `create` is not safe on the other hand. A retry after a timeout now correctly returns a `409` when there is a duplicate, rather than silently overwriting.
5. **What happens after partial failure (create saved, response lost)?**\
   A record will be saved on the server side for POSTs like `create`. The caller gets no confirmation and now in this lab a duplicate record is rejected rather than creating a second record.
6. **What would an operator monitor (not-found rate, create rate)?**\
   An operator would likely monitor the not-found rate and create-duplicate rate as two separate things. A spike in one would tell a different story than a spike in another.
7. **Which local default is unacceptable in production (in-memory as sole store)?**\
   In production, the in-memory store as the persistence is unacceptable. It is fine for this lab, but once more than one instance is needed or restarts need to preserve state, we'll need to move off that.
8. **How are API contracts versioned when repository becomes JPA?**\
   The API contracts aren't affected by the repo becoming JPA. `CustomerController`'s REST contract depends only on `CustomerService`. Since `CustomerService` only depends on the `CustomerRepository` interface, swapping the interface's implementation won't affect the other two layers.

