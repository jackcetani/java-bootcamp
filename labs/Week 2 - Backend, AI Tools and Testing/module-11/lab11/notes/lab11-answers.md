## Lab 11 Answers

### Manual Verification
| Test                                                                    | Result |
|-------------------------------------------------------------------------|--------|
| `mvn -q clean test` passes (≈7 tests, document exact count).            | PASS   |
| `CustomerTest` proves equals/toString with real assertions.             | PASS   |
| `CustomerServiceTest` covers add / duplicate / update / unknown-ID.     | PASS   |
| Mock test verifies notifier args for `CUS-1002`.                        | PASS   |
| `CustomerNotifier` is a useful extraction—not a Spring/JPA paste.       | PASS   |
| Notes `lab11-001`–`lab11-004` present.                                  | PASS   |
| At least one false-confidence assertion rejected.                       | PASS   |
| No secrets / real PII in tests or prompts.                              | PASS   |
| `git status` clean of `target/` junk.                                   | PASS   |
| You can explain every accepted AI test/refactor without reopening Chat. | PASS   |

### Concepts to Discuss
1. **Difference between an exploratory Copilot-generated test and a deliberately designed suite?**\
Exploratory tests make sure the obvious components of a program work. A deliberate suite is planned around edge cases and failure modes on purpose. This lab is an example of exploratory.
2. **What makes an assertion “false confidence”?**\
An assertion that has no chance of failing given the way the test is set up. It looks like coverage but is false confidence.
3. **Why extract CustomerNotifier before mocking, instead of mocking concrete CustomerService?**\
You can't cleanly mock  concrete class's internal side effect. An interface gives Mockito something real to substitute.
4. **What is a code smell, and which Lab 10 smell is the clearest refactor candidate?**\
Code smells are warning signs in source code that hint at design problems. In this lab, the duplicated blank-ID validation was the obvious one.
5. **Why is high coverage % not the same as meaningful coverage?**\
A test can execute a line without actually asserting anything useful about it. Coverage tools count execution, not correctness.
6. **What regression risk exists when refactoring without a full suite—and how do today’s tests help?**\
You could silently break behavior by refactoring. This would be hard to notice. That is why this lab's tests exist to catch it before that happens.
7. **When should you trust a Copilot extract-method vs verify manually?**\
You should always verify manually, no matter what. You should also rerun the suite before and after.
8. **What acceptance criteria should a reviewer apply before merging an AI-generated test or refactor?**\
Basically the checklist in 'ai-test-refactor-notes.md'. For example, can it fail, was it tested before or after, no new deps, can you explain it, and are gaps documented.
9. **Why keep JUnit/Mockito at test scope?**\
This is for the same reaosn as in lab 9. A production consumer of this JAR should never be forced to also pull in test frameworks.
10. **How does this preview set up Labs 17–18 without replacing them?**\
It's not a replacement but a preview. The real methodology comes in the later labs, but this one just gets the review habits down.

### Security and Production Review
1. **Which test data is safe to commit, and why (CUS-1001 / CUS-1002)?**\
CUS-1001 and CUS-1002 are safe to commit since they are sample examples, not real data.
2. **Where is human review enforced before AI tests/refactors merge?**\
It is enforced via the review-log entries. Nothing merges unread.
3. **What risk does an always-green trivial test create?**\
It risks a false sense of safety. A real regression could slip trough while the suite looks "green".
4. **What is the risk of accepting a refactor without before/after suite runs?**\
You wouldn't know if you brooke something until it surfaces somewhere much later and harder to trace.
5. **Which values must never appear in tests or mocks?**\
Real customer data, credentials, PII, etc. This goes for prompts as well.
6. **What would a tech lead audit for meaningful coverage?**\
Whether the assertions actually fail, not just the coverage percentage.
7. **How does mocking CustomerNotifier reduce coupling vs concrete implementations?**\
The tests don't need a real notification mechanism to exist yet. Instead, they just verify teh interaction happened correctly.
8. **How do you keep an audit trail of AI-suggested vs human-verified test code?**\
The `ai-test-refactor-notes.md` entries are the audit trail, same as for lab 10.

### Reflection Questions
1. **What made a test “meaningful” vs “false confidence” here?**\
Whether or not I could describe a concrete input that would make a test fail makes it meaningful. For example, `serviceIsNotNull` was false confidence because it could never fail, while `findByStatusReturnsOnlyMatchingCustomers` was meaningful because it could fail if logic was broken.
2. **How did extracting CustomerNotifier change testability?**\
It made tests more focused and easier to verify. Instead of testing the concrete implementation of `CustomerService`, we could mock the notifier and verify that is was called correctly.
3. **What would you tell a teammate who accepts every Copilot test unread?**\
That they are taking a huge risk. For one, this is how false confidence tests become a thing. A green check or a compilation success, DOES NOT mean that the code is correct or that the tests are accurate. Until every line of code created by Copilot is reviewed, it should be considered untrusted and should not be accepted.
4. **Which refactor suggestion did you reject, and why?**\
I rejected the trivial `serviceIsNotNull` test created by Copilot. I rejected a refactored it to be a real assertion instead.
5. **How does this preview connect to Labs 17–18?**\
It uses the same tools and review habits, but is just a preview rather than full methodology. Later in labs 17-18 we will go more into detail and apply it to real-world scenarios.
6. **Which coverage gap is acceptable now, and what would change that later?**\
For now, the indirect coverage of `findByCUstomerId` and `listAll` are acceptable for now. Simple methods that transitive coverage still catches regressions. However, we would want it to be more direct before this code got more complex.
7. **How does this lab connect to the wider Northstar CRM platform (Weeks 2–6)?**\
This lab is where the safety net of tests and refactors are implemented. Every later lab that touches `CustomerService` inherits this test suite as a regression check. It also sets up the review habits used in later labs.
8. **What is the cost of skipping before/after test runs on a refactor in a shared codebase?**\
It risks comebody else pulling a broken build and losing time trying to fugure out why their build isn't working. They would waste a lot of time without knowing it wasn't their changes that broke the build, but instead somebody else's changes breaking their build.
9. **(Forward look) When Spring arrives, what about today’s notifier mock pattern stays valuable?**\
The pattern of using mocks to verify interactions with dependencies is valuable. It is exactly how you'd unit-test a Spring-managed bean too. Instead of this being a one-off pattern for this lab, it's the actual long-term approach we should be using.