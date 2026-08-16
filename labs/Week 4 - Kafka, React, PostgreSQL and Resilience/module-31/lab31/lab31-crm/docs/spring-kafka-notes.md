# Lab 31 — Spring Kafka notes

## Concepts to Discuss

1. **Main flow: HTTP success → publish → listen → notify**\
   A customer `create` or `status` PATCH commits to the in-memory repo first, then `CustomerEventPublisher` sends a keyed `CustomerEvent` to `crm.customer-events.v1`. The listener then picks it up and checks it. If it's valid, it hands it to `NotificationHandler`.
2. **Trust boundary: key/payload validation before side effects**\
   The listener rejects any record whose key doesn't match the event's `customerId` before doing anything. That check runs before idempotency and before notifications, so a corrupted or forged key never reaches the business logic.
3. **Success/failure: publish callback vs DLT recoverer**\
   On the producer side, `whenComplete` logs `customer_event_published` or `customer_event_publish_failed`. On the consumer side, `DefaultErrorHandler` and `DeadLetterPublishingRecoverer` decide what happens when the listener throws.
4. **Stable identity: `eventId` for idempotency; `customerId` for keying**\
   `eventId` is a UUID given per business event and is what `ProcessedEventStore` tracks. `customerId` on the other hand is the partition key, which keeps every event about a specific customer in order, but says nothing about whether a given event was handled.
5. **Retry vs non-retryable exceptions**\
   `InvalidCustomerEventException` and `UnsupportedEventVersionException` mean the message itself is broken, which can't be fixed with a retry. Thus, both go to DLT. Anything else gets 2 retries with a backoff first.
6. **Local EmbeddedKafka vs Docker broker vs production cluster**\
   `EmbeddedKafka` makes an in-process broker per test run, so running `mvn test` doesn't need Docker. The Lab 20 Docker Compose broker is for manual demos against an actual Kafka. A prod cluster adds replication, TLS/SASL, and ACLs.
7. **Evidence: partition/offset logs, DLT headers, correlation ID**\
   `customer_event_published` logs the partition and offset the broker assigned. A DLT record carries headers showing the original topic, partition, offset, and exception, which is what an operator needs to trace a bad message back to the source. `correlationId` ties both to one business request.
8. **Two app instances: shared consumer group competition**\
   If two instances both run with the same `crm-notifications` group id, Kafka splits the topic's partitions between them so each event is handled by one instance, not both. `ProcessedEventStore` is in-memory and per-instance, meaning this only fully protects against duplicates in one instance, not across a restart or second instance replaying.
9. **Why trusted packages matter for JSON deserialization**\
   `spring.json.trusted.packages` tells `JsonDeserializer` it's only allowed to instantiate that package's classes from message bytes. Leaving it open with `*` means any JSON payload header could get deserialized into an arbitrary class on the classpath.
10. **What Lab 32 adds without changing Kafka contracts**\
    Lab 32 will wrap the outbound HTTP call to an account-profile service using Resilience4j. It doens't touch `crm.customer-events.v1`, the event schema, or anything built in this lab.

## Publish path

`CustomerController.create()` and `.updateStatus()` both commit to the repository first, then call `CustomerEventPublisher.publish(...)` with a freshly built `CustomerEvent` (`eventType` = `CustomerCreated` or `CustomerStatusChanged`, `eventVersion` = 1, keyed by `customerId`).

## Idempotency

`ProcessedEventStore.markIfNew(eventId)` is checked in the listener *before* calling `NotificationHandler.handle(...)`. First delivery marks the ID and processes; any redelivery of the same `eventId` (replay, rebalance, retry) logs `duplicate_event_ignored` and returns without a second notification. This store is an in-memory `ConcurrentHashMap`-backed set and resets on restart and isn't shared across instances, which is fine for this lab but would need a durable, shared unique-key store (e.g. a DB table on `event_id`) in production.

## DLT

**Decision: using Spring's default DLT naming** (`DeadLetterPublishingRecoverer` with no explicit destination resolver) rather than Lab 30's `crm.customer-events.v1.dlq`. A poison message on `crm.customer-events.v1` therefore lands on `crm.customer-events.v1.DLT`, not `.dlq`. The `.dlq` topic Lab 30 created stays provisioned but unused by this app unless a future lab explicitly wires a custom destination resolver to redirect there.

DLT records carry Spring's standard `KafkaHeaders` identifying the original topic, partition, offset, and exception class/message. This is how an operator traces a dead-lettered record back to where it failed.

## Publish timing note (DB vs Kafka)

- **Publish-after-success (this lab):** simple, and what's implemented here — `customers.save(...)` runs before `events.publish(...)`. The risk: if the DB write succeeds but the Kafka publish fails (broker down, network blip), the customer record exists but no downstream consumer is ever notified.
- **Transactional outbox (bonus/production):** write an event row in the *same* DB transaction as the customer write, then a separate relay process publishes from that outbox table to Kafka. This is the preferred pattern for anything where a missed notification is a real business problem.
- This lab does **not** claim dual-write atomicity — that would require the outbox pattern (or full DB+Kafka transactions) actually implemented end-to-end, which is out of scope here.

## Runbook

\`\`\`powershell
# Broker (Lab 30)
docker compose up -d

cd "C:\Users\jackc\Desktop\PNC Training\java-bootcamp\labs\Week 4 - Kafka, React, PostgreSQL and Resilience\module-31\lab31\lab31-crm"
mvn -q test
mvn -q spring-boot:run
# In a second PowerShell window: create CUS-1001 / update CUS-1002 via API
# Observe app logs for: customer_event_published / customer_event_received / duplicate_event_ignored
\`\`\`

| Item                                          | Lab value                                            |
|-----------------------------------------------|------------------------------------------------------|
| Bootstrap (host)                              | `localhost:9092`                                     |
| Primary topic                                 | `crm.customer-events.v1` (3 partitions, from Lab 30) |
| DLT destination (this lab)                    | `crm.customer-events.v1.DLT` (Spring default)        |
| Lab 30's provisioned DLQ (unused by this app) | `crm.customer-events.v1.dlq`                         |
| Record key                                    | `customerId` (`CUS-1001`, `CUS-1002`)                |
| Sample correlation                            | `lab-request-001`                                    |
| Consumer group                                | `crm-notifications`                                  |