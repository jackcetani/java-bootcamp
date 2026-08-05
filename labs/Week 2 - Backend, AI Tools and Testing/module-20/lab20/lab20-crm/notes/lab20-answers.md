## Lab 20 Answers

### Reflection Questions
1. **Which design decision most affected correctness (filter-owned MDC vs service-owned)?**\
   The most important design decision was splitting MDC ownership between the filter and the service. The service owns the full responsibility of `MDC.clear()`, and the service only ever uses `MDC.remove()` on its own two keys. Getting this wrong would either wipe `corr` mid-request, or leak `cust`/`op` across requests.
2. **Which failure was hardest to diagnose?**\
   The MDC leak from the 5th failure experiment would be the hardest to diagnose. Without a deterministic throwaway test, this bug would only show up occasionally. Errors that are hard to reproduce reliably are the hardest to diagnose.
3. **What evidence proves support can search a request?**\
   The evidence that proves support can search a request is that every log line for one HTTP call carries `corr=lab-request-001`. No matter what class or method produces it, you can get the full story due to the consistency of the value in logging.
4. **What breaks first at ten times the log volume?**\
   Anything in the log outputs that isn't structured would break quickly as long volume increases. Free text messages with inconsistent order or missing MDC make logs nearly unsearchable under large volumes.
5. **Which concern should move to shared infrastructure (shipping, retention, redaction)?**\
   Log shipping, retention policy, and redaction and scrubbing rules are good candidates to move over to shared infrastructure. All three are concerns that shouldn't be reinvented per service.
6. **What must change before real customer data is used (still: never log PII)?**\
   Nothing must change. Real customer data must never be logged, same as every other lab. Especially in logs, PII is a major compliance infraction.
7. **How does this lab connect to Labs 19 and 21?**\
   Lab 19 build the HTTP endpoints this lab uses. The same fixtures have stayed throughout. Lab 21 will add Actuator metrics that correlate with the logs.
8. **What log field matters most on the ops dashboard?**\
   The correlation ID `corr` field is the one that ties every other piece of evidence together, including logs, metrics, and traces. Being able to trace evidence back to a specific user request is the whole point of logging in the first place.
9. **(Forward look) Why must customer IDs stay in logs but not become Micrometer tags?**\
   A log line is created per event, so a high-cardinality value like an ID costs nothing extra to include. However, a Micrometer tag creates a new time-series per unique tag value, meaning tagging metrics with many unique IDs would explode the metrics storage.