# Lab 21 starter — timed path (~45 minutes)

**Theme:** Observability — Actuator probes, CrmReadinessIndicator, Micrometer create/get metrics

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `/` into your bootcamp examples tree as `lab21-crm`.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab21-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab21-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab21-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab21-crm
cp -R starter/. ~/java-bootcamp/examples/lab21-crm/
cd ~/java-bootcamp/examples/lab21-crm
```

Full GUIDE: [`../LAB-21-GUIDE.md`](../LAB-21-GUIDE.md)

## 45-minute checklist

- [ ] Confirm Actuator exposure + probes in `application.yml` (lab-only)
- [ ] Implement `CrmReadinessIndicator` (ready vs OUT_OF_SERVICE)
- [ ] Implement `CustomerMetrics` counters (low-cardinality tags only)
- [ ] Wire metrics calls in `CustomerService`; finish `ActuatorIT`
- [ ] Fill `docs/monitoring-report.md`; run smoke test

## Smoke test

```bash
mvn -B -Dtest=ActuatorIT test
mvn -B clean verify
# with app running:
# curl -s http://localhost:8080/actuator/health/liveness
# curl -s http://localhost:8080/actuator/health/readiness
# curl -s http://localhost:8080/actuator/metrics/crm.customer.create
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-21/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| ActuatorIT green | Pass / Fail |
| Liveness UP when readiness toggled down | Pass / Fail |
| Create/get metrics present after traffic | Pass / Fail |
| monitoring-report.md documents prod exposure caution | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

## Manual Verification
| Test                                                                      | Result |
|---------------------------------------------------------------------------|--------|
| Actuator dependency resolves on the classpath.                            | PASS   |
| /actuator/health returns UP when the app is healthy.                      | PASS   |
| /liveness and /readiness are reachable with probes enabled.               | PASS   |
| Readiness can fail while liveness stays UP (controlled experiment).       | PASS   |
| crm.customer.create appears under /actuator/metrics.                      | PASS   |
| POST CUS-1001 increases create success (or documented failure increment). | PASS   |
| GET increments get latency/success metrics.                               | PASS   |
| Logs still carry lab-request-001 without putting it in metric tags.       | PASS   |
| ActuatorIT passes.                                                        | PASS   |
| Monitoring report forbids unrestricted public Actuator in production.     | PASS   |

## Security and Production Review
1. **Which browser, network, or Actuator inputs are untrusted?**\
   Any request to `actuator/` from outside a local machine is untrusted in real deployment. There is no authentication on any of these endpoints right now, which is acceptable only in a local lab environment.
2. **Where are authn/authz enforced for management endpoints in production?**\
   Authn and authz are not enforced in this lab. This is the point of the production note. A real deployment would put Actuator behind Spring Security or a management-only port not exposed to the public. It would also restrict `exposure.include` to just `health` and maybe `info` publicly.
3. **Which values are sensitive (/env, secrets, PII)—never as metric tags or open Actuator fields?**\
   `customerId`, `fullName`, `email`, and `correlationId` must never appear as metric tags. Also, `env` and `configprops` must never be exposed since they can reveal config values including secrets.
4. **What can be retried safely (GET health/metrics scrapes)?**\
   GET requests to `actuator/health` and `actuator/metrics/` are always safe. Since it is just a read, there are no side effects of multiple, back-to-back calls.
5. **What happens after partial failure (failure counters; readiness drain)?**\
   First, a failure counter will increment `crm.customer.create{result=failure}` or `{result=validation=failed}`. If it is a genuine dependency problem rather than a bad input, readiness can be flipped to signal not to route traffic. The combination of a failure counter and a readiness drain is exactly how an operator would distinguish an instance or request problem.
6. **What would an operator monitor (create failure ratio, readiness flaps)?**\
   They would monitor the `create` failure ratio and readiness flapping transitions. Both of these are what the sample alert sketch in the guide targets.
7. **Which local default is unacceptable (public unrestricted Actuator, lab toggle left on)?**\
   Leaving `actuator` open with no plan to restrict it in production is a clear offender. Similarly, leaving the `lab/readiness/` endpoint deployed anywhere shared would let anyone pull the service out of load balancer rotation whenever they want. This is why cleanup explicitly calls for removing it before any shared deployment.
8. **How are metric names versioned when services rename ops?**\
   If a metric name were to ever change, every dashboard and alert rule would silently stop finding data under the old name. A proper rename needs a transition period where both names are emitted, or it should be documented to whoever owns the dashboards.

