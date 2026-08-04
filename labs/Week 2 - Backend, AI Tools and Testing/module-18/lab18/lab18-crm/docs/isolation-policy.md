# Lab 18 — isolation policy

## What we mock

- `CustomerRepository` (I/O boundary)

## What we keep real

- `CustomerValidator` (domain rules)
- `DefaultCustomerService` (class under test — never mock it)

## When to prefer Lab 17 real-repo tests vs Lab 18 mocks
Real in-memory tests, such as `CustomerServiceTests` are best for proving end-to-end service behavior across multiple calls where the actual storage semantics matter. Mockito tests on the other hand are best when you specifically want to prove which repo methods get called with what arguments. Similarly, if you want to prove what methods don't get called, like never().save(). 

## Concepts to Discuss
1. **Main flow under test (service use cases with stubbed repo, not HTTP)**\
Every test is run on the `DefaultCustomerService` use cases. Using just the service's logic, it calls out to `CustomerRepository`.
2. **Trust boundary: what mocks prove vs what they assume about repository contracts**\
A mock proves the service calls the repository correctly. A mock doesn't prove the repository necessarily behaves that way, which is lab 17's job.
3. **Success/failure contracts encoded as asserts and verify / never**\
Successes get `assertEquals` on the customer. A failure gets `assertThrows` and `verify(..., never()).save(). 
4. **Stable fixtures (CUS-1001) vs random data in stubs**\
Every test uses stable fixtures like `CUS-1001` and `Customer.ravi()`. We never randomly generate data to test so that failures are consistent across different runs.
5. **Idempotency of mvn test and fresh mocks per @BeforeEach**\
`mvn test` gives the same result every time because `@BeforeEach` builds a new mock and service for every test method. Nothing carries over, thus `mvn test` is idempotent.
6. **Why unit mocks coexist with Lab 17 real in-memory suite**\
Real-repo tests will catch storage / shape bugs, while mock tests catch the interaction / contract bugs. This includes wrong method calls, unsafe retries, etc. Both have their own purpose even though they are different failure classes.
7. **Evidence operators/leads need (Surefire + isolation README)**\
The Surefire output and the isolation-policy doc will tell a lead what's proven instead of having to read every test body.
8. **Two machines: same stubs, same fixtures, same verify counts**\
Since nothing touches a real database or file system, the same verify counts will be produced on any machine.
9. **False-confidence: unused stubs, mocking the class under test, Thread.sleep**\
Tests that have an unused `when()` stub, a mock of `DefaultCustomerService`, or a `thread.sleep` are signs of false confidence. They show a test doesn't actually prove anything.
10. **What Lab 19 will change (HTTP/UI) without rewriting fixture IDs**\
Lab 19 adds HTTP and UI boundaries, but the fixture IDs and service contracts are stable and stay the exact same. Nothing will get rewritten, only added on top.