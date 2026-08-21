# Lab 36 — Security decisions

## Token storage

Memory-only `tokenStore` because `localStorage` and `sessionStorage` are directly readable by any script running on the page. A single XSS bug anywhere in the app would let an attacker read a persisted token and impersonate the user indefinitely. A memory-only token is lost on ever page reload, which is a deliberate trade-off. It forces re-authentication more often in exchange for a much smaller theft window.

## 401 vs 403

401 means the token is missing, invalid, or expired. The session itself isn't valid anymore, so `http.ts` clears it and returns the user to `anonymous`. 403 means the token is valid but the account lacks permission for that specific action. Clearing the session here would be wrong, since the user is correctly logged in and just hit a role boundary. The UI should show a forbidden message but let them keep working elsewhere.

## CSRF

Not applicable to this lab. All authenticated CRM calls use a Bearer token in the `Authorization` header, not a cookie. CSRF specifically exploits the browser's automatic cookie-attachment behavior on cross-site requests. A Bearer  token in a custom header is never sent automatically by the browser to any origin, so the attack has no vector here. If this app switched to cookie-based sessions, every unsafe method request would need an `X-XSRF-TOKEN` header validated server-side against the session, since the browser would then auto-attach the session cookie to cross-site requests regardless of origin.