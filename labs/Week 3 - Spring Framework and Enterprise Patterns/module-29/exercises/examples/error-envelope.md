# Exercise 3 — ErrorResponse Envelope

## Goal

Specify `ErrorResponse` fields clients can rely on.

| Field | Purpose |
| --- | --- |
| timestamp | ISO-8601 UTC |
| status | HTTP status code |
| error | Short reason phrase |
| message | Safe human message |
| path | Request path |
| correlationId | e.g. lab-request-001 |
| violations | Optional field errors |