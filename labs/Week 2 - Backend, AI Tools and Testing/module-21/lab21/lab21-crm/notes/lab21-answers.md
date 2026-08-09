# Lab 21 Answers

## Reflection Questions
1. **Which design decision most affected correctness (readiness group vs single health blob)?**\
The most crucial design decision was using Spring Boot's built-in liveness/readiness probe groups rather than trying to create a single health blob. `CrmReadinessIndicator` affects only readiness while liveness stays independently computed.
2. **Which failure was hardest to diagnose?**\
The hardest failure to diagnose was realizing the blank name validation rejection path never touched `CustomerMetrics` in the starter for this lab. The counter stayed the same no matter how many bad requests were sent.
3. **What evidence proves create traffic is observable?**\
A before/after metric JSON pair around a real post, along with the same request's `corr=lab-request-001` line from the console prove the metric moved because of the specific traffic, not just a counter existing somewhere else.
4. **What breaks first at ten times the scrape rate or traffic?**\
Any high-cardinality tag becomes catastrophic at 10 times the traffic/scrape rate. At low traffic, a `customerId` tag only creates a few extra time series, but at real production volume, it would create thousands. This is what failure experiment 5 is trying to teach.
5. **Which concern should move to shared infrastructure (Prometheus, alertmanager, auth gateway)?**\
First, the actual Prometheus scrape target config and Alertmanager routing rules. Also, an actual authn gateway in front of `actuaor/` must be implemented. None of these belong hand-rolled per service and exactly the kind of stuff a shared observability stack should own once.
6. **What must change before real customer data appears in telemetry (still no PII tags)?**\
Nothing should change, PII should never be tagged, EVER. It only gets more important as real telemetry data often lives in systems with much looser access control than the app's own database.
7. **How does this lab connect to Labs 19–20 and Lab 22?**\
Lab 19 built the endpoints and Lab 20 made individual requests traceable via correlation IDs in logs. This lab adds the aggregate view logs alone can't give. Lab 22 will then replace the remaining manual `new` wiring elsewhere in the app with full Spring DI.
8. **What metric matters most on the ops dashboard for CRM create?**\
The `create` failure ratio is the metric that matters most. The single number will answer if `create` is currently healthy faster than reading through individual logs lines to figure it out.
9. **(Forward look) How should constructor DI (Lab 22) change how CustomerMetrics is wired?**\
It shouldn't change. `CustomerMetrics` is already receives via constructor injection in both `CustomerService` and `CustomerController` after this lab. This is exactly the pattern Lab 22 wants and this lab is proof it was already correct going in.

