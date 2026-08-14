# Lab 27 — ACID evidence

| Property | Lab evidence |
| -------- | ------------ |
| Atomicity | TODO: ACC-FORCE-FAIL leaves MAIN unchanged |
| Consistency | TODO |
| Isolation | TODO (lab note) |
| Durability | TODO (H2 mem limits) |

## Concepts to Discuss

1. **Transfer flow: HTTP → transactional service → two account updates + log**\
   HTTP POST --> `TransferController` --> `TransferService.transfer()` --> `TransactionLog`
2. **Trust boundary: amounts, account ownership, correlation ID**\
   The transfer amount, account IDs, and the correlation header are all untrusted input. The service validates account existence and sufficient funds before touching any balance.
3. **Success/failure contracts (HTTP + ledger appearance after failure)**\
   Happy paths return the `TransactionLog` with HTTP 200. A business failure such as insufficient funds, unknown account, etc., will throw and the ledger shows the state before the call. No HTTP success without a corresponding balance change and no balance change without a corresponding log row.
4. **Stable account IDs vs transfer idempotency keys**\
   ACC-1001-MAIN is stable and safe to reference forever. A transfer itself has no stable identity in this lab, which is what failure experiment 3 show us.
5. **Retry risks after network timeout (double spend)**\
   If a client never sees the response to a successful transfer, a retry would do a second transfer. This is a real double spending risk, not a safe no-op.
6. **H2 local shortcut vs PostgreSQL production isolation/durability**\
   The H2 in-memory is fine for the lab demos but loses everything on restart. It also doesn't reflect the production isolation and durability requirements. Lab 28 will address this.
7. **Evidence operators need (lab-request-001, balances, rollback logs)**\
   The evidence an operator would need is the lab-request-001 correlation, the before and after balances, and a rollback log entry (or the lack of one). Together these tell the whole story of a transfer attempt.
8. **Two app instances: DB transactions vs in-process locks**\
   The `@Transacation` boundary does not rely on any in-JVM lock, but instead relies entirely on the database's own transaction guarantees. This means it protects a single transfer's atomicity across multiple instances with the same database.
9. **Why @Transactional on controller is the wrong seam**\
   The controller is only an HTTP adapter, not suitable for an `@Transactional` boundary. Putting the annotation on the controller would be pointless, or could wrap unrelated HTTP-layer concerns in the worst case.
10. **Self-invocation (this.transfer) skipping the proxy**\
    Spring's `@Transactional` works by wrapping the bean in a proxy that intercepts calls from outside the bean. A method calling another method on `this` won't go through that proxy, so the annotation on the second method is silently ignored.
