# Code smells — Lab 12

Catalog **≥8** smells from the messy baseline (`doStuff`). Tie each to CRM impact (CUS-1001).

| # | Smell                                | Location                                                                            | Impact on CUS-1001                                                                                                                   |
|---|--------------------------------------|-------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| 1 | Poor naming (`doStuff`, `data`)      | `CustomerService.before.java.txt` - the `doStuff` method signature                  | Support engineer can't tell what this method does without reading every line                                                         |
| 2 | Raw types                            | field declaration                                                                   | No compile-time safety. Anythign can ho in, casts can fail at runtime.                                                               |
| 3 | Long method / mixed responsibilities | the whole body of `doStuff`                                                         | create + update jammed into one method via a name-based hack                                                                         |
| 4 | Stringly-typed status                | inside `doStuff`, the status-assignment if/else chain                               | Type of "Active: silently falls through to the `else` default. No compiler help.                                                     |
| 5 | Incorrect equality (`==`)            | the `get(String id)` method                                                         | Lookup for Amina fails if the ID string wasn't the exavt same object reference.                                                      |
| 6 | Null as control flow                 | both `return null` lines in `doStuff` and one also in `get`                         | Every caller must remember to null-check or risk an NPE two calls later.                                                             |
| 7 | Side-effect logging                  | the three print statements in `doStuff`                                             | No severity, no structure, unusable for real log aggregation.                                                                        |
| 8 | Magic `"UPDATE"` behavior            | the trailing `if (b != null && b.contains("UPDATE"))` block at the end of `doStuff` | A customer literally named "UPDATE Khan" would trigger unintended behavior that is completely undiscoverable without reading source. |
