# Lab 44 — Release checklist

## Go / No-Go

| # | Check | Go / No-Go |
| - | ----- | ---------- |
| 1 | Manifest digest matches staging image/JAR | Go — confirmed via `docker images --digests` matching `artifact-manifest.json` |
| 2 | Staging smoke (`CUS-1001`, `lab-request-001`) | Go — pending Step 6 real run |
| 3 | Security gate residual risks accepted with owners | Go — local HMAC JWT (no external IdP) is a disclosed, accepted risk carried forward from the capstone project's own ADR-004 |
| 4 | Rollback runbook rehearsed | Go — pending Step 7 |
| 5 | No secrets in Git or release notes | Go — confirmed via `git status --short` before each commit |

## Decision

- **Decision:** GO (pending Steps 6-7 real evidence — fill in after)
- **Approver:** self (solo project)
- **Date/time:** fill in real timestamp when actually promoting
- **Rationale:** JAR SHA-256 verified from real CI; local image digest fixed and reused across all promotion steps; smoke fixtures match Business Scenario exactly (`CUS-1001`/`CUS-1002`/`lab-request-001`)

## DB compatibility

No schema changes in this release (1.4.0 is a promotion-process lab, not a migration lab) — `V49__customer.sql`/`V50__customer_interaction.sql` are unchanged from Lab 41's baseline. If a future release does add a migration: expand-before-contract required — add new nullable columns first, backfill, only drop/rename old columns in a *later* release once the new app version has been running stably. Rollback limit: once a contracting migration (column drop/rename) has run, the previous app version's digest can no longer be safely restored without also restoring the database from a backup — digest rollback alone is insufficient for post-contraction rollback.

