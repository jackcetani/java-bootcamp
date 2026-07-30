## Exercise 4 — equals() vs ==:

### Goal

Document when == is wrong for status strings and customer ids.

| Check | Use | Why |
| --- | --- | --- |
| status ACTIVE? | Objects.equals / enum | String identity is unsafe |
| same Customer instance? | == | Reference equality only |
| id CUS-1001? | equals | Value equality |

**Bad Line:** `if (status == "ACTIVE")` --> FAIL\
**Good Line:** `if (Objects.equals(status, "ACTIVE"))` or `if (statusEnum == Status.ACTIVE)` --> PASS

Note: prefer enums on JDK 21 sketches when status set is closed.