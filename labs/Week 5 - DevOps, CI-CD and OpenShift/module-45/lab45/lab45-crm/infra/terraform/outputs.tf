output "environment" {
  value = var.environment
}

output "region" {
  value = var.region
}

# No secret-bearing outputs — only environment/region are surfaced.