# AI-review-notes - Lab 12

## lab12-001 — extract-method pass
- Prompt: "extract the duplicate-ID check in CustomerService into a helper method"
- Suggestion summary: Copilot proposed `requireUniqueId(String)`, matching what Step 4
  already specifies — accepted as-is, no edits needed.
- Verdict: accept
- Risk caught: none this time, but reviewed for stray Spring/JPA imports on principle
  (none present) before accepting.