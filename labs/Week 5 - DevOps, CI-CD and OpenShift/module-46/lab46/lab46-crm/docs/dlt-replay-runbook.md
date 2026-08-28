# Lab 46 — DLT replay runbook

## When to replay

Poison messages on `crm.customer.events.DLT` after root cause is fixed.

## Dry-run first

1. Inspect DLT records: `docker exec -it <kafka-container> kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic crm.customer-events.v1.DLT --from-beginning --property print.headers=true --max-messages 10`
2. Peek without producing to main topic: the command above only reads — it never republishes on its own, confirmed safe to run repeatedly
3. Confirm idempotent handler will not double-apply: `ProcessedEventStore` keys by `eventId`, so even a replayed message with the same `eventId` is a safe no-op (proven by `CustomerEventListenerIdempotencyTest`)

## Limited replay

1. Rate-limit: no more than 5 messages/sec for this lab's scale
2. Replay N messages → verify `CUS-1001`/`CUS-1002` projections update exactly once, not duplicated
3. Stop on unexpected errors; escalate rather than replaying blindly

## Evidence

See `notes/screenshots/lab-46/dlt-inspection.png` and `notes/screenshots/lab-46/idempotency-test.png`