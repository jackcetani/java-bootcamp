# Lab 43 — CI runbook

## Pipeline policy

| Trigger | Jobs | Notes |
| ------- | ---- | ----- |
| Pull request | verify | Fast feedback; no package job runs |
| `main` push | verify + package | Immutable JAR + SHA-256 checksum |
| Tag `v*` | verify + package | Release candidate identity; deploy stays manual (Lab 44) |

Deploy authority: no one deploys from this workflow — Lab 44's separate `cd.yml` (`workflow_dispatch` + GitHub Environments) owns actual deployment, gated by manual approval.

Retention: Surefire/Failsafe reports upload with `if: always()` — kept even when verify fails, so a red build still leaves triageable evidence.

## Secrets / variables

GitHub Environment secrets (names only — never values):
- `NVD_API_KEY` — secured, used only by the optional Dependency-Check step
- `CRM_REGISTRY_USER`, `CRM_REGISTRY_TOKEN` — secured, scoped to the `test` deployment environment, consumed by Lab 44's `cd.yml` only (not this workflow)

No plaintext credentials appear anywhere in `.github/workflows/ci.yml` or `scripts/`.
## Re-run failed verify

1. Open Actions → failed run
2. TODO(lab43): Document when to re-run vs fix locally
3. Confirm Surefire artifact uploaded (`if: always()`)

## Failure experiment (safe)

TODO(lab43): Force one failing test → observe red check → restore → green.

## Artifact identity for Lab 44

- JAR + `SHA256SUMS` + `GITHUB_SHA`
- TODO(lab43): Where peers download the artifact
