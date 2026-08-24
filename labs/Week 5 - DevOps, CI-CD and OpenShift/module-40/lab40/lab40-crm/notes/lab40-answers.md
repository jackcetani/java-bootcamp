# Lab 40 Answers

## Reflection Questions

1. **Which design decision most affected correctness of the security gate?**\
The most crucial design decision was pinning `dependecy-check.version` to `10.0.4` rather than `LATEST`. An unpinned scanner could pass or fail the same codebase differently as the plugin itself changes over time, which defeats the purpose of a reproducible release gate.
2. **What evidence proves the remediation worked?**\
A before and after comparison of the same scan command against the same codebase is evidence the remediation worked. Also, `mvn -B verify` proving the app and its integration test still worked correctly on both sides of the upgrade, not just that the scan happened to change.
3. **Which failure was hardest to triage (tool noise vs real bug)?**\
Distinguishing fixable findings vs ones that simply can't be fixed yet was difficult in this lab. `tomcat-embed-core:10.1.57` is the newest available patch and still carries CVE-2026-66299; there's no "upgrade further" option. Accepting it with an owner and expiry rather than chasing a fix that doesn't exist was the right call.
