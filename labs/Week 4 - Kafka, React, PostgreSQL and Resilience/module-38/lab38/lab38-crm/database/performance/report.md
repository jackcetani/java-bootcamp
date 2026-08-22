# Lab 38 — Performance report

| Experiment | Plan hash / notes                                                                | Buffers | Median time | Write cost |
| ---------- |----------------------------------------------------------------------------------| ------- | ----------- | ---------- |
| lab38-001 baseline email | Index Scan (Lab 37's `uk_customer_email` already indexed this, never a Seq Scan) | 3 | 0.10 ms | — |
| lab38-002 after email index | `ux_customer_email_norm` — same plan/cost as baseline, redundant                 | 3 | ~0.05-0.10 ms | extra index to maintain on writes |
| lab38-003 OFFSET deep page | Index Scan, OFFSET 5000. Scans/discards 5000 rows first                          | 5047 | 3.29 ms | — |
| lab38-004 keyset page | Index Scan, row-value predicate. Jumps straight to position                      | 23 | 0.13 ms | — |

## Why keyset beats deep OFFSET

OFFSET 5000 still touches all 5000 skipped rows (5047 buffers). Keyset uses the index condition to skip straight there (23 buffers). Over 200x fewer buffers for the same page depth.