variable "environment" {
  type        = string
  description = "dev | staging | prod"
  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "Use an approved environment name: dev, staging, or prod."
  }
}

variable "region" {
  type        = string
  description = "Cloud region"
  default     = "us-east-1"
}

variable "db_password" {
  type        = string
  description = "Sensitive — supply via tfvars locally or secret store; never commit"
  sensitive   = true
  default     = ""
  # No default in a real stack — this default exists only so `validate` succeeds
  # without a real secret in this training exercise.
}