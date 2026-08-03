# Lab 14 starter — timed path (~45 minutes)

**Theme:** DTOs + Jakarta Bean Validation at the API boundary

## Copy into your workspace

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab14-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab14-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab14-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab14-crm
cp -R starter/. ~/java-bootcamp/examples/lab14-crm/
cd ~/java-bootcamp/examples/lab14-crm
```

Full GUIDE: [`../LAB-14-GUIDE.md`](../LAB-14-GUIDE.md)

## 45-minute checklist

- [ ] Add Bean Validation annotations on `CustomerRequestDTO`
- [ ] Implement `CustomerMapper` + `CustomerApiFacade` TODOs
- [ ] Complete validation tests (valid / bad email / blank name)
- [ ] Main demo returns response DTOs only; invalid path shows `lab-request-001`
- [ ] Fill `docs/dto-boundary-notes.md`
- [ ] Run smoke test

## Smoke test

```bash
mvn -B clean test
```

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| Validation tests green | Pass / Fail |
| Facade returns DTOs only (no entity leak) | Pass / Fail |
| Invalid payload rejected before service | Pass / Fail |
| Correlation id visible on failure | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

## Validation rules (CustomerRequestDTO)
| Field | Constraints |
| ----- | ----------- |
| customerId | @NotBlank, @Size(max=32) |
| fullName | @NotBlank, @Size(2..100) |
| email | @NotBlank, @Email, @Size(max=254) |
| status | @NotBlank (ACTIVE\|PROSPECT\|SUSPENDED\|CLOSED) |

## Sample invalid (email)
email=not-an-email -> IllegalArgumentException with field message, 
correlationId=lab-request-001

## Manual Verificaiton
| Step                                                                             | Result |
|----------------------------------------------------------------------------------|--------|
| Create/read workflow succeeds for CUS-1001 and CUS-1002.                         | PASS   |
| Invalid email / blank name / oversized ID rejected at the facade.                | PASS   |
| API returns CustomerResponseDTO, never Customer.                                 | PASS   |
| Correlation lab-request-001 appears on validation/not-found errors.              | PASS   |
| Validation tests pass independently of service tests.                            | PASS   |
| Duplicate create still handled by service rules (distinct from Bean Validation). | PASS   |
| No secrets in logs or Git; target/ ignored.                                      | PASS   |
| README lists constraints and run commands.                                       | PASS   |
| mvn -q clean test succeeds.                                                      | PASS   |
| You can explain why entities stay behind the mapper.                             | PASS   |

## Security and Production Review
1. **Which inputs are untrusted (all DTO fields from clients)?**\
Every DTO field from the client are untrusted.
2. **Where are authn/authz/validation enforced (validation now; auth still absent)?**\
Shape validation is enforced at the facade. Real auth is still absent in this lab.
3. **Which values are sensitive—never put them on response DTOs?**\
None of them belong on response DTOs. This discipline should be held even as fields get added later.
4. **What can be retried safely (getById; create only with idempotency design)?**\
`getById` is safe to retry. `create` is only safe with real idempotency design.
5. **What happens after validation failure (no service call, no partial save)?**\
No service call happens at all. Nothing partial is saved.
6. **What would an operator monitor (validation fail rate, correlation IDs)?**\
An operator would likely monitor validation failure rate and correlation IDs on rejected requests.
7. **Which local default is unacceptable in production (in-memory; exceptions as HTTP 400 mapping TBD)?**\
In production, an in-memory store is unacceptable. Same with exceptions instead of real HTTP 400 mapping, which will be Spring's responsibility later on.
8. **How are contracts versioned (DTO field adds vs breaking renames; Lab 13 WSDL parallel)?**\
Additive DTO fields are safe, however, the renames/removals are breaking and need a version bump.