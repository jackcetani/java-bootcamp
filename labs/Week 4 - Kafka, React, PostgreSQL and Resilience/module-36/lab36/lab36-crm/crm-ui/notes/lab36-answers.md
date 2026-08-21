# Lab 36 Answers

## Reflection Questions

1. **Which design decision most affected correctness?**\
The most crucial design decision in this lab was making 401 and 403 branch differently in `http.ts`. A shared handler that logged the user out on any 4xx would turn an authz flag into an unnecessary full re-login, which is exactly the UX bug step 7 plans to prevent.
2. **What evidence proves the implementation works?**\
The Application tab showing empty storage after login and the Network tab showing `Authorization` only on same-origin requests and never on the third-party test call. Together they prove memory-only tokens and origin-scoped bearer with real browser evidence, not just passing tests.
3. **Which failure was hardest to diagnose?**\
Getting the "checking" state to mean something real was the hardest. Since the token never survives a reload, it's easy to write code where `checking` resolves so fast it might as well not exist. The guide's warning about defaulting to `authenticated` is exactly the trap a boolean-based `isAuthenticated` state falls into.

