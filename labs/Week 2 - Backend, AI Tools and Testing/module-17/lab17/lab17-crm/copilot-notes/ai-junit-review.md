# AI Junit Review

## lab17-001 — Copilot-drafted duplicate-email test
- Created manually: copilot unavailable
- Checklist:
    1. Can every assert fail if production regresses? Yes, once tightened to the specific type.
    2. Shared CRM fixture IDs? Yes, CUS-1001/CUS-1002.
    3. No phantom Spring/JPA imports? Confirmed none.
    4. Independent `@BeforeEach`? Yes, fresh repo/service each test.
    5. `mvn -q test` after edits? Green.
- Verdict: Manually created and reviewed → accept. Made sure to use `IllegalStateException.class`
  before moving on, since the broad type would have silently passed even if the wrong
  exception type were thrown.