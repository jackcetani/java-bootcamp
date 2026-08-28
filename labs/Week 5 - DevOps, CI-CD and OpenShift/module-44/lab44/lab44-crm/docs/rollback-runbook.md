# Lab 44 — Rollback runbook

## Known-good identity

| Field | Value |
| ----- | ----- |
| Previous digest / checksum | `crm-api:1.3.2-simulated-prior` (simulated for this rehearsal — first real release has no genuine prior digest yet) |
| Previous version | 1.3.2 (simulated) |
| Verification check | readiness + `GET /api/v1/customers/CUS-1001` |

## Procedure

1. Confirm current digest ≠ known-good: `kubectl -n crm-staging get deploy crm-api -o jsonpath='{.spec.template.spec.containers[0].image}'`
2. `kubectl -n crm-staging set image deployment/crm-api crm-api=crm-api:1.3.2-simulated-prior`
3. `kubectl -n crm-staging rollout status deployment/crm-api --timeout=180s`
4. Re-run `scripts/smoke-crm.sh` against the rolled-back deployment
5. Record time-to-recover in release notes

## Rehearsal evidence

Real timing and command output captured under `notes/screenshots/lab-44/` — see evidence list below.

## Evidence pack pass criteria

| # | Confirm | Your notes |
| - | ------- | ---------- |
| 1 | artifact-manifest.json filled | Pass |
| 2 | staging digest screenshot / command output | Pass |
| 3 | smoke with CUS-1001, CUS-1002, lab-request-001 | Pass |
| 4 | rollback rehearsal note (time + verifier) | Pass |
| 5 | residual risks with owners | Pass |