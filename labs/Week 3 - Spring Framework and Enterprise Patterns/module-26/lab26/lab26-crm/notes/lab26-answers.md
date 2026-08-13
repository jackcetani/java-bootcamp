# Lab 26 Answers

## Reflection Questions

1. **Which design decision most affected correctness — YAML split or typed binding?**\
The most crucial design decision was the YAML split. Typed binding adds validation and a single source of truth, but without separate `dev`/`test`/`prod` files, there'd be nothing for the CLI / env overrides to even demonstrate.
2. **Which failure was hardest (missing prop, wrong profile, override confusion)?**\
The binding-mismatch failure from failure experiment 3 is the hardest to diagnose. A correctly-bound value could look identical to a wrong one unless you specifically check the resolved number.
3. **What evidence proves prod cannot start with blank credentials?**\
Failure experiment 1 is evidence of this. In my screenshot, you can see `APPLICATION FAILED TO START` in the console output. This is direct proof of fail-fast behavior for a blank prod password.
4. **What breaks first if dev settings leak into prod?**\
The open H2 console is the main worry and the most exploitable leak. It allows for direct database access through a web UI with no real auth. 
5. **Which concern should move to a shared secrets manager?**\
Credential storage and rotation should move. Plain env vars are fine for this lab, but a real prod system wants secret management and rotation, access auditing, and injection centrally instead of per-deployment env var management.
6. **What must change before real customer data touches prod?**\
The datasource needs to point at an access-controller, backed-up PostgreSQL instance, not the placeholder hostname here. Also, the secrets need to come from a real secrets manager rather than plain env vars set by hand.
7. **How does this lab connect to Labs 25 and 27 (and 43/45)?**\
Lab 25's layered `CustomerService` and `CustomerRepository` stay the same in this lab, which is proof our layering held up. Lab 27 consumes this exact dev/test datasource setup for transactional demos, without reinventing config. Labs 43 and 45 reuse this env var and secrets pattern.
8. **Which /actuator/env or log field matters most for misconfig diagnosis?**\
The `actuator/env` and log field that matters most for misconfig diagnosis is the banner's `"active profile"` line at startup. This is the fastest way to if the instance is running on the expected profile, without digging into any specific property value.
9. **(Forward look) Which keys should Lab 27 refuse to hard-code?**\
Anything related to the datasource should not be hardcoded in Lab 27. This includes connection URLs, credentials, pool sizing, and the `northstar.integration.api-key/timeout-ms`. Lab 27 should consume the exact profile-driven values this lab already made rather than introducing a parallel config source.