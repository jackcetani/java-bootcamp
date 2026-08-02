# AI review notes — Lab 10

## lab10-001 — weak vs strong (entity)
- Date: 7/30/2026
- Weak prompt used: `// customer class`
- Output summary: Copilot invented its own field set (guessed at `id`, `name` — not
  matching our actual `customerId`/`fullName` naming), no `equals`/`hashCode` contract,
  no clear status type. Not usable as-is.
- Strong prompt used: full field/type/format spec from Step 3's table (customerId as
  "CUS-1001"-style String, fullName, email, phone, CustomerStatus enum, createdAt,
  equals/hashCode on customerId only).
- Output summary: matched the target shape closely — correct field names, correct
  equals/hashCode scoping. Had to manually strip a `@Entity` annotation Copilot added
  unprompted (see lab10-003).
- Decision: accept (strong prompt version, after removing the JPA annotation)
- Reason: strong prompt encoded exact naming/rules, which is what stopped Copilot from
  guessing its own structure.

## lab10-002 — weak vs strong (addCustomer)
- Date: 7/30/2026
- Weak prompt used: `// addCustomer method`
- Output summary: produced a bare `add(Customer c) { list.add(c); }` — no validation
  at all, no duplicate check, nothing matching our actual business rule.
- Strong prompt used: full rule spec from Step 3 row 2 (reject null/blank ID, reject
  duplicate ID with IllegalStateException, otherwise store and return).
- Output summary: matched almost exactly — guard clauses in the right order, correct
  exception type.
- Decision: accept
- Reason: putting the rules directly in the prompt got guard clauses instead of only a
  happy path.

## lab10-003 — CustomerStatus / Customer scaffold
- Rejected JPA? yes 
- Notes:
1. Every import resolves against pom.xml deps actually present (no phantom JPA/Spring
   imports) — Fail initially (Copilot added `import jakarta.persistence.Entity;` and
   `@Entity`/`@Id` on `Customer` unprompted), Pass after manually deleting both the
   import and the annotations.
2. Business rules from the prompt appear in code (blank ID rejected, duplicate ID
   rejected, unknown ID rejected) — Pass, verified against `CustomerService` directly.
3. equals/hashCode based on customerId only — Pass.
4. Could explain every accepted line with Copilot closed — Pass.
5. No hardcoded secrets/real PII — Pass, only CUS-1001/CUS-1002 used throughout.

Worked example: caught and removed `@Entity`/`@Id` from `Customer.java` — this project
has no JPA on the classpath (Lab 9's pom.xml only has spring-context + junit-jupiter),
so those annotations would not even compile, let alone belong here architecturally.


## lab10-004 — CustomerService review
- Notes:
1. Real customer data avoided in Chat: never typed anything resembling a real name,
   email, or ID. Every prompt and every test used only CUS-1001/CUS-1002 (Amina
   Khan / Ravi Singh), which are this bootcamp's fixed test examples.
2. If a suggestion looks copied verbatim from a known library/article, I don't accept it
   blindly. I reread it like unfamiliar code from a coworker, and if it still looks
   suspiciously like a lift from somewhere specific (unusual variable names, comments
   that don't match our style), rewrite it in our own words/structure rather than
   pasting it as-is.
3. A team rule for code you don't fully understand is don't merge it. Same goes for
   if a human teammate handed you code and couldn't explain it either.
