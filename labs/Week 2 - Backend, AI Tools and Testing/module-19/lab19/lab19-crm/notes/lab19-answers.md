## Lab 19 Answers

### Reflection Questions
1. **Which design decision most affected correctness (Page Object vs inline locators)?**\
The most important design decision was putting every locator in `CustomerFormPage` instead of writing them inline in `CustomerUiIT`. When the starter's real `data-testid` values turned out to differ from the guide's snippet, having to fix only one file instead of hunting through test methods mattered a lot.
2. **Which failure was hardest to diagnose (driver mismatch, wait timeout, API JSON)?**\
The hardest failure to diagnose would be a missing `.clear()` call on the `status` field. Since the HTML ships `value="PROSPECT"` in the input, a `.sendKeys()` without a `.clear()` would silently produce `"PROSPECTACTIVE"`instead of just `"ACTIVE"`. The resulting failure would look like a validation error when really it is a test-authoring issue.
3. **What evidence proves the implementation works?**\
The three `CustomerApiTT` (and correlation-header echo) and two `CustomerUiIT` tests showing the suite is green is the main evidence the implementation works. Also, the captured `ui-failure.png` proves the suite can actually fail and report why, which is just as important as passing.
4. **What breaks first at ten times the suite size (shared browser session, shared data store)?**\
Shared application context state between test classes breaks first at ten times the suite size. With more IT/UI classes all potentially sharing one `InMemoryCustomerRepository` singleton, fixture collisions become much more likely.
5. **Which concern should move to shared CI infrastructure (browser image, WebDriver cache)?**\
First, a pre-built browser image with Chrome and a matching ChromeDriver already resolved would be ideal, so the WebDriverManager doesn't hit the network on every CI run. Also, a shared WebDriverManager cache directory would save time without changing any test code.
6. **What must change before real customer data is used in UI tests (spoiler: don’t)?**\
Nothing should change. UI and integration tests should NEVER use real customer data. Only the fixtures (CUS-1001/1002) should be used for reads, and fresh IDs for writes.
7. **How does this lab connect to Labs 17–18 and Labs 20–21?**\
Labs 17 and 18 proved the service and validator logic in isolation. This lab, hwoever, proves that same behavior works over real HTTP and a browser. Lab 20 will add structured logging keyed off the exact same `lab-request-001` corr ID this lab already threads through every request. Lab 21's actuator will gate the UI suite on an actual readiness check instead of just assuming the app is running.
8. **What metric or UI state matters most on the CI dashboard?**\
The metric that matters most on the CI dashboard would be the suite duration and flake rate trends over time. A UI suite that passes only sometimes is far worse than one that fails reliably. 
9. **(Forward look) How will structured correlation logs (Lab 20) help debug a red UI run?**\
In this lab, a failing UI test only tells you the browser-visible symptom. Once structured logs are implemented, a failure can be traced through to the exact server-side log line that explains why the API responded the way it did, instead of being left to guess with only partial information.