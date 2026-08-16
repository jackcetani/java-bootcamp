# Lab 30 — Kafka notes (timed path)

## Produce → consume

A CLI or Java producer sends a keyed record to `crm.customer-events.v1`. The broker then
appends it to the partition selected by hashing the key. A consumer in a group reads
from its assigned partitions and, in a real system, commits its offset once the
record is handled.

## Keying

Key = `customerId` so every event about one customer (e.g. `CUS-1001`) always hashes
to the same partition. This is what keeps that customer's events in relative order.
Losing this (random or missing key) breaks ordering immediately.

## DLQ

`crm.customer-events.v1.dlq` exists so a record that repeatedly fails processing can
be moved aside instead of blocking every other record behind it on the same
partition. This lab only creates the topic, Lab 31 will wire it into real retry/recovery logic.


## Ordering and delivery semantics (read before Lab 31)

Same `customerId` key always hashes to the same partition, so events about one customer
stay in relative order on that partition. However, there is no global order across
different customers, since `CUS-1001` and `CUS-1002` events can land on different
partitions and interleave arbitrarily from a consumer's point of view. Kafka's
at-least-once delivery model means a consumer can see the same record more than once
after a rebalance or a retried send, so Lab 31's consumers must be idempotent on
`eventId` rather than assuming exactly-once delivery. The DLQ topic exists so a
record that repeatedly fails processing can be moved aside instead of blocking every
other record behind it on the same partition. This lab only creates the DLQ topic, while
Lab 31 is where it actually gets wired into real retry/recovery logic.

## Lab 31 hand-off runbook (Step 9)

| Item               | Lab value                                                  |
|--------------------|------------------------------------------------------------|
| Bootstrap (host)   | `localhost:9092`                                           |
| Primary topic      | `crm.customer-events.v1` (3 partitions)                    |
| DLQ topic          | `crm.customer-events.v1.dlq` (1 partition)                 |
| Record key         | `customerId` (`CUS-1001`, `CUS-1002`)                      |
| Sample correlation | `lab-request-001`                                          |
| Demo groups        | `crm-notifications` (competing), `crm-audit` (independent) |

Commands a peer needs, in order:
```bash
docker compose up -d
docker exec crm-kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic crm.customer-events.v1 --partitions 3 --replication-factor 1
docker exec crm-kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic crm.customer-events.v1.dlq --partitions 1 --replication-factor 1
docker exec -it crm-kafka /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic crm.customer-events.v1 --property parse.key=true --property key.separator=:
docker exec crm-kafka /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic crm.customer-events.v1 --from-beginning --property print.key=true --property print.partition=true --property print.offset=true
docker exec crm-kafka /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group crm-notifications
```
PLAINTEXT and RF=1 are lab-only — never a production recommendation.

## Replay and idempotency (Failure Experiment 3)

