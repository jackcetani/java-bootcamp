#!/usr/bin/env bash
set -euo pipefail
TAG="${1:?tag required}"
: "${CRM_REGISTRY_USER:?}"
: "${CRM_REGISTRY_TOKEN:?}"
echo "Would deploy artifact for tag=${TAG} commit=${GITHUB_SHA:-local}"
# Consume CI artifacts / digest — do NOT mvn package here. Real deploy logic is Lab 44's job.