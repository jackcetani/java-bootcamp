# Lab 30 starter — timed path (~45 minutes)

**Theme:** Kafka EDA — topics, keys, producer

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `/` into your bootcamp examples tree as `lab30-crm`.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab30-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab30-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab30-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab30-crm
cp -R starter/. ~/java-bootcamp/examples/lab30-crm/
cd ~/java-bootcamp/examples/lab30-crm
```

## 45-minute checklist

- [ ] Start KRaft broker (`compose.yaml`) or use instructor shared bootstrap
- [ ] Create topics `crm.customer-events.v1` (3 partitions) + `.dlq`
- [ ] Complete event JSON samples for CUS-1001 / CUS-1002
- [ ] Fill `CustomerEventProducer` TODOs (acks=all, idempotence, key=customerId)
- [ ] Run smoke produce; note partition/offset; fill `docs/kafka-notes.md`

## Smoke test

```bash
docker compose up -d
# then: mvn -B -q package && mvn -B exec:java -Dexec.mainClass=com.northstar.crm.event.CustomerEventProducer
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-30/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| Broker up; topics describe (3 partitions on events) | Pass / Fail |
| Keyed produce for CUS-1001 / CUS-1002 succeeds | Pass / Fail |
| Producer uses acks=all + enable.idempotence | Pass / Fail |
| kafka-notes.md has produce→consume + keying notes | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

## Manual Verification

| Test                                                                   | Result |
|------------------------------------------------------------------------|--------|
| docker compose ps shows Kafka healthy/up.                              | PASS   |
| Topics crm.customer-events.v1 and .dlq exist with expected partitions. | PASS   |
| Console produce/consume shows keys CUS-1001 / CUS-1002.                | PASS   |
| Amina events share a partition with increasing offsets.                | PASS   |
| Java producer prints partition and offset.                             | PASS   |
| Competing group splits partitions; audit group is independent.         | PASS   |
| Lag rises when consumer stopped, falls after restart.                  | PASS   |
| Correlation IDs present in sample envelopes.                           | PASS   |
| Runbook commands listed for a peer.                                    | PASS   |
| No secrets committed; PLAINTEXT called out as lab-only.                | PASS   |

## Security and Production Review

1. **Which event inputs are untrusted (payload fields, keys)?**\
   Every field inside `data`, the `customerId`, and the `correlationId` are all untrusted. Nothing in this lab validates event shape before publish, which is a gap that has to be closed in prod before ever calling `send()`.
2. **Where will authn/authz for publish/consume be enforced in production?**\
   They are not enforced in this lab yet. The local plaintext broker has no authn or ACLs. Prod needs SASL/TLS for connection authentication and topic-level ACLs restricting which service principles can publish vs. which can consume.
3. **Which values are sensitive — never in event `data`?**\
   From `data`, passwords, tokens, payment details, and any PII beyond our fixed, fiction `fullName` and `status` fields are considered sensitive. An event envelope is durable and gets read by every consumer with access, so it's a bad place for sensitive values to leak into.
4. **What can be retried safely (consumer redelivery)?**\
   Consumer redelivery of the same record is safe **only** when the consumer is idempotent on `eventId`. Producer retries with `enable.idempotence=true` is safe trivially, since the broker itself deduplicates retried sends from the same producer session.
5. **What happens after partial failure (produced but consumer crashed mid-handle)?**\
   The record remains on the topic. If the consumer crashed before committing its offset, the same record gets delivered to whichever consumer picks up that partition next. Failure experiment 3 demonstrates this behavior and why idempotent handliing matters.
6. **What would an operator monitor (lag, ISR, failed produce rate)?**\
   An operator would monitor the lag per group, as a rising trend means consumers can't keep up. They would also monitor the in-sync replica (ISR) count in a real multi-broker cluster, as well as the failed-produce rate. All three answer different questions about the pipeline health.
7. **Which local default is unacceptable (PLAINTEXT, RF=1, auto-create)?**\
   Plaintext is the first offense for a prod environment, with no encryption or authentication. Leaving topic auto-create enabled is a second, and failure experiment 5 shows why. Lastly, `RF=1` would be the third, as a single broker failure results in data loss.
8. **How are event contracts versioned (`eventVersion`, topic `.v1`)?**\
   The topic name carries a cersion suffix and each envelope carries its own `eventVersion`. A breaking change to event shape would need either a new topic name or a documented consumer migration.

