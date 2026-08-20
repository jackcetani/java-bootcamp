# Backend CORS checklist (sibling Spring app)

Allow `http://localhost:5173` for timed-path demos — configured in `lab32-crm`'s new `WebConfig.java` (see below), restricting `/api/**` to exactly that origin.

Do not use `*` with credentials — a wildcard origin combined with credentialed requests is a real CORS security hole, not just a stricter-than-necessary default. `WebConfig.java` allowlists one explicit origin, never `*`.