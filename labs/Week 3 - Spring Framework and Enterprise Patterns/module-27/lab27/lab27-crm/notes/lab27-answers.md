# Lab 27 Answers

## Reflection Questions
1. **Which design decision most affected correctness (transaction boundary size)?**\
The most crucial design decision in this lab was keeping the entire debit / credit / log write inside one `@Transactional` method. A smaller boundary by splitting these into three separate `@Transactional` calls would let a partial write survive a mid-op failure.
2. **Which failure was hardest (proxy / self-invocation / exception type)?**\
The self-invocation failure from failure experiment 5 was the hardest to diagnose. The code looks identical to the correct version except for how one method calls another. Also, the actual failure mode depends on save-call ordering rather than anything visible in the signature. Hard to diagnose if you don't know what to look for.
3. **What evidence proves rollback works?**\
The evidence a rollback works would be re-checking the balance after a forced failure and making sure it matches the pre-call balance exactly. This is checking more than just if an exception was thrown, but that the balance actually persisted through the failure. 
4. **What breaks first at ten times the transfer rate?**\
At ten times the transfer rate, the row-level lock contention on accounts frequently used would become a problem with real concurrency. Failure experiment 4 shows a preview of what this problem would look like.
5. **Which concern should move to shared infrastructure (DBA isolation standards)?**\
Two things that should move to shared infrastructure are the DBA isolation-level tuning and lock-timeout policy. These are DBA standards that should be set once for the whole platform, not decided per-service.
6. **What must change before real customer money is moved?**\
H2 memory would have to be swapped for PostgreSQL with real durability and idempotency keys on every transfer request. Also, actual authn/authz in front of the endpoint would need to implemented.
7. **How does this lab connect to Labs 25–26 and Lab 28?**\
Lab 25's constructor-injection and service/repo layering is what makes `TransferService` testable and the transaction boundary clean. Lab 26's profiles is what lets dev's H2 datasource work here,, and Lab 28 plans to add security around these transfer paths without needing to touch the logic itself.
8. **What metric or log field matters most for ledger support?**\
The metric or log field that matters most for ledger support would be the correlation ID on the TransactionLog row. It's the one field that lets you trace a specific customer transfer back to the specific attempt.
9. **(Forward look) How would an idempotency key change the retry story?**\
An idempotency key a client generates once and reuses on every retry would let the server recognize it's already processed the same request and return the original result rather than re-executing a transfer. This would solve the double-spend retry risk in this lab.