# Lab 30 Answers

## Reflection Questions

1. **Which design decision most affected correctness (keying by customerId)?**\
The most crucial design decision was using `customerId` as the record jey rather than a random or missing key. It's the decision that makes Amina's event's stay in relevant order rather than coincidental.
2. **Which failure was hardest to diagnose (lag, rebalance, advertised listeners)?**\
The accidental auto-created topic outlined in failure experiment 5 is a very hard failure to diagnose if you don't know what to look for. A typo in a topic name silently creates an incorrect, empty, new topic and doesn't fail loudly. Could easily be overlooked, causing real problems.
3. **What evidence proves produce/consume works end-to-end?**\
The line printed by the Java producer showing `topic`, `partition`, and `offset`, cross-checked against the CLI consumer showing the same key/partition/offset for that same record. These two independent tools agreeing is the strongest proof compared to either alone.
4. **What breaks first at ten times the event rate?**\
Consumer lag on whichever group has the fewest members ralative to the partition count would break first at ten times the event rate. `crm-notifications` splitting three partitions across two consumers would start falling behind well before the broker itself showed any strain.
5. **Which concern should move to shared infrastructure (managed Kafka, ACLs)?**\
A managed Kafka cluster with central ACLs should be the first thing to move to shared infrastructure. Topic creation, retention policy, and access control shouldn't be decided per service.
6. **What must change before real customer data is used in payloads?**\
TLS/SASL on the broker connection, real ACLs restricting who can publish and consume, and a real replication factor are needed before real customer data is used. All three are explicitly deferred in this lab's **local** setup.
7. **How does this lab connect to Labs 25–29 and Lab 31?**\
Labs 25-29 built the HTTP API and its validation, error, and security layers. This lab adds an asynchronous side-channel for the same customer facts without touching any of the REST surface. Lab 31 will wrap this exact broker and topic names with Kafka, reusing the keying tule and fixture IDs established here.
8. **What metric matters most on the ops dashboard for consumers?**\
The metric that matters most on the ops dashboard for consumers is the consumer lag, specifically over time rather than a single snapshot. A lag value that's flat, but not zero, is very different from one that's climbing. Only this view distinguishes them.
9. **(Forward look) Why does Lab 31 still need idempotent handlers?**\
Kafka's default deliver guarantee is at least one for consumers. A rebalnce, a consumer restart, or a retried commit can all cause the same record to be delivered twice even with a reliable broker and producer. Lab 31 doesn't change that guarantee, it just makes the plumbing more convenient so the same `eventId` deduplication is still necessary once `@KafkaListener` replaces the CLI consumer.