# Lab 44 Answers

## Reflection Questions
1. **Which design decision most affected correctness (digest vs tag)?**\
The most crucial design decision was pinning to a real digest/checksum instead of `:latest`. Failure experiment 4 shows directly how `:latest` can't tell us exactly which bits are running, which is exactly the question a rollback needs answered under pressure.
2. **What evidence proves staging and prod candidate are the same bits?**\
The same `jarSha256` value in `artifact-manifest.json` matching a fresh `sha256sum` of the deployed JAR. Also, reusing the identical image tag/digest across every promotion step in this lab rather than rebuilding. No step after the one-time local build ever ran `docker build` again.
3. **Which failure was hardest to diagnose?**\
Realizing my real lab 43 CI never actually produces a container image at all took me a while to figure out. It's not something you'd notice until you actually tried to promote by digest and had nothing real to promote. The fix wasn't a bug fix, but it was recognizing a genuine gap in the pipeline built two labs ago and disclosing it honestly rather than quietly working around it.
