# Lab 41 Answers

## Reflection Questions
1. **Which design decision most affected image safety/size?**\
The design decision that most affected image safety/size was using a genuine multi-stage build. Maven and the full JDK never ship in the runtime image at all, only the JRE and the final jar. Combined with running as non-root UID 10001, a container compromise can't escalate to root on the host the way it could with a single-stage, root-run image.
2. **What evidence proves non-root + readiness?**\
`docker exec crm-lab41 id` showing `uid=10001(spring)` is direct proof of the non-root claim. Just reading the Dockerfile and trusting it took effect is not enough. Readiness is proven the same way with an actual `curl` against `/actuator/health/readiness` returning `UP`, not just `docker ps` showing the container running.
3. **Which failure was hardest to diagnose (network vs health vs perms)?**\
Getting the JDBC hostname right took me a while to realize. The guide assumes the container and network name follows `crm-postgres`/`lab37-crm_default`, but that didn't match my real environment at all. My actual Postgres container is named `lab39-postgres`. Using `docker network ls` and `docker inspect` resolved the problem.

