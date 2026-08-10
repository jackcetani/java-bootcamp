# Lab 23 Answers

## Reflection Questions
1. **Which design decision most affected correctness?**\
The most crucial design decision was avoiding manual exception checks using `if` by implementing the `@Valid` and `@NotBlank` validation. It's good because it keeps code cleaner, matches Springs idiomatic pattern, and means status codes like 400/404 come from framework-level mechanisms rather than custom logic that could eventually break.
2. **Which failure was hardest to diagnose?**\
Without knowing what to look for, figuring out `pom.xml` is missing `spring-boot-starter-validation` would be hard to diagnose. `@Valid` and `@NotBlank` will compile file without it but silently do nothing at runtime, which is hard to see if you're not aware of it.
3. **What evidence proves the implementation works?**\
First, the `create1 / `get` and 404 tests in `CustomerControllerIT` is one piece of evidence the implementation works. Then, the manual curls for both fixtures actually prove the tests work as expected.
4. **What breaks first at ten times the request rate with an in-memory map?**\
The lack of persistence becomes hard to handle at ten times the request rate. Any restart would wipe everything, which is alright at a local lab scale but would not pass in true prod environments.
5. **Which concern should move to shared infrastructure (Actuator policy, reverse proxy)?**\
The Actuator expose policy should move to shared infrastructure and have a consistent allow-list that can be enforced across every service centrally. Also, a reverse proxy in front of every instance should be implemented, none of these concerns should be reinvented per Boot.
6. **What must change before real customer data is used?**\
Persistence has to move of the in-memory map entirely for one. Also, the Actuator and logging exposures need a lot of hardening before they are production ready. As of now, the exposure strategy we've been using is loose, only should be used for local lab.
7. **How does this lab connect to Labs 22 and 24–26?**\
Lab 22 implemented the constructor injection that the service and controller still follow in this lab. Lab 24 will add a SOAP endpoint besides this API contract without changing it. Lab 25 will formalize the Controller --> Service --> Repo split. Lab 26 will lean more into making `dev` and `prod` profiles meaningful.
8. **What metric or log field matters most on first Boot smoke?**\
The line showing "Started CrmApplication in X seconds" tells you the server actually came up, while the `actuator/health` status tells us if it's actually serving traffic. This is a fast way to tell if things are working as expected compared to reading through a bunch of log output.
9. **(Forward look) What must stay stable when Lab 24 adds SOAP beside REST?**\
The API paths, JSON field names, status codes, and `CustomerService` itself must not change at ALL. Lab 24's SOAP endpoint delegates to the same service bean, and is additive rather than replacing.