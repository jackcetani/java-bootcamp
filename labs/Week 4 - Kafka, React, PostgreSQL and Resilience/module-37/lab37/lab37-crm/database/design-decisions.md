# Lab 37 — Design decisions

## Cardinalities

`Customer 1 ---- 0..* Account`, `Customer 1 ---- 0..* Address`, `Customer 1 ---- 0..* StatusHistory` are all optional-many, never mandatory. Ravi (`CUS-1002`) has zero accounts and that's a state, not an edge case to work around.

## Delete rules

Default `FOREIGN KEY` behavior is used for `account`, `address`, and `customer_status_history`. Deleting a customer with existing accounts/addresses/history should fail loudly. History rows are also never updated in place. Status changes are always a new `INSERT`, since overwriting a row would destroy the audit trail this table exists to provide.

## public_id vs surrogate key

`customer_id` is a PostgreSQL identity surrogate key. `public_id` (`CUS-1001`) is the actual immutable business identifier used everywhere else. Using `email` or the surrogate key as the external identifier would be wrong because emails change and surrogate keys are private to this table's own storage.

## Constraints

`ck_customer_status`/`ck_account_type`/`ck_account_status`/`ck_address_type` CHECK constraints stop invalid enum-like values at the database layer, independent of the application layer. `uk_customer_public`/`uk_customer_email`/`uk_account_number` UNIQUE constraints prevent duplicates even under concurrent writes, which an application-level "check then insert" can race on. `fk_account_customer`/`fk_address_customer`/`fk_hist_customer` FOREIGN KEYs make an orphaned row structurally impossible, not just discouraged by convention.