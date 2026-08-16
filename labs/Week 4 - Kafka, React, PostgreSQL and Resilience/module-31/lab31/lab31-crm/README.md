# Lab 31 starter — timed path (~45 minutes)

**Theme:** Spring Kafka — publish, listen, DLT, idempotency

## Activity card

| | |
| --- | --- |
| **Checkpoint** | **E** |
| **Must prove** | Publish · listen-once · error/DLT config · `mvn test` ×2 |
| **Hard gate** | Pre-lab Pass · Kafka bootstrap or EmbeddedKafka |

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `starter/` into your bootcamp examples tree as `lab31-crm`.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab31-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab31-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab31-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab31-crm
cp -R starter/. ~/java-bootcamp/examples/lab31-crm/
cd ~/java-bootcamp/examples/lab31-crm
```

## 45-minute checklist

- [ ] Confirm Kafka bootstrap (Lab 30 compose or shared cluster)
- [ ] Complete `CustomerEvent` + publisher (`KafkaTemplate`, key=customerId)
- [ ] Implement `@KafkaListener` + `ProcessedEventStore` idempotency
- [ ] Wire `KafkaErrorConfig` (DefaultErrorHandler + DLT recoverer)
- [ ] Run `mvn -B test`; capture Amina event handled-once evidence

## Smoke test

```bash
mvn -B test
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-31/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| Publisher sends keyed event to crm.customer-events.v1 | Pass / Fail |
| Listener handles once; replay ignored via ProcessedEventStore | Pass / Fail |
| Error handler / DLT config present | Pass / Fail |
| Integration test green twice | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.


### Troubleshooting

| Symptom | Fix |
| --- | --- |
| Deser errors | Trusted packages / JsonDeserializer config |
| Listener silent | New group-id or publish new events |
| DLT empty | Wire DefaultErrorHandler + DeadLetterPublishingRecoverer |
| Duplicate notify | Mark ProcessedEventStore before side-effect |


## Manual Verification

| Test                                                            | Result |
|-----------------------------------------------------------------|--------|
| Create/update Amina publishes an event with key CUS-1001.       | PASS   |
| Listener logs received event with lab-request-001.              | PASS   |
| Replay/duplicate eventId does not double-notify.                | PASS   |
| Key mismatch or bad version goes to DLT without infinite retry. | PASS   |
| Transient retries show bounded backoff then recover or DLT.     | PASS   |
| Integration test awaits specific eventId (no bare sleep).       | PASS   |
| Two consecutive mvn test runs match.                            | PASS   |
| Trusted packages restricted to com.northstar.crm.event.         | PASS   |
| Ravi (CUS-1002) publish/consume path works.                     | PASS   |
| No secrets or full PII payloads in Git/logs.                    | PASS   |

## Security and Production Review

1. **Which event/network inputs are untrusted?**\
   The Kafka record key, the deserialized `CustomerEvent` payload, and the `X-Correlation-Id` header are all considered untrusted. The listener treats the key/payload pairing as untrusted and checks they match before doing anything else.
2. **Where are validation and authz enforced (HTTP Lab 28/29 vs Kafka ACLs later)?**
   HTTP validation is persisted from Lab 29 and still runs before any event is built. Both authn/authz are gaps we would need to close with this lab right now, no JWT auth from lab 28 copied forward here.
3. **Which values are sensitive in payloads/logs?**\
   `fullName` and `email` are both sensitive fields. `CustomerData` never carries `email` intentionally. All log lines print IDs and status only, never the full payload.
4. **What can be retried safely vs must go to DLT?**\
   Everything can be retried twice with backoff, except for `INvalidCustomerEventException` and `UnsupportedEventVersionException` since those failures could be transient. A key mismatch or unsupported version is a contract problem that retrying won't fix, so they go to DLT.
5. **What happens after partial failure (DB committed, publish failed)?**\
   The customer record is saved regardless of whether the Kafka publish succeeds. This is because `save` runs first. If the publish then fails, the app logs it but the HTTP caller still gets a 201.
6. **What would an operator monitor (lag, DLT depth, error rate)?**\
   An operator would monitor the consumer lag for a stuck or slow listener, as well as a rising `crm.customer-events.v1.DLT` message count. Lastly, the publish/publish-failed rate logs would show broker connectivity issues.
7. **Which local default is unacceptable (open trusted packages, in-memory idempotency alone)?**\
   Leaving `spring.json.trusted.packages` as `*` would be the most unacceptable in prod. Relying only on `ProcessedEventStore`'s in-memory set for idempotency is the second unacceptable default, since it forgets everything on restart and doesn't work across multiple instances.
8. **How are event contracts versioned (`eventVersion`, topic `.v1`)?**\
   The topic name carries the version suffix and every event also carries its own `eventVersion` field. A breaking change would need either a new topic version or a documented migration.
