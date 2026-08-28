# Lab 45 Answers

## Reflection Questions

1. **Which design decision most affected safety (contract vs AI draft)?**\
The most crucial design decision for safety was writing the contract's forbidden list **before** prompting. The AI draft genuinely did suggest a publicly-exposed database port, and having an explicit, pre-written rule against it made rejecting that suggestion a fast, obvious call instead of a judgment call made under pressure.
2. **What evidence proves you read the plan, not only validated syntax?**\
The rejected `LoadBalancer` resource in `ai-aic-review.md` is evidence that proves I read the plan, not just validated syntax. `validate` alone would have passed that resource as syntactically fine, since it's a valid HCL. Only actually reading what the resource does (expose port 5432 publicly) caught the real problem.
3. **Which failure was hardest to diagnose?**\
Recognizing that a genuinely idempotent run rehearsal wasn't possible at all in this env (no authroized disposable host). The honest fix wasn't a workaround, but documenting the real limitation as a residual risk rather than quietly skipping the requirement.


