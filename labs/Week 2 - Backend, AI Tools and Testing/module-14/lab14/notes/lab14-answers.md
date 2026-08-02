## Lab 14 Answers:

### Concepts to Discuss
1. **Main data/request flow (facade → validate → map → service → response DTO)**\
2. **Trust boundary and input validation point**\
3. **Success and failure contract (validation vs duplicate ID vs not found)**\
4. **Stable identity (CUS-1001) vs mutable display fields**\
5. **Retry/idempotency implications at the DTO boundary**\
6. **Local programmatic Validator vs Spring @Valid later**\
7. **Logs/evidence for support (lab-request-001 on failures)**\
8. **Behavior with two application instances (independent memory)**\
9. **Why response DTOs should prefer getters-only / factory methods**\
10. **What must never appear on a response DTO (password hashes, internal flags)**\