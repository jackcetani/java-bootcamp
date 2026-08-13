# Lab 26 starter — timed path (~45 minutes)

**Theme:** Spring profiles — dev/test/prod YAML, ConfigProperties, secret hygiene

## Activity card

| | |
| --- | --- |
| **Objective** | Complete profile YAML + NorthstarIntegrationProperties; prove activation |
| **Skills practiced** | Profiles, override awareness, env placeholders, fail-fast prod |
| **Expected outcome** | dev smoke · prod refuses missing secrets · no secrets in Git |
| **Estimated time** | ~45 minutes |
| **Files** | `examples/lab26-crm/` copied from this starter |

**Boilerplate reduced:** Profile stubs + `// TODO` — keep Lab 25 layering.

Pacing: [`../../PACING.md`](../../PACING.md) · Full steps: [`../LAB-26-GUIDE.md`](../LAB-26-GUIDE.md)

## Copy into your workspace

Do **not** grade work only inside the course `labs/` clone. Copy this `starter/` into your bootcamp examples tree as `lab26-crm`.

**Windows (PowerShell)** — from this lab folder:

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\java-bootcamp\examples\lab26-crm" | Out-Null
Copy-Item -Recurse -Force ".\starter\*" "$env:USERPROFILE\java-bootcamp\examples\lab26-crm\"
cd $env:USERPROFILE\java-bootcamp\examples\lab26-crm
```

**macOS / Linux:**

```bash
mkdir -p ~/java-bootcamp/examples/lab26-crm
cp -R starter/. ~/java-bootcamp/examples/lab26-crm/
cd ~/java-bootcamp/examples/lab26-crm
```

Full GUIDE: [`../LAB-26-GUIDE.md`](../LAB-26-GUIDE.md)

## 45-minute checklist

- [ ] Complete base `application.yml` (`name: northstar-crm`, `api-base-url`, `connect-timeout-ms: 2000`) and three profile files (`lab26dev` / `lab26test` H2 URLs)
- [ ] Fill mutable `NorthstarIntegrationProperties` + `@ConfigurationProperties` binding
- [ ] Prove activation via `-Dspring.profiles.active` / env (`docs/profile-notes.md`)
- [ ] Ensure `.env.example` only (`DB_USERNAME` / `DB_PASSWORD` / `NORTHSTAR_API_KEY`) — no real secrets committed
- [ ] Add `ProfileBindingTest` (starter ships **0** tests; expect **Tests run: 1**)
- [ ] Smoke under `dev`: GET CUS-1001 still works

## Smoke test

```bash
# After adding ProfileBindingTest:
mvn -B test -Dspring.profiles.active=test
# Tests run: 1
# PowerShell: quote the -D argument
mvn -B spring-boot:run "-Dspring-boot.run.profiles=dev"
# Fail-fast check: mvn -B spring-boot:run "-Dspring-boot.run.profiles=prod"
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-26/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| `dev` profile starts with H2-friendly settings | Pass / Fail |
| `prod` fails to start without env secrets / Postgres stack (see GUIDE Step 4) | Pass / Fail |
| Profile YAML files present for dev/test/prod | Pass / Fail |
| No real secrets in Git | Pass / Fail |
| CUS-1001 smoke under `dev` | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.

## Manual Verification
| Test                                                                                                                             | Result |
|----------------------------------------------------------------------------------------------------------------------------------|--------|
| dev starts; active profile banner shows dev.                                                                                     | PASS   |
| test profile runs Quiet/CI-friendly tests.                                                                                       | PASS   |
| prod without env vars fails startup (no blank-password connect).                                                                 | PASS   |
| With env vars supplied (fake lab values), prod start either connects or fails for driver/network — not for missing placeholders. | PASS   |
| CLI -D overrides env for the same key (evidence recorded).                                                                       | PASS   |
| application.properties gone (YAML only).                                                                                         | PASS   |
| GET CUS-1001 under dev with correlation works.                                                                                   | PASS   |
| /actuator/env exposure is tighter in prod YAML intent.                                                                           | PASS   |
| Two consecutive tests under test match.                                                                                          | PASS   |
| git status shows no secrets / .env.                                                                                              | PASS   |


## Security and Production Review

1. **Which config values are sensitive per profile, and where stored?**\
`DB_USERNAME`, `DB_PASSWORD`, and `NORTHSTAR_API_KEY` are all sensitive in `prod`. They're never stored in any committed file, only referenced via `${VAR}` placeholders resolved from env vars at runtime.
2. **Why must application-prod.yml avoid defaults for DB username/password?**\
A default DB username or password causes a silent blank-credential connection attempt instead of failing loudly. This is not a good practice in any true business service, and failure experiment 1 proves this doesn't happen for this reason.
3. **What if a real PostgreSQL password is committed — detect, rotate, scrub history policy?**\
If a real PostgreSQL password is committed, you can detect it with `git log -p` or a secret scanning tool on the affected file. Then, rotate the actual database credential immediately, as the committed value must be treated as compromised no matter what. Then, scrub git history with `git filter-repo` only AFTER rotation.
4. **Which local-only settings are unacceptable in prod (H2 console, ddl-auto: update, verbose SQL)?**\
Three local-only setting unnacceptable in `prod` are the open H2 console, `ddl-auto: update`, and the verbose SQL logging. All three should be `dev` and `test` ONLY, signified in this lab's YAML and purposely excluded from `application-prod.yml`.
5. **How do Labs 43/45 relate to these env vars?**\
The pattern for `DB_USERNAME`, `DB_PASSWORD`, and `NORTHSTAR_API_KEY` will stay the same and is what Lab 43's CI pipeline secrets and Lab 45's secret injection both build on. It is the same mechanism, only the place values get set differs.
6. **What does /actuator/env expose and why restrict in prod?**\
It exposes every resolved property and its source, and in a misconfigured setup, possible secret values. `application-prod.yml` restricts exposure to `health` only to specifically prevent this endpoint from becoming a security risk.
7. **How do you rotate NORTHSTAR_API_KEY without rebuild?**\
Since `NORTHSTAR_API_KEY` is injected purely via env vars at runtime, rotating it is just updating the env var in the deployment platform and restarting the process. No code changes or rebuilds are necessary.
8. **Blast radius if SPRING_PROFILES_ACTIVE unset in real deployment?**\
This is what failure experiment 5 hopes to prove. The app would silently start in `dev`, meaning the H2 console would be exposed, in-memory data is lost on restart, and verbose debug logging. All of these are unacceptable in a prod environment.