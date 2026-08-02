## Lab 14 Answers

### Reflection Questions
1. **Which design decision most affected correctness?**\
Validating strictly before calling the service was the most important design decision. The ordering is what stops bad data from ever reaching business rules.
2. **Which failure was hardest to diagnose?**\
A wrong-case status string failing at `CustomerStatus.valueOf()` would be hard to diagnose. It is easy to misdiagnose a validation bug when it's actually a mapping bug.
3. **What evidence proves the implementation works?**\
The validation test suite (3+ tests) plus a facade demo showing invalid email rejected with the correlation ID attached.
4. **What breaks first at ten times the field/load count?**\
Manually keeping the README constraint table in sync with the actual annotations. Drift becomes likely without some kind of generated doc.
5. **Which concern should move to shared infrastructure later?**\
The `Validator`/`ValidatorFactory` bootstrap should move to shared infrastructure later. Spring will own that lifecycle instead of each facade building its own.
6. **What must change before real customer data is used?**\
DTOs would need way more constraints (phone format, PII handling) before this touches any real customer data.
7. **How does this lab connect to Labs 12–13 and later Spring validation?**\
Lab 12 gave the service worth protecting. Lab 13's XSD constraints are the SOAP equivalent of these annotations. Lab 29 swaps manual `validate()` for `@Valid` without touching the rules themselves.
8. **What metric or log field matters most for invalid payloads?**\
The validation failure rate would matter most. When broken down by field, it tells you which constraint clients trip over the most.
9. **(Forward look) What stays stable when @Valid replaces manual Validator calls?**\
Every annotation on `CustomerRequestDTO` and every constraint message stays stable. Only the trigger mechanism changes.