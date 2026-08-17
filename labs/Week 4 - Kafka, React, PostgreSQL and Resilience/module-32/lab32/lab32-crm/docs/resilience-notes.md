# Lab 32 — Resilience notes

## Instance name

All three annotations (`@CircuitBreaker`, `@Retry`, `@TimeLimiter`) share the name `accountProfile` because Resilience4j resolves each annotation's configuration by looking up that name under its own `resilience4j.<pattern>.instances.<name>` block in `application.yml`. Using the same name across all three ties them to one coherent policy for one dependency, rather than three unrelated configs that happen to apply to the same method.

## Truthful fallback

`available=false` must never look like a funded, successful account response, because React and any downstream consumer treat the two cases completely differently. A real empty-accounts customer vs. a dependency outage are not the same situation, and collapsing them would hide a real incident behind a normal-looking response.

## Concepts to Discuss

1. **Main flow for an account-enriched customer read**\
   A customer profile read calls `AccountProfileService.find(customerId)`, which calls `AccountClient.fetch`. On success, the summary shows `available=true` and the real accounts list.
2. **Trust boundary: CRM vs remote account service responses**\
   The account service is less trusted. Its 5xx responses, timeouts, and connections failures are expected failure modes, not exceptions. `AccountClient` translates all of those into one known type (`TemporaryAccountException`) so the CRM's resilience config can decide what happens next.
3. **Success vs degraded contracts (`available` flag)**\
   `available=true` means the acocunt dependency answer and the accounts list is trusted. `available=false` means the CRM is showing an empty placeholder. React keys UI banners off this flag, not off whether the accounts list happens to be empty.
4. **Stable identity (`CUS-1001`) across CRM and account stubs**\
   The healthy/degraded response and all the WireMock stubs key off the same `customerId` we've been using. Evidence and demos using `curl` commands will stay consistent using this.
5. **Why GET retries can be safe and write retries are not by default**\
   A GET to `/accounts/{customerId}` is idempotent because a retry can't create a second interaction or double-charge anything. A write retry could create duplicates if the first attempt succeeded but the response was lost. That's why only reads should be retried in this lab.
6. **Local aggressive thresholds vs production tuning**\
   We've kept the thresholds low on purpose so state transitions are visible in this lab. Prod thresholds should come from real SLOs and load test data.
7. **Evidence: WireMock journal, CB events, correlation ID**\
   `requestCount` proves whether a call actually reached the stub and `circuitbreakerevents` proves the state transitions actually happened. `lab-request-001` on the outbound header would tie both back to a single request.
8. **Two CRM instances: independent CB state unless shared — implications**\
   Resilience4j's in-memory circuit breaker is per-JVM. Two CRM instances behind a load balancer would each independently decide open or closed. One instance could fail fast while the other is still retrying against the same degraded dependency.
9. **Ordering of Resilience4j aspects with `CompletableFuture` / TimeLimiter**\
   The proxy order of CircuitBreak, Retry, and TimeLimiter aren't hardcoded in this lab. Behavior was verified empirically via WireMock request counts.
10. **What must never appear in a fallback (false write success)**\
    `AccountSumarry.unavailable` is only ever used on the read path. A write endpoint must never share this fallback pattern because returning a success-shaped response for a failed mutation would tell an agent that a change was saved when it actually wasn't.

## Response field meaning (Step 9)

| CRM response field | Meaning |
| ------------------ | ------- |
| `available: true` | Account dependency succeeded; accounts list trustworthy |
| `available: false` | Degraded; show banner; do **not** invent balances |
| Correlation | `lab-request-001` (or request header) in CRM + outbound logs |

**Warning:** the CB windows and 10s open-wait in this lab are for classroom visibility only — production values come from real SLOs and load tests, not these numbers.