# Lab 26 — Config notes

## Override-order measurements (Step 6)

Ran three separate `mvn spring-boot:run` sessions against `GET /actuator/env/northstar.integration.timeout-ms`

| Layer | Source | Command | Effective timeout-ms |
| ----- | ------ | ------- | --------------------- |
| Profile YAML | `application-test.yml` | `mvn spring-boot:run "-Dspring-boot.run.profiles=test"` (no env vars set) | 500 |
| Env var | `NORTHSTAR_INTEGRATION_TIMEOUT_MS` | `$env:SPRING_PROFILES_ACTIVE="test"; $env:NORTHSTAR_INTEGRATION_TIMEOUT_MS="9999"; mvn spring-boot:run` | 9999 |
| CLI | `commandLineArgs` | Same env vars still set, plus `mvn spring-boot:run "-Dspring-boot.run.arguments=--northstar.integration.timeout-ms=1234"` | 1234 |

Confirmed CLI beats env beats profile YAML beats base YAML. All four tiers
were visible simultaneously in the Measurement 3 response, with `commandLineArgs`
winning at 1234.

## Concepts to Discuss

1. **Why CLI beats env, and env beats profile YAML**\
   Every layer becomes progressively broader and more permanent in scope. CLI arhs are set per-invocation, so they are the most specific and temporary. Env vars are broader, and YAML ships with the code, thus it is the broadest and least specific to one run.
2. **When .properties vs YAML nesting pays off**\
   Flat `.properties` is fine for a few keys, but YAML's nesting is better when you have grouped settings like `northstar.integration.*`. The visual grouping makes the relationship between keys obvious instead of relying on a shared prefix string.
3. **What “active profile” means if two profiles set the same key**\
   Whichever profile is listed last in `--spinrg.profiles.active=a,b` wins for a shared key. However, this lab never has us activate two profiles at once, so this ambiguity is avoided.
4. **Why prod passwords must never default in YAML**\
   A default password in YAML like `${DB_PASSWORD:}` silently resolve tan an empty string instead of failing. Thus, a misconfigured prod deployment would attempt to connect to the database with the blank password instead of refusing to start, which is considerably worse than a loud failure.
5. **@Value vs @ConfigurationProperties for northstar.integration**\
   `@Value` scattered across classes has no single source of truth and no validation. `NorthstarIntegrationProperties` binds the whole group and validates it with `@NotBlank` and `@Positive`. It will fail fast with a named field instead of a late `NullPointerException` somewhere els.e
6. **Why missing required props fail startup instead of silent nulls**\
   A silent null surfacing at runtime is a confusing error that is likely far from its actual cause. Failing at startup means the problem is caught before the app ever accepts a single request. This is a best practice.
7. **Evidence: startup banner, /actuator/env, exit codes**\
   First piece of evidence is the banner's `"The following profile is active"` line, proving which profile is loaded. `actuator/env` shows the actual resolved property values and their source. Also, a non-zero exit code on a fail-fast startup is itself evidence for a CI pipeline.
8. **Link to Labs 43/45 secrets injection**\
   The environment variable pattern we've built here is the same as Lab 43's CI pipeline variables and Lab 45's secret injection both reuse. Nothing about the pattern changes, but where the env vars get set from does.
9. **Why /actuator/env exposure differs in prod**\
   In dev/test it's a goof way to verify what's actually loaded. In prod on the other hand, it can leak internal config structure and even values if misconfigured to anyone who can reach the endpoint. `application-prod.yml` restricts this exposure to just `health` for this reason.
10. **What Lab 27 needs from your datasource profile split**\
    Lab 27's transactional demos needs a real dev and test datasource already resolvable without secrets. This lab provides that so it can focus entirely on `@Transactional` behavior instead of also having to solve environment configuration from scratch.
