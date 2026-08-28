# Lab 46 — Kafka dashboard notes

## Signals

| Signal | Why it matters | Alert sketch |
| ------ | -------------- | ------------ |
| Consumer lag | Partition stuck / slow handler | Lag > 1000 messages for 5m → warning; lag > 10000 → critical |
| DLT message rate | Poison / contract break | Any DLT publish rate > 0 for 2m → critical, page + open `docs/dlt-replay-runbook.md` |
| Retry count | Transient vs permanent | Retry count climbing without a matching drop in lag → investigate before it exhausts into DLT |
| Processing latency | SLA risk | p95 handler latency > 2s → warning |

## False confidence

Lag = 0 while DLT is growing still means customer events are failing — a lag-only dashboard would show green while real messages are silently dropping into the dead-letter topic. Both signals must be watched together, not lag alone.

## Fixtures

Synthetic only: `CUS-1001`, `CUS-1002`, correlation `lab-request-001`. Redact emails from metric tags.