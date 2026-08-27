# Lab 43 Answers

## Reflection Questions
1. **Which design decision most affected correctness (cache, image pin, or package-once)?**\
The most crucial design decision was the highest-leverage decision. Without it, whether the pipeline passed and what's actually running in prod could silently diverge if a deploy step ever rebuilt from source instead of consuming the exact artifact that was tested.
2. **What evidence proves the JAR matches the commit?**\
The `SHA256SUMS` containing both the hash and `commit=${GITHUB_SHA}` in the same uploaded artifact is evidence the JAR matches the commit. Anyone downloading the JAR later can independently verify it against that recorded commit and hash, rather than trusting an unverifiable claim.
3. **Which failure was hardest to diagnose?**\
Reconciling the guide's assumption about my project tree was difficult to work around, but I got it figured out eventually. The fix wasn't a bug, it was recognizing the guide's own `cp -r lab42-crm || cp -r lab41-crm` fallback already anticipated exactly this kind of divergence.
