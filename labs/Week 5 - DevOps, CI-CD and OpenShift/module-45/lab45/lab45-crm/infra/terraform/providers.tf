terraform {
  required_version = ">= 1.5.0"
  required_providers {
    null = {
      source  = "hashicorp/null"
      version = "~> 3.2"
    }
  }
  # Remote state narrative (never committed): encrypted S3 backend + DynamoDB
  # lock table, credentials supplied via CI OIDC role — not local files, not Git.
  # backend "s3" { … }
}