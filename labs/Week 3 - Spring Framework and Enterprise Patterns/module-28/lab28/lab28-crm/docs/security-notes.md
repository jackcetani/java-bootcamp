# Lab 28 — Security notes

## Concepts to Discuss

1. Main request flow for login versus an authenticated customer read
   Logging in posts credentials once and gets a JWT back. Every call after sends that same JWT as a `Bearer` header and `JwtAuthenticationFilter` verifies it.
2. Trust boundary: credentials at login, signature/expiry on every Bearer request
   Credentials are only trusted at the login endpoint. Every other request trusts nothing but the JWT signature and expiry.
3. Success/failure contracts: 401 vs 403 vs 200
   401 means a bad token or login credentials. 403 means the token is good, but the user is unauthorized to make the call. 200 means both authn and authz passed, so success.
4. Stable identity (`sub` / username) versus customer IDs (`CUS-1001`)
   The JWT's `sub` identifies the caller, while the customer ID identifies the record being accessed. These are two completely different things and mixing them up is a security risk.
5. Idempotency: login vs `GET` with a bearer token; why refresh tokens are a production topic
   GET with a valid token is safe to retry always. Login issues a new token each time rather than returning the same one, so production systems add a separate refresh-token flow instead of redoing the whole login every time a token is about to expire.
6. Local shortcut (in-memory users, HS256 shared secret) versus production (IdP, JWKS, rotation)
   Using a single, shared HS256 secret and in-memory users is alright for this lab, but production needs a real identity provider and key management. None of these are in this lab.
7. Evidence operators need (failed-login rate, 401/403) without logging raw tokens or passwords
   One evidence operators would need are the failed-login and 401/403 rates being safe and useful. Another is that raw JWT and plaintext secrets never appear in any log line.
8. Two app instances: shared JWT secret / JWKS so both accept the same tokens
   Both instances need to be configured with the same `JWT_SECRET` value so the token issued by one instance matches the other. Failure experiment 1 shows what happens if they don't match.
9. Why CSRF disable is acceptable for a pure Bearer API and when it would not be
   A CSRF-disable is fine here because exploits rely on a browser catching cookies. A stateless Bearer API never relies on cookies for auth.
10. What Lab 29 will change (error bodies) without rewriting role names or fixture IDs
    Lab 29 will add a unified `ErrorResponse` JSON body for validate failures. It won't touch the role names or fixture IDs, and also doesn't change how 401/403 work already.

## Production Checklist (Step 9)

- Replace in-memory `UserDetailsService` with a real IdP (OAuth2/OIDC provider)
- Replace the shared HS256 secret with RSA/ECDSA + JWKS-based key distribution
- Rotate signing keys on a schedule; support graceful rollover (old + new key both valid during transition)
- Rate-limit failed login attempts per account/IP
- Never log raw Bearer tokens or plaintext passwords, at any log level
- Define and implement a refresh-token flow so expiry doesn't force disruptive relogin
- Document token TTL and the operational plan for revoking a compromised token before natural expiry

