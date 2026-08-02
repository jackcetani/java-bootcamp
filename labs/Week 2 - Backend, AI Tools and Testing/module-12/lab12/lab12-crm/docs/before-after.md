# Before / After — CustomerService

## Smell -> fix mapping
See `smells.md` for the full 8-row table; every row's fix is described in the
refactored `CustomerService.java` above (typed Map, exceptions not null, `equals`-based
lookup, no magic branch, extracted helpers).

## Method list
Before: `doStuff(String,String,String,String,String)`, `get(String)`
After: `createCustomer(...)`, `getCustomer(String)`, `updateStatus(String,CustomerStatus)`,
plus private helpers `requireNonBlank`, `requireUniqueId`, `requireExisting`.

## Test output excerpt
Early run (target API tests against messy baseline): 5 tests failed — API didn't exist.
After refactor: `mvn -q clean test` -> `Tests run: 5, Failures: 0` (this file's 5 tests,
plus whatever count carries from Lab 11) -> `BUILD SUCCESS`.

## Manual demo transcript
Amina: Amina Khan
Ravi status: ACTIVE
expected: duplicate customerId: CUS-1001 correlationId=lab-request-001
expected: Customer not found: CUS-9999 correlationId=lab-request-001