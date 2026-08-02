# Lab 13 starter — timed path (~45 minutes)

**Theme:** Contract-first SOAP (XSD + WSDL + samples) — no Java server

## Copy into your workspace

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab13-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab13-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab13-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab13-crm
cp -R starter/. ~/java-bootcamp/examples/lab13-crm/
cd ~/java-bootcamp/examples/lab13-crm
```

Full GUIDE: [`../LAB-13-GUIDE.md`](../LAB-13-GUIDE.md)

## 45-minute checklist

- [ ] Complete `contracts/customer.xsd` TODOs (types + request/response elements)
- [ ] Complete `contracts/CustomerService.wsdl` (3 ops, document/literal, placeholder address)
- [ ] Fill success + fault samples under `samples/`
- [ ] Finish operation-matrix + soap-design-notes TODOs
- [ ] Smoke-validate well-formed XML

## Smoke test

**Windows PowerShell:**

```powershell
Get-ChildItem contracts,samples -Filter *.xml | ForEach-Object { [xml](Get-Content -Raw $_.FullName) | Out-Null; "OK $($_.Name)" }
Get-ChildItem contracts -Filter *.xsd | ForEach-Object { [xml](Get-Content -Raw $_.FullName) | Out-Null; "OK $($_.Name)" }
Get-ChildItem contracts -Filter *.wsdl | ForEach-Object { [xml](Get-Content -Raw $_.FullName) | Out-Null; "OK $($_.Name)" }
```

**macOS / Linux** (if `xmllint` available):

```bash
xmllint --noout contracts/* samples/*
```

Do **not** start a server on port 8080.

## Timed-path Pass criteria

| Criterion                                          | Pass / Fail |
|----------------------------------------------------|-------------|
| XSD + WSDL well-formed; schemaLocation beside WSDL | Pass        |
| Three operations + success/fault samples           | Pass        |
| Docs name CUS-1001 / CUS-1002 / lab-request-001    | Pass        |
| Placeholder URL documented as not live             | Pass        |

Continue remaining GUIDE steps as homework / full path if needed.

## Handoff Checklist

| # | Confirm                                                       | Result |
|---|---------------------------------------------------------------|--------|
| 1 | Namespace URI published (`http://northstar.com/crm/customer`) | Pass   |
| 2 | WSDL location placeholder documented as non-live              | Pass   |
| 3 | Three operations named and described                          | Pass   |
| 4 | Sample success envelopes for CUS-1001 / CUS-1002              | Pass   |
| 5 | Fault examples for not-found and validation                   | Pass   |
| 6 | Correlation ID convention (`lab-request-001`)                 | Pass   |
| 7 | Explicit note: implementation arrives in Lab 24               | Pass   |
| 8 | Well-formedness checked via PowerShell `[xml]`                | Pass   |

## Security and Production Review
1. **Which SOAP inputs are untrusted (body/header fields)?**\
All body fields, such as `fullName`, `email`, `status`, etc. Basically anything the partner sends.
2. **Where will authn/authz/validation be enforced (schema + future WS-Security / service rules)?**\
Auth and validation will be enforced later. The schema enforces the shape now. WS-Security and service-level rules are future work that are documented but not yet built.
3. **Which values are sensitive—keep samples fictional?**\
None should be. Fictional samples stay fictional for a purpose.
4. **What can be retried safely (Get yes; Create only with idempotency design)?**\
`Get` can always be retried safely. `Create` can only be retired safely with an idempotency design. Until that is implemented, `Create` is not safe for retry.
5. **What happens after failure (Fault response; no half-written customer in samples)?**\
A Fault response. No half-created customer implied by any sample.
6. **What would ops monitor later (fault rates, latency)?**\
Fault rate and latency once lab 24 hosts this.
7. **Which local default is unacceptable in production (http:// placeholder, no auth)?**\
The `http://` placeholder with no auth. It is labeled as a placeholder for a reason.
8. **How are contracts versioned (namespace / WSDL version strategy)?**\
The namespace string bump.
