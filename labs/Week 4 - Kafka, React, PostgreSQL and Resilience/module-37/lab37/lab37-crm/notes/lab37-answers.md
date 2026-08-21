# Lab 37 Answers

## Reflection Questions

1. **Which design decision most affected correctness?**\
The most crucial design decision was choosing `NUMERIC(19,2)` over any floating point type for `account.balance`. Failure exp 4 shows floating point silently losing precision on values.
2. **What evidence proves the implementation works?**\
TThe three negative tests each producing the correct and distinct SQLSTATE codes is one piece. Also, `04_verify.sql` showing the original seed data unchanged after. Together they prove the constraints fire under hostile input and that savepoint rollback genuinely isolates the damage.
3. **Which failure was hardest to diagnose?**\
The floating point error mentioned above would be the hardest to diagnose. Failure experiment 4 shows how easy it would be to miss floating point silently losing precision. Even on values as simple as 0.1 + 0.2.