# Lab 24 starter — timed path (~45 minutes)

**Theme:** Spring-WS endpoint / payload

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `/` into your bootcamp examples tree as `lab24-crm`.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab24-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab24-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab24-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab24-crm
cp -R starter/. ~/java-bootcamp/examples/lab24-crm/
cd ~/java-bootcamp/examples/lab24-crm
```

## 45-minute checklist

- [ ] Confirm Spring-WS dependency in `pom.xml`
- [ ] Complete `WebServiceConfig` (MessageDispatcherServlet + WSDL bean)
- [ ] Implement `@Endpoint` / `@PayloadRoot` getCustomer in `CustomerEndpoint`
- [ ] Map SOAP payload ↔ domain in `CustomerSoapMapper` (TODO methods)
- [ ] Smoke: WSDL loads; secured/unsecured get notes in docs

## Smoke test

```bash
mvn -B test
mvn -B spring-boot:run
# WSDL: http://localhost:8080/ws/customers.wsdl
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-24/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| WSDL reachable (or config compiles) | Pass / Fail |
| `@PayloadRoot` getCustomer delegates to CustomerService | Pass / Fail |
| REST `/api/customers` still works for CUS-1001 | Pass / Fail |
| Sample XML under requests/ reviewed | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

## Manual Verification

| Test                                                               | Result |
|--------------------------------------------------------------------|--------|
| WSDL served and lists all four operations.                         | PASS   |
| Secured getCustomer for CUS-1001 returns Amina / ACTIVE.           | PASS   |
| createCustomer returns a customer payload with new or assigned id. | PASS   |
| updateCustomerStatus change visible via REST too.                  | PASS   |
| CUS-9999 yields CLIENT not-found fault (not a stack dump).         | PASS   |
| Request without UsernameToken is rejected before business logic.   | PASS   |
| Correlation lab24-001 appears in logs where instrumented.          | PASS   |
| Two consecutive mvn test runs match.                               | PASS   |
| UsernameToken secret documented as lab-only.                       | PASS   |
| No real partner passwords or target/ in Git.                       | PASS   |

## Security and Production Review
1. **Which SOAP fields are untrusted and where validated?**\
   Every request field should be considered untrusted. They are extracted by the generated JAXB accessors and passed to `CustomerService`. The element presence is enforced by the XSD/JAXB binding itself, which is labs form of validation for a required element that is missing.
2. **Is UsernameToken enough without HTTPS?**\
   No, PasswordText sends the password in a recoverable form. Without TLS, anyone that can observe traffic would be able to read the password. Message and transport security require different needs, but both are required in a prod environment.
3. **Is plaintext PasswordText acceptable outside the lab? What replaces it?**\
   No, production should use PasswordDigest instead. Also, TLS and rotated credentials should be implemented, not just have one static shared secret in production.
4. **Which operations are safe to retry?**\
   `getCustomer` and `listCustomers` are always safe to retry. `createCustomer` is not as per failure experiment 4. `updateCustomerStatus` is safe to retry ONLY if the target status is idempotent.
5. **What happens if create succeeds but response is lost in transit?**\
   The record is saved, but the partner gets no confirmation. A retry would risk a second distinct record rather than a safe no-op.
6. **What should operators monitor (fault rates, WSS rejects, latency)?**\
   Operators would monitor the fault rate by code, for example fault rates for the client vs. fault rates for the server. They would also look at WS-Security rejection rate and endpoint latency.
7. **Which local defaults are unacceptable in prod (in-memory, plaintext secret, clear HTTP)?**\
   In-memory storage is an unacceptable local default in prod. Also, the plaintext `lab24-shared-secret` should not be used in a prod environment, having a static shared secret is a huge offender. Lastly, plain HTTP vs. HTTPS is also unacceptable.
8. **How do you version customer.xsd without breaking the partner?**\
   Additive changes should be safe in general and won't break the partner. However, anything else would need a new namespace version or a coordinated cutover.
