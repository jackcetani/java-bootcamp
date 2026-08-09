# Lab 21 — monitoring report

## Probes

| Probe     | Expected when ready | Expected when lab toggle down |
|-----------|---------------------|-------------------------------|
| liveness  | UP                  | UP                            |
| readiness | UP                  | OUT_OF_SERVICE / DOWN         |

A live-but-not-ready app (e.g. still running a schema migration on startup) should show
`liveness=UP` / `readiness=DOWN`. The process is healthy and shouldn't be restarted, it
just isn't safe to send traffic to yet. A dead process shows `liveness=DOWN`, which
should trigger a restart. Confusing these two causes real incidents: restart-thrashing a
process that just needs more warm-up time, or leaving a broken process in the load
balancer rotation because nobody checked liveness separately.

## Metrics

- `crm.customer.create` tag `result` (`success`/`failure`/`validation_failed`)
- `crm.customer.get` tag `result` (`success`/`not_found`)
- `crm.customer.get.latency` — timer, no tags beyond the metric name itself
- Never tag `customerId` or correlation id

## Production note

Lab exposure of health+metrics+info is **not** production-safe. **Must** restrict endpoints later
(see Security and Production Review in README).

## Concepts to Discuss
1. **Main flow: traffic → service → metrics registry → Actuator scrape**\
   HTTP traffic --> `CustomerController` --> `CustomerService` --> `CustomerMetrics` --> `/actuator/metrics` scrapes what's registered
2. **Trust boundary: which Actuator endpoints are sensitive**\
   `health` and `metrics` are relatively safe to expose, since they're aggregate and contain no secrets. `env` and `configprops` on the other hand can leak config values and secrets. Thus, they must never be open on a public network.
3. **Success/failure contracts: UP vs OUT_OF_SERVICE vs DOWN**\
   UP means fully healthy and ready for traffic. OUT_OF_SERVICE means no accepting traffic (intentionally, like with the readiness toggle). DOWN means something is actually broken and needs to be fixed.
4. **Stable aggregate tags (operation, result) vs high-cardinality IDs**\
   Operations like `create` and `get`, as well as results like `success` and `failure` are fixed, small sets. Thus, they are safe tags. `customerId` on the other hand has unbounded cardinality and grows with every customer created. Thus, it would create a brand new time series per customer in a real metrics backend.
5. **Idempotent GET vs create counter growth semantics**\
   GET can be called as many times as it wants and only ever increments `crm.customer.get{result=success}`. It never changes what exists. Create will increment `crm.customer.create{result=success}` every time. It only counts the number of attempts, even with a violation like duplicate-ID creation.
6. **Local exposure vs production allow-list / auth**\
   Locally, we can expose `health`, `metrics`, and `info` with no authn so we can curl freely. In production, we need an allow-list, with everything else behind auth or a separate management port/firewall rule.
7. **Evidence: before/after metric JSON + log correlation**\
   A before/after metric JSON pair tied to the same `corr-lab-request-001` is a combination that proves the metric is wired to real traffic, not just registered and never touched.
8. **Two instances: each has its own counters; LB uses readiness**\
   Metrics are per-instance in Micrometer's local registry. Two app instances behind a load balancer have completely separate counts unless something aggregates them. The load balancer itself only cares about each instance's readiness state.
9. **Why readiness down should not always restart the process**\
   If a dependecy is temporarily unavailable, restarting the CRM process would do nothing to fix that. Readiness-down should instead pull the instance out of rotation and let it recover or wait, not trigger a restart.
10. **What Lab 22 changes (DI wiring) without renaming metric names**\
    Lab 22 replaces remaining manual `new` wiring with full Spring dependency injection across the CRM bean graph. `CustomerMetrics` is already constructor-injected in this lab, so Lab 22 shouldn't need to touch metric names or tag keys at all.


## Evidence (paste after smoke test)

```text
GET /actuator/health           → {"status":"UP","components":{"crmReadinessIndicator":{"status":"UP"},"diskSpace":{"status":"UP","details":{"total":1003429556224,"free":85856514048,"threshold":10485760,"path":"C:\\Users\\jackc\\....","exists":true}},"livenessState":{"status":"UP"},"ping":{"status":"UP"},"readinessState":{"status":"UP"}},"groups":["liveness","readiness"]}
GET /actuator/health/liveness  → {"status":"UP"}
GET /actuator/health/readiness → {"status":"UP","components":{"crmReadinessIndicator":{"status":"UP"},"readinessState":{"status":"UP"}}}
GET /actuator/metrics/crm.customer.create → {"name":"crm.customer.create","measurements":[{"statistic":"COUNT","value":1.0}],"availableTags":[{"tag":"result","values":["success"]},{"tag":"application","values":["northstar-crm"]}]}
GET /actuator/metrics/crm.customer.get.latency → {"name":"crm.customer.get.latency","baseUnit":"seconds","measurements":[{"statistic":"COUNT","value":1.0},{"statistic":"TOTAL_TIME","value":4.418E-4},{"statistic":"MAX","value":4.418E-4}],"availableTags":[{"tag":"application","values":["northstar-crm"]}]}
```

