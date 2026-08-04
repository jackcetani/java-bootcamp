## Lab 18 Answers:

### Reflection Questions:
1. **Which design decision most affected correctness (shared mock repo vs @InjectMocks alone)?**\
The most important design decision in this lab was sharing one mock `CustomerRepository` between the validator and the service, instead of wiring them separately. If they were pointed at two different mock instances, the uniqueness stubs (like `existsById`) would never fire for the code path that matters.
2. **Which failure was hardest to diagnose (UnnecessaryStubbing, wrong verify count, …)?**\
AN `UnnecessaryStubbingException` is hard to diagnose, because the error message doesn't make it obvious. Once you know that Mockito is complaining about a stub you didn't use, it becomes clearer.
3. **What evidence proves the implementation works (captor values, never().save)?**\
The `ArgumentCaptor` values and the `never().save()` verification on the not-found and illegal-transition paths prove it works. They would also prove what doesn't work in an implementation.
4. **What breaks first at ten times the suite size (shared static mocks, brittle verifyNoMoreInteractions)?**\
Shared or static mocks would be the first thing to cause failures. This is why `@BeforeEach` rebuilds everything and helps to avoid order-dependent failures.
5. **Which concern should move to shared CI infrastructure (Mockito version pin, strict stubbing)?**\
Pinning the Mockito version and enforcing stricter stubbing across the entire project would be good ideas. Having a shared parent POM or CI config is better than something each folder re-declares independently.
6. **What must change before real customer data is used in tests (spoiler: don’t)?**\
Nothing must change, real customer data should never appear in test code. Only the stable fixtures we created can be used.
7. **How does this lab connect to Labs 15–17 and Lab 19?**\
Labs 15-17 build the business rules and the repo test suite this lab is isolating. Lab 19 will ass HTTP and UI on top without changing anything else. This is the isolation were adding now at work.
8. **What metric matters most on the CI dashboard for this isolation gate?**\
The `mvn test` pass and fail rates would matter most on the CI dashboard. A failure here would mean a service bug or the wrong thing being mocked. Both are big problems that require attention.
9. **(Forward look) How will Spring @MockBean differ from these plain Mockito unit tests?**\
`@MockBean` replaces a bean inside the full Spring application context. The plain Mockito tests never create any container or context, which is why they're faster and more isolated than `@MockBean`.