# Lab 42 Answers

## Reflection Questions
1. **Which design decision most affected traffic safety (which probe)?**\
The most crucial design decision most affecting traffic safety was keeping readiness and liveness on separate probe paths. Startup/readiness on `/actuator/health/readiness` gates traffic during slow Flyway migration, while liveness of `/actuator/health/liveness` means a transient DB blip sheds traffic instead of triggering an unnecessary pod restart.
2. **What evidence proves rollback worked?**\
The `kubectl rollout history` showing a second revision after the bad `set image`, followed by `rollout undo` returning the deployment to a `Ready` sate and the Step 8 curls succeeding again. The combination of revision history and a working smoke test after undo is what actually proves recovery.
3. **Which failure was hardest to diagnose from events/logs (pull vs probes vs JDBC)?**\
The `CRM_DB_PORT` mismatch was the hardest to diagnose for this lab. The guide anticipated `crm_app` vs `crm` username problem, but my environments correct container `lab39-postgres`, was also on a different port (5433, not 5432). Thus, I needed to adapt beyond what the guide's troubleshooting suggested.

