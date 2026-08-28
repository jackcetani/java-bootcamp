# Lab 45 — CRM infra sketch (safe local validate without cloud apply)
# FORBIDDEN: publicly reachable database, hardcoded passwords, open 0.0.0.0/0 SSH.

locals {
  tags = {
    application = "crm"
    environment = var.environment
    managed_by  = "terraform"
  }
}

resource "null_resource" "crm_stack_sketch" {
  triggers = {
    environment = var.environment
    region      = var.region
  }
  # Documentation-only note: any real database resource here must specify
  # a private subnet / no public IP — enforced by human review, not by Terraform alone.
}

output "sketch_note" {
  value = "Sketch validated locally; real DB/network modules require human threat review and an authorized sandbox before apply."
}