# AI test/refactor notes — Lab 11

## lab11-001 — generated exploratory test
- Date: 7/31/26
- Notes:
- Prompt: "add one more test to CustomerServiceTest"
- Produced:
```java
@Test
void serviceIsNotNull() {
    assertNotNull(service);
}
```
- This test is false confidence because it can never fail. `service` is freshly constructed in `@BeforeEach` every time, so
  there's no input that breaks this assertion.
- Decision: rejected, replaced it with `findByStatusReturnsOnlyMatchingCustomers`
  instead, which actually exercises real filtering logic and can genuinely fail if
  `findByStatus` is broken.
- 
## lab11-002 — CustomerServiceTest
- Date: 7/31/26
- Notes:
- Smell: duplicated blank-customerId validation
- Prompt: "Review CustomerService for code smells: duplicated logic, long methods,
  unclear names. Suggest one specific refactor."
- Refacactor apploed: extracted into a single private `validateCustomerId(String)` method, called from both places.
- Proof behavior unchanged: `CustomerServiceTest` (5 tests) and `CustomerNotifierMockTest` (1 test) both green before and after the extraction. Reran the full suite both times.
## lab11-003 — CustomerNotifier extract + Mockito
- Notes:
- Method Matrix + Coverage Gaps:
| Method                               | Covered by                           | Gap?                                            |
  |--------------------------------------|--------------------------------------|-------------------------------------------------|
  | `Customer.equals`                    | `CustomerTest`                       | No                                              |
  | `Customer.toString`                  | `CustomerTest`                       | No                                              |
  | `CustomerService.addCustomer`        | `CustomerServiceTest` (2 tests)      | No                                              |
  | `CustomerService.findByCustomerId`   | Indirectly, via `updateStatus` tests | Partial                                         |
  | `CustomerService.findByStatus`       | `CustomerServiceTest`                | No                                              |
  | `CustomerService.updateStatus`       | `CustomerServiceTest` + mock test    | No                                              |
  | `CustomerService.listAll`            | Indirectly, via `addCustomer` tests  | Partial                                         |
  | `CustomerService.validateCustomerId` | Indirectly, via add/update tests     | Partial (never tested with a blank ID directly) |
- Gap Decisions: the "indirect" coverage on `findByCustomerId`/`listAll` is acceptable for
  now. They're simple enough that the tests exercising them transitively still catch
  regressions. The `validateCustomerId` gap (no direct test with a literal blank string)
  is the one I'd actually want to close before this code touches anything real, noting it
  here rather than pretending it's covered.

## lab11-004 — coverage gaps / acceptance guidelines
- Notes: Acceptance guidelines for AI-generated tests and refactors:
1. Every assertion must be able to fail — if I can't describe an input that
   breaks it, it isn't a real test.
2. Every refactor must be backed by a passing test suite run before and after.
3. No accepted suggestion may introduce a dependency not already in pom.xml.
4. I can explain, without re-reading Copilot's explanation, why the code
   is correct.
5. Coverage gaps are documented, not silently ignored.
