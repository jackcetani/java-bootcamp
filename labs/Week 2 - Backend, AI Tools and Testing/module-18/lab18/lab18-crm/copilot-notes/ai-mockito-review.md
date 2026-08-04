# AI Mockito Review — Lab 18

## lab18-001 — duplicate-email mock test
- Status: **manual** 
- Date: 8/4/2026
- Review checklist:
    1. Mocks the class under test? No — only `CustomerRepository` is mocked.
    2. Stubs minimal (no unused `when`)? Yes, only `existsById` and `existsByEmail` are
       stubbed, both of which the duplicate-email path actually calls.
    3. Verification matches real validator call order? Yes, `existsByEmail` is checked
       after `existsById`, matching `CustomerValidator.validateNew`'s actual order.
    4. Any `Thread.sleep` or real DB? No.
    5. `mvn -q test` green after adding it? Yes.
