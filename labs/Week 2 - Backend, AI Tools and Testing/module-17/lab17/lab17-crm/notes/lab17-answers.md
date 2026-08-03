## Lab 17 Answers

### Reflection Questions
1. **Which design decision most affected correctness?**\
The most important design decision was wiring a fresh repository in `@BeforeEach`. A shared static state would have made tests flaky and order-dependent.
2. **Which failure was hardest to diagnose?**\
The deliberate 0.99 gate failure would be the hardest failure to diagnose. The error message is clear if you know what to look at, but in a real situation, the low coverage gap would be much harder to pinpoint.
3. **What evidence proves the implementation works?**\
The two consecutive identical `mvn test` runs and the JaCoCo HTML showing >= 0.8 on `service`.
4. **What breaks first at ten times the suite size?**\
Test run time and adding sleep-based waits or shared fixtures to speed up the process. They are both anti patterns this lab says not to indulge.
5. **Which concern should move to shared CI infrastructure?**\
The coverage gate enforcement should be moved to shared CI infrastructure. A CI pipeline should block merges automatically rather than relying on a dev running `verify`.
6. **What must change before real customer data is used in tests (spoiler: don’t)?**\
Nothing should change. Real customer data should NEVER be used in tests.
7. **How does this lab connect to Labs 11, 15–16, and Lab 18?**\
Lab 11 previewed this lab at a smaller scale. Labs 15 and 16 are what we're testing. Lab 18 will swap the real repository collaborator for a mock without touching fixtures.
8. **What metric matters most on the CI dashboard for this gate?**\
The metric that matters most for this gate is the coverage trend line for `com.northstar.crm.service`.
9. **(Forward look) Which tests will need rewriting when the repository becomes a mock?**\
None of the assertions should change. How the `repository` and `validator` get constructed in `@BeforeEach` will have to change.