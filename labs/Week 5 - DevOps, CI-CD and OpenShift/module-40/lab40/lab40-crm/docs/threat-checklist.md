# Lab 40 — Threat checklist (OWASP-aligned)

**Scope:** CRM API serving synthetic fixtures `CUS-1001` / `CUS-1002`.

## Surfaces

| Surface | OWASP theme | Risk note | Status |
| ------- | ----------- | --------- | ------ |
| Customer lookup / list | Broken access control | `GET /api/customers` has zero Spring Security — no authentication, no authorization, confirmed via SAST (`lab40-002`) | Open |
| Search / filter params | Injection | `status`/`page`/`size` query params flow into `CustomerRepository.findByStatus(...)` via Spring Data JPA method-name derivation — parameterized by the framework, no raw string concatenation found | Reviewed — no finding |
| Logs / error bodies | Sensitive data exposure | No PII observed logged directly; `ApiExceptionHandler` returns generic messages, not stack traces | Reviewed — no finding |
| Dependencies | Vulnerable components | Confirmed via real scan: 9 packages/dozens of CVEs (up to CVSS 9.8) on Boot 3.3.5 baseline; remediated via Boot 3.5.16/Tomcat 10.1.57 — down to 3 packages, max CVSS 7.5, zero Criticals (`lab40-001`, `lab40-003/004/005`) | Mitigated, residual risk tracked |
| Secrets in config | Security misconfiguration | `SPRING_DATASOURCE_PASSWORD` set via environment variable only, never in `application.yml` directly; `.env` correctly gitignored | Reviewed — no finding |

## Notes

- Primary confirmed finding: `lab40-001` (Tomcat/Spring CVEs, CVSS up to 9.8) — see `docs/security-findings.csv`.
- Confirmed SAST finding: `lab40-002` (missing authn/authz on customer list endpoint) — real, not hypothetical; verified directly against `CustomerController.java`.
- No NVD API keys, tokens, or real emails appear anywhere in this file or the scan evidence.
- Never paste NVD API keys, tokens, or real emails into this file.
