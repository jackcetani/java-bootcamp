# Lab 31 Answers

## Reflection Questions

1. **Which design decision most affected correctness (publish-after-success vs outbox)?**\
The most crucial design decision was publishing after `customers.save()` succeeds, as it makes events trustworthy. An event never claims something happened before it actually does. The trade-off is the publish-failure gap documented in the notes.
2. **Which failure was hardest (deserialization, DLT wiring, flaky await)?**\
Getting the idempotency check to run before the notification side effect, not after, was the hardest failure to diagnose. It's a one-line mistake that only shows itself under a real duplicate delivery, not a normal test, so it's hard to catch.
3. **What evidence proves once-only business side effects?**\
Failure experiment 3's log pair is evidence of once-only business side effects. As in, the first delivery's `customer_event_recieved` and a real `notification_sent` followed by the replay's `duplicate_event_ignored` without another `notification_sent` for the same `eventId`.
4. **What breaks first at ten times the event rate?**\
At ten times the event rate, `ProcessedEventStore`'s unbounded in-memory `Set<UUID>` will break first. It never evicts anything, so at a high event volume it will grow until the JVM runs out of heap space. 
5. **Which concern should move to shared infrastructure (Schema Registry, ACLs)?**\
The event schema evolution belongs in a Schema Registry rather than being enforced by a compact constructor's version check. It only protects this one consumer, not every future consumer of the topic. Kafka AC:s restricting who can publish and consume also belong at the infrastructure level, not left to the code.
6. **What must change before real customer data is used in events?**\
Before real customer data is used, `ProcessedEventStore` needs to become a durable, shared store so idempotency survives restarts and works across multiple instances. The broker itself also needs TLS/SASL and ACLs before any PII crosses it.
7. **How does this lab connect to Labs 30 and 32?**\
Lab 30 provided the broker, topics, and keying / ordering this lab now builds the listeners on. Lab 32 keeps this Kafka backbone untouched and layers Resilience4j onto a separate outbound HTTP call.
8. **What metric or log field matters most for consumer ops?**\
The most important field for consumer ops is the `correlationId`, since it's the one field that lets an operator trace one business request across the HTTP, publish, and listener logs without needing the `eventId` on-hand. Consumer lag matters most for spotting a stuck listener before it becomes a problem the customer can see.
9. **(Forward look) How would transactional outbox change Step 4?**\
A transaction outbox would write an event as a row in an outbox table inside the same transaction as the customer save, rather than `CustomerService` calling `events.publish()` right after `save()`. A separate relay process would then read unpublished outbox rows and call `KafkaTemplate.send()` marking them published only on broker acknowledgement.