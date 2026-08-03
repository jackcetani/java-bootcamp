## Coverage Notes

### Concepts to Discuss
1. **Main flow under test (service use cases, not UI)**\
`DefaultCustomerService` and `CustomerValidator` use cases. No UI or HTTP.
2. **Trust boundary: what tests prove vs what they assume about the repo**\
Tests prove service behavior. They assume the `InMemoryCustomerRepository` itself is correct.
3. **Success/failure contracts encoded as asserts**\
Every success/failure path from the last two labs have a matching `assertEquals` and `assertThrows`.
4. **Stable fixtures (CUS-1001) vs random data**\
`CUS-1001/1002` stay fixed so failures are recognizable across every test file. Random data would make it hard to tell if your tests would actually work on past data.
5. **Idempotency of mvn test (repeatability)**\
`mvn test` is idempotent because it gives identical results no matter how many times you run it. There is no shared static state or time-based flakiness.
6. **Why ≥80% on service not 100% whole project**\
Since the service package holds the business rules, chasing 100% everywhere (with getters, DTOs) wastes effort for no real gain.
7. **Evidence operators/leads need (Surefire + JaCoCo)**\
Surefire's pass / fail summary and JaCoCo's HTML report are evidence for operators. Both are machine-checkable.
8. **Two machines: same tests, same fixtures, same gate**\
There is nothing environment specific, so it will all be the same fixtures, gate threshold, and pass/fail outcomes.
9. **False-confidence asserts vs AAA with domain outcomes**\
Asserts prove nothing. On the other hand, AAA with a real domain outcome actually can fail.
10. **What Lab 18 will change (repo → mock) without rewriting fixture IDs**\
Lab 18 will swap the `InMemoryCustomerRepository` collaborator with a Mockito mock. Fixture IDs and test structure won't need to change.