# Lab 25 Answers:

## Reflection Questions
1. **Which design decision most affected correctness (where rules live)?**\
The most crucial design decision was keeping every business rule inside `CustomerService` and nowhere else. The moment any of that logic leaks into the controller, the imports would break and layering itself would be compromised.
2. **Which failure was hardest to diagnose?**\
The actual illegal-transition check being silently omitted is a hard one to notice. Without knowing what to look for, failure experiment 2 would have silently passed by doing nothing, since an unchecked `setStatus()` call never rejects anything.
3. **What evidence proves layering works?**\
The evidence for layering working is the combination of a controller file with no repo imports, a service file with no Spring Web imports, and failure experiment 5 breaking the moment an HTTP type was introduced. These are all proof of layering being correct.
4. **What breaks first at ten times load with an in-memory map?**\
Nothing at the data structure level will break, but the lack of durability will become obvious. Any restart or second instance will diverge from the data entirely.
5. **Which concern should move to shared infrastructure (DB, migrations)?**\
The database itself should move to shared infrastructure, which is lab 27's direction. Also, schema migrations should move. Both are the kind of cross-cutting concern that shouldn't be reinvented per lab or service.
6. **What must change before real customer data is used?**\
Persistence needs to move off the in-memory map first. Second, validation needs to be much stricter. Email, phone, and other PII must be properly formatted and validated before real customer data touches the service.
7. **How does this lab connect to Labs 23–24 and 26–28?**\
Lab 23 built the original flat API layer this lab now separates. Lab 27 will add `@Transactional` around repo calls without touching controller routes. Lab 28 adds security in front of the controller.
8. **What metric or log field matters most for layered CRM APIs?**\
The most important metrics would be the duplicate-rejection rate and not-found rate. Together, they can tell you whether it is client-side or a caching problem. This is more actionable than just an error count.
9. **(Forward look) Which files should stay untouched when JPA arrives?**\
The `CustomerController` and `CustomerService` files should stay completely untouched. Neither import anything persistence-specific in this lab, so a JPA-backed `CustomerRepository` can be swapped underneath without changing either file at all.