# Lab 45 — AI IaC review record

## Prompts used (summarized)

## Prompts used (summarized)

**lab45-001** — Generate Terraform for a non-prod Kubernetes namespace `crm-${environment}`
with labels `application=crm`. No public LoadBalancer DB. No plaintext secrets.
Pin provider versions. List assumptions. Include a human review checklist.

## AI suggestions accepted

| Item | Why accepted |
| ---- | ------------ |
| `null_resource.crm_stack_sketch` with `environment`/`region` triggers | Matches contract; validates without cloud credentials; safe placeholder for a future real module |
| `variable "environment"` validation block restricting to `dev`/`staging`/`prod` | Directly enforces the contract's environment constraint at the Terraform layer, not just documentation |

## AI suggestions rejected or hardened

| Item | Risk | Human change |
| ---- | ---- | ------------ |
| `kubernetes_service_v1.crm_db_lb` with `type = "LoadBalancer"` on port 5432 | Publicly exposes a database port — directly violates the contract's forbidden list | Rejected outright; never implemented in `main.tf`; documented here only as evidence of the review catching it |

## Validation evidence

- `terraform fmt` / `init -backend=false` / `validate`: PASS — see `notes/screenshots/lab-45/terraform-validate.png`
- Ansible `--syntax-check`: PASS — see `notes/screenshots/lab-45/ansible-syntax-check.png`
- Plan read: N/A for this `null_resource` sketch, per guide's own note that plan is optional here

## Residual risks

- No `ansible-lint` in this training image (if applicable) — owner: self, revisit before any real target host
- No live idempotence rehearsal (no authorized disposable host) — owner: self, revisit when a real sandbox is available
- No customer PII anywhere in this IaC — confirmed by inspection, `CUS-1001`/`CUS-1002` never appear in any `.tf`/`.yml` file