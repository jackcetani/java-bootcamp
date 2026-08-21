BEGIN;
SAVEPOINT negative_test;

-- invalid status — expect SQLSTATE 23514 (check_violation)
INSERT INTO customer (public_id, full_name, email_normalized, status)
VALUES ('CUS-X', 'Bad Status', 'bad@example.com', 'UNKNOWN');

ROLLBACK TO SAVEPOINT negative_test;
SAVEPOINT negative_test;

-- duplicate email — expect SQLSTATE 23505 (unique_violation)
INSERT INTO customer (public_id, full_name, email_normalized, status)
VALUES ('CUS-DUPE', 'Dupe', 'amina@example.com', 'PROSPECT');

ROLLBACK TO SAVEPOINT negative_test;
SAVEPOINT negative_test;

-- orphan account FK — expect SQLSTATE 23503 (foreign_key_violation)
INSERT INTO account (account_number, customer_id, account_type, balance)
VALUES ('ACCT-ORPHAN', 999999, 'CHECKING', 0);

ROLLBACK TO SAVEPOINT negative_test;
COMMIT; -- no net change