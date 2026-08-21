INSERT INTO customer (public_id, full_name, email_normalized, phone, status)
VALUES ('CUS-1001', 'Amina Khan', 'amina@example.com', '+1-555-0101', 'ACTIVE');

INSERT INTO customer (public_id, full_name, email_normalized, phone, status)
VALUES ('CUS-1002', 'Ravi Singh', 'ravi@example.com', '+1-555-0102', 'PROSPECT');

INSERT INTO account (account_number, customer_id, account_type, balance, currency)
SELECT 'ACCT-1001-01', customer_id, 'CHECKING', 2500.00, 'CAD'
FROM customer WHERE public_id = 'CUS-1001';

INSERT INTO address (customer_id, address_type, line1, city, region, postal_code, country_code)
SELECT customer_id, 'HOME', '100 Maple St', 'Toronto', 'ON', 'M5V 2T6', 'CA'
FROM customer WHERE public_id = 'CUS-1001';

INSERT INTO customer_status_history (
    customer_id, old_status, new_status, changed_by, reason, correlation_id
)
SELECT customer_id, 'PROSPECT', 'ACTIVE', 'lab37', 'Activation', 'lab-request-001'
FROM customer WHERE public_id = 'CUS-1001';

COMMIT;