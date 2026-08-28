#!/usr/bin/env bash
set -euo pipefail
: "${CRM_BASE_URL:?}"
CORR="${CORR:-lab-request-001}"
for id in CUS-1001 CUS-1002; do
  echo "GET $id"
  curl -fsS -H "X-Correlation-Id: ${CORR}" \
    "${CRM_BASE_URL}/api/v1/customers/${id}" >/dev/null
done
curl -fsS -H "X-Correlation-Id: ${CORR}" \
  "${CRM_BASE_URL}/actuator/health/readiness"
echo "smoke ok corr=${CORR}"