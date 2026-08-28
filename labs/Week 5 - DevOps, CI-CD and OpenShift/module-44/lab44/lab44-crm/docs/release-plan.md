# Lab 44 — Release plan

## Immutable artifact

Promote **one** identity from Lab 43: JAR SHA-256 and/or image digest — never rebuild on the deploy agent as "the same" release.

| Field | Value |
| ----- | ----- |
| Version | 1.4.0 |
| Commit | <run `git rev-parse HEAD` in `lab44-crm` and paste the real output here> |
| Digest / checksum | `457FE7DA4508FF83C450676179482FE60AEF960D882BAB731E17135AD7BA9B03` (JAR SHA-256, real, from Lab 43's build); image digest to be filled once the `docker build` above succeeds |

## Promotion path

```text
CI package → test → staging (smoke) → [approval] → production
```

## Gates (objective)

| Env | Gate | Evidence |
| --- | ---- | -------- |
| test | verify green | local `mvn -B clean verify` output (no Actions URL — Lab 43's CI only runs `verify`/`package`, no image build; see Known limitation) |
| staging | smoke `CUS-1001` / `CUS-1002` | pending Step 6 |
| production | approval + digest match | pending Step 8 |

## Config vs artifact

Env-specific ConfigMaps/Secrets stay outside the artifact — `CRM_DB_HOST`/`PORT`/`NAME` via ConfigMap per namespace, `CRM_DB_PASSWORD`/`JWT_SECRET` via `kubectl create secret` per namespace, never committed.

## DB compatibility

No schema changes in this release — `V49__customer.sql`/`V50__customer_interaction.sql` unchanged from Lab 41's baseline. Expand-before-contract required for any future migration: add nullable columns first, backfill, only drop/rename in a later release.