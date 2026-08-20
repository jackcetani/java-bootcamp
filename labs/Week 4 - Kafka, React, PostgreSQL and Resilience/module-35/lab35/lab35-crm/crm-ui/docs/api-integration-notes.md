# Lab 35 — API integration notes

## Request flow

A UI event (search keystroke, form submit, mount) calls into `customersApi`, which calls `request()` in `http.ts`, which does the actual `fetch` against `VITE_CRM_API_URL`. The response (or thrown `ApiError`) flows back up and `App` turns it into one of `loading`/`data`/`error` state, which the render branches on directly. No intermediate caching layer.

## CORS

Local dev: Vite serves the SPA on `http://localhost:5173`; Spring's `WebConfig` allowlists exactly that origin for `/api/**`, nothing else. Production would allowlist the real deployed frontend origin(s) instead. Never `*`, since a wildcard origin combined with credentialed requests is a known CORS security hole, not just a convenience shortcut.

## Concepts to Discuss

1. **Main request flow (UI event → customersApi → Spring → UI state)**\
   UI event --> `customersApi` method --> `request()` --> Spring --> response parsed or thrown as `ApiError` --> `App` state updates --> re-render.
2. **Trust boundary: browser never trusted; server validates again**\
   Every request is treated as coming from an untrusted client. Spring re-validates on every write regardless of what the React form checked.
3. **Success/failure contracts (2xx body vs ApiError kinds)**\
   A 2xx response body is the real `Customer` / `Customer[]` shape. Any non-2xx becomes one normalized `ApiError` with `status`/`code`/`message`, callers never have to branch on raw response shapes themselves.
4. **Stable identity: server `customerId` after create**\
   Before this lab, `customerId` was a client-generated UUID. After this lab, it's whatever the server assigned in it's `201` response. The client stops inventing IDs entirely once the server is the real source of truth.
5. **Retry/idempotency: disable duplicate POST; safe GET retry**\
   `GET /customers` is safe to retry any number of times. `POST /customers` is not, which is why `saving` disables the Save button synchronously on first click rather than trusting a slower state update to land in time.
6. **Local CORS shortcut vs production allowlist/CDN origins**\
   the `localhost:5173` is a `localhost` shortcut appropriate for this lab. A real deployment would allowlist real frontend origins.
7. **Evidence: Network waterfalls + correlation header**\
   The Network tab showing `X-Correlation-Id: lab-request-001` on a create/update request is what proves the request actually carried the header. Console logs alone don't prove what left the browser.
8. **Two SPA tabs: abort/race behavior; last-write wins without ETags yet**\
   Each browser tab has its own `AbortController` and load cycle. Without ETags or optimistic locking, two tabs editing the same customer would have the second save silently overwrite the first.
9. **False confidence: swallowing errors into empty list**\
   If a failed `list()` call were caught and silently turned into an empty `Customer[]`, the UI would show "No customers yet" instead of "the API is down". It's indistinguishable from a real empty state and actively misleading.
10. **What Lab 36 adds (tokens) without rewriting ApiError shape**\
    Lab 36 wraps `request()` to attach a bearer token and affs origin-scoped guards. It does not fork `http.ts` into a second file, and `ApiError`'s shape stays the exact same.