Running the consumer with `--from-beginning` replayed all 9 events from offset 0
again, even though several had already been seen in earlier steps.
From the point of view of any downstream system that already processed these once,
this is a duplicate delivery. This is expected, correct Kafka behavior, as replay is a
valid operational and debugging tool, not a bug. It means any real consumer
(Lab 31's Spring `@KafkaListener`) must be idempotent on `eventId`, since at-least-once
delivery guarantees a consumer may see the same record more than once after a replay,
restart, or rebalance.

```bash
jackcetani@Jacks-Laptop MINGW64 ~/Desktop/PNC Training/java-bootcamp/labs/Week 4 - Kafka, React, PostgreSQL and Resilience/module-30/lab30/lab30-crm (main)
$ docker exec -it crm-kafka /opt/kafka/bin/kafka-console-producer.sh \
  --bootstrap-server localhost:9092 --topic crm.customer-events.v1
>{"eventType":"CustomerCreated","eventVersion":1,"customerId":"CUS-1001","correlationId":"lab-request-001","data":{"fullName":"Amina Khan","status":"ACTIVE"}}
>^C
jackcetani@Jacks-Laptop MINGW64 ~/Desktop/PNC Training/java-bootcamp/labs/Week 4 - Kafka, React, PostgreSQL and Resilience/module-30/lab30/lab30-crm (main)
$ --property print.key=true --property print.partition=true
bash: --property: command not found

jackcetani@Jacks-Laptop MINGW64 ~/Desktop/PNC Training/java-bootcamp/labs/Week 4 - Kafka, React, PostgreSQL and Resilience/module-30/lab30/lab30-crm (main)
$ 

11. Failure Experiment 3: (Tell me what to document in kafka-notes.md)
jackcetani@Jacks-Laptop MINGW64 ~/Desktop/PNC Training/java-bootcamp/labs/Week 4 - Kafka, React, PostgreSQL and Resilience/module-30/lab30/lab30-crm (main)
$ docker exec crm-kafka /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 --topic crm.customer-events.v1 \
  --from-beginning --property print.key=true --property print.offset=true
Offset:0        null    {"eventType":"CustomerCreated","eventVersion":1,"customerId":"CUS-1001","correlationId":"lab-request-001","data":{"fullName":"Amina Khan","status":"ACTIVE"}}
Offset:0        CUS-1001        {"eventType":"CustomerCreated","eventVersion":1,"customerId":"CUS-1001","correlationId":"lab-request-001","data":{"fullName":"Amina Khan","status":"ACTIVE"}}
Offset:1        CUS-1001        {"eventType":"CustomerStatusChanged","eventVersion":1,"customerId":"CUS-1001","correlationId":"lab-request-001","data":{"status":"ACTIVE"}}
Offset:2        CUS-1002        {"eventType":"CustomerCreated","eventVersion":1,"customerId":"CUS-1002","correlationId":"lab-request-001","data":{"fullName":"Ravi Singh","status":"PROSPECT"}}
Offset:3        CUS-1001        {
  "eventId": "e4b5f53d-7f18-4cf4-81ce-7cab6ec98491",
  "eventType": "CustomerCreated",
  "eventVersion": 1,
  "occurredAt": "2026-07-13T06:00:00Z",
  "customerId": "CUS-1001",
  "correlationId": "lab-request-001",
  "source": "customer-service",
  "data": { "fullName": "Amina Khan", "status": "ACTIVE" }
}

Offset:4        CUS-1001        {"eventType":"CustomerCreated","eventVersion":1,"customerId":"CUS-1001","correlationId":"lab-request-001","data":{"fullName":"Amina Khan","status":"ACTIVE"}}
Offset:5        CUS-1001        {"eventType":"CustomerStatusChanged","eventVersion":1,"customerId":"CUS-1001","correlationId":"lab-request-001","data":{"status":"ACTIVE"}}
Offset:6        CUS-1002        {"eventType":"CustomerCreated","eventVersion":1,"customerId":"CUS-1002","correlationId":"lab-request-001","data":{"fullName":"Ravi Singh","status":"PROSPECT"}}
Offset:7        CUS-1001        {
  "eventId": "e4b5f53d-7f18-4cf4-81ce-7cab6ec98491",
  "eventType": "CustomerCreated",
  "eventVersion": 1,
  "occurredAt": "2026-07-13T06:00:00Z",
  "customerId": "CUS-1001",
  "correlationId": "lab-request-001",
  "source": "customer-service",
  "data": { "fullName": "Amina Khan", "status": "ACTIVE" }
}

Offset:8        CUS-1001        {
  "eventId": "e4b5f53d-7f18-4cf4-81ce-7cab6ec98491",
  "eventType": "CustomerCreated",
  "eventVersion": 1,
  "occurredAt": "2026-07-13T06:00:00Z",
  "customerId": "CUS-1001",
  "correlationId": "lab-request-001",
  "source": "customer-service",
  "data": { "fullName": "Amina Khan", "status": "ACTIVE" }
}

```
**Output:**\

## Concepts to Discuss

1. **Main produce → broker → consume flow for a customer event**\
   A CLI or java producer sends a keyed record to `crm.customer-events.v1`. Then the broker appends it to the partition selected by hashing the key. A consumer in a group then reads from its assigned partitions and commits its offset once the record is handled.
2. **Trust boundary: who may publish; why payloads stay free of secrets**\
   Only the customer service should publish onto `crm.customer-events.v1` in prod. nothing in this lab's local PLAINTEXT setup enforces this yet, which is why payloads must stay free of secrets.
3. **Success/failure contracts: ack, offset commit, DLQ purpose**\
   `acks=all` means the leader waits for the full replica set to acknowledge before confirming the `send`. The DLQ topic exists so a message that repeatedly fails can be set aside without blocking the rest of the partition.
4. **Stable identity: key=`customerId` for ordering of one customer’s events**\
   Every event about `CUS-1001` hashes to the same parititon, which is what makes Amina's event's stay in relative order. Losing this by using random or missing keys breaks that guarantee.
5. **Retry and idempotency under at-least-once delivery**\
   Kafka's default delivery ensures it can redeliver a record after a rebalance or a producer retry. Thus, a consumer must be able to safely process the same eventId twice without double-effect.
6. **Local KRaft PLAINTEXT vs production cluster security/RF**\
   This lab's `RF-1` unauthenticated broker is fine for this labs learning objective, but completely unacceptable for prod / real customer data. Prod needs TLS/SASL, a real replication factor, and proper ACLs.
7. **Evidence operators need (lag, partition assignment, correlation ID)**\
   Evidence operators needs includes the lag to tell how far behind a group is, the partition assignments, and the `correlationId` inside each event's envelope. Together they show if the pipeline is healthy.
8. **Two consumer instances in one group vs two groups**\
   Two instances sharing `crm-notifications` split the three partitions between them. `crm-audit` as a separate group id gets every record independently, regardless of what `crm-notifications` is doing.
9. **Why topics are created explicitly (no silent auto-create in prod habits)**\
   Relying on auto-create means a single typo in a topic name silently makes up a new, empty topic instead of failing loudly. Failure experiment 5 demonstrates this, which is why using `--create` with the frozen name is the correct practice for prod.
10. **What Lab 31 changes (Spring APIs) without renaming topics/fixtures**\
    Lab 31 replaces the CLI/plaintext java producer and consumer with Spring's `KafkaTemplate` and `@KafkaListener`, and wires the DLQ topic for real retry and recovery. None of that requires any renaming or changing the `customerId` keying rule made here.
