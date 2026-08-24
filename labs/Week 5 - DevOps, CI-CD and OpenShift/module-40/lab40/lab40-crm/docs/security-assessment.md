# Lab 40 — Security assessment

**App:** Northstar CRM (`lab40-crm`)
**Fixtures:** `CUS-1001`, `CUS-1002`, correlation `lab-request-001`
**Repo:** `module-40/lab40/lab40-crm`
**Database:** `crm_lab40` on `lab39-postgres` (port 5433)
**Scan command:** `mvn -B -Psecurity-scan dependency-check:check -DnvdApiKey=$env:NVD_API_KEY -DdataDirectory=$pwd\dependency-check-data` (plugin **10.0.4**)

## Summary

Ran OWASP Dependency-Check against the Lab 39 baseline (Spring Boot 3.3.5) and confirmed real, severe findings — most critically CVE-2026-43512 (CVSS 9.8) on `tomcat-embed-core:10.1.31`, alongside multiple 9.x-range CVEs across `spring-boot`, `spring-core`, and `spring-web`. Remediated with the smallest verified fix: Spring Boot parent bumped to 3.5.16, with `tomcat.version` pinned to 10.1.57. Re-scan confirmed the fix: every Critical-range finding is gone, and the failing package count dropped from 9 to 3, with no CVE above 7.5 remaining. Separately, manual SAST confirmed a real, still-open authorization gap (`GET /api/customers` has no Spring Security at all), documented as a residual risk rather than fixed, since adding authentication is explicitly out of this lab's scope.

## Before / after

| Item | Before (Boot 3.3.5) | After (Boot 3.5.16 / Tomcat 10.1.57) |
| ---- | -------------------- | -------------------------------------- |
| Failing packages (≥ CVSS 7) | 9 (`angus-activation`, `hibernate-validator`, `jackson-databind`, `log4j-api`, `postgresql`, `spring-boot`, `spring-boot-starter-web`, `spring-core`, `spring-web`, `tomcat-embed-core`) | 3 (`angus-activation`, `log4j-api`, `tomcat-embed-core`) |
| Highest CVSS | 9.8 (multiple: `tomcat-embed-core`, `spring-boot`, `spring-core`) | 7.5 (no Criticals remain) |
| `spring-boot`/`spring-core`/`spring-web` findings | Multiple 9.8-range CVEs | **None — fully clean** |
| Build result | `BUILD FAILURE` | `BUILD FAILURE` (3 lower-severity, accepted findings — see residual risks) |
| Suppressions | 0 | 0 — no suppressions used; every remaining finding is explicitly accepted with owner/expiry instead |

## Residual risks

| Risk | Severity | Owner | Expiry | Acceptance |
| ---- | -------- | ----- | ------ | ---------- |
| CVE-2026-66299 on `tomcat-embed-core:10.1.57` (`lab40-003`) | High (7.5) | student | 2026-10-15 | accepted — already on the latest available Tomcat patch; no newer version exists to upgrade to |
| Log4j-api transitive CVEs (`lab40-004`) | High (7.5) | student | 2026-10-15 | accepted — version pinned by Boot 3.5.16's own dependency management; not directly controlled |
| angus-activation transitive CVE (`lab40-005`) | High (7.5) | student | 2026-10-15 | accepted — transitive via Hibernate/JAXB; not directly declared |
| No authn/authz on customer list endpoint (`lab40-002`) | High | student | 2026-10-01 | accepted — explicitly deferred to Lab 41 |

## Evidence paths

- Reports: `target\dependency-check-report.html` / `.json` (sanitized excerpts in `notes\screenshots\`)
- CSV: `docs\security-findings.csv`