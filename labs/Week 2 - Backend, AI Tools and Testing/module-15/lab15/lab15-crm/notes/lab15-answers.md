## Lab 15 Answers

### Reflection Questions
1. **Which design decision most affected correctness?**\
The most important design decision was wiring one shared `CustomerRepository` instance in both the validator and the service. Two separate instances would silently break duplicate detection.
2. **Which failure was hardest to diagnose?**\
The hardest failure to diagnose was status being mutated before validation failed. Reordering `validateTransition` before `setStatus` in `changeStatus` was the fix.
3. **What evidence proves the implementation works?**\
The evidence is `CustomerValidatorTest` and the `Main` demo showing Ravi activated and Amina's illegal transition rejected with her status unchanged afterward.
4. **What breaks first at ten times the load (or concurrent activations)?**\
The in-memory `HashMap` has no concurrency control. Simultaneous activations could race, so a real DB with proper transaction isolation is needed at that point.
5. **Which concern should move to shared infrastructure?**\
The transition-policy table should be moved to shared infrastructure. It is a strong candidate for a config service once product want to change KYC rules without a code deployment.
6. **What must change before real customer data is used?**\
A persistence swap to a real database with actual concurrency guarantees. Also, real authn/authz on `changeStatus` must be implemented.
7. **How does this lab connect to Labs 14 and 16–18?**\
Lab 14's facade depends on this lab's `CustomerService` interface. Lab 16 will wrap these `IllegalStateException`s in more detailed typed exceptions. Labs 17 and 18 will mock `CustomerRepository` and `CustomerValidator` directly through these same constructors.
8. **What metric or log field matters most for rejected transitions?**\
For rejected transitions, the metric that matters most is the rejected-transition count grouped by `from -> to` pair. This tells product exactly which illegal transition users keeps attempting.
9. **(Forward look) What stays identical when Spring injects DefaultCustomerService?**\
The `DefaultCustomerService`, constructor signature, `ALLOWED` transition table, and every validation rule will stay the same. Only who calls `new DefaultCustomerService()` changes.