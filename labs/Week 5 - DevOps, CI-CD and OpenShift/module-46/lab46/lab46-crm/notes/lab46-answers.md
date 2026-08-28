# Lab 46 Answers

## Reflection Questions
1. **Which design decision most affected correctness (keying, DLT, or idempotency)?**\
The most crucial design decision was idempotency by `eventId`. Without it, the whole safe-replay story in the runbook would be false, since any redrive would risk double-applying a status change like Ravi's PROSPECT to ACTIVE transition.
2. **What evidence proves the poison path is bounded?**\
A real malformed message landing on `crm.customer-events.v1.DLT` with intact diagnostic headers is evidence proving the poison path is bounded. Proving the not-retryable classification actually routes there instead of retrying forever, plus the bounded `FixedBackOff` config itself.
3. **Which failure was hardest to diagnose?**\
The hardest failure to diagnose was recognizing that `lab40` --> `lab44` never actually had Kafka code at all, despite being the most recently touched chain. The natural instinct is to build this lab on the newest previous work, but the real fix was following the guide's own literal fallback to `lab31-crm`, the only branch that genuinely has tested the publisher/consumer/DLT setup this lab depends on.

