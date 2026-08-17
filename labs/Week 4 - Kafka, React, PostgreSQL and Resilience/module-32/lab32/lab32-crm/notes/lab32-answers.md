# Lab 32 Answers

## Reflection Questions

1. **Which design decision most affected correctness (fallback honesty vs fail-hard)?**\
The most crucial design decision in this lab was choosing the `available=false` fallback over failing the whole request. It lets the CRM stay usable during a service outage instead of taking down an unrelated read. 
2. **Which failure was hardest (aspect order, TimeLimiter + Future, CB thresholds)?**\
The hardest failure to diagnose was getting `TimeLimiter` to actually apply. It silently does nothing on a synchronous method, so the fix isn't obvious in a stack trace. 
3. **What evidence proves OPEN fail-fast without calling WireMock?**\
`openCircuit_failsFastWithoutHittingStub` compares the WireMock request count before and after the probe call. If the breaker was open, that count wouldn't move, and the response also comes back in under 1500ms.
4. **What breaks first at ten times the outbound call rate?**\
The account service itself would break first at ten times the outbound call rate, with the breaker tripping open often. Also, the sliding window would start alternating between open/closed under real traffic, it likely wouldn't give a stable signal like in this lab.
5. **Which concern should move to shared infrastructure (mesh timeouts, platform CB)?**\
The circuit break state should move to shared infrastructure first. Right now, it's per-JVM, so two instances behind a load balancer make independent open/closed decisions about the same failing dependency. Should be shared.
6. **What must change before real customer/account data is used?**\
The outbound call needs real authentication before real customer/account data can be used. Right now it only carries a correlation header and no credentials. Also, the labs aggressive thresholds need replaced.
7. **How does this lab connect to Labs 29 (errors), 30–31 (async), and the React UX?**\
This lab reuses Lab 29's error contract philosophy and Lab 30-31's async `CompletableFuture` patterns are exactly what `TimeLimiter` needs to work cleanly. 
8. **What metric or Actuator field matters most during an account outage?**\
The metric that matters most is the circuit breaker itself, or `actuator/circuitbreakerevents`. It's the one signal that tells an operator whether the CRM is protecting itself or if it's still working on a downed dependency.
9. **(Forward look) How would a bulkhead change thread isolation for this dependency?**\
A bulkhead would cap the concurrent calls to the account service. This is so a slow or hanging dependency can't exhaust the shared execution and starve unrelated requests. 

