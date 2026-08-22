-- generate >= 50k customers with skewed status distribution
-- keep fixture public_id CUS-1001 / CUS-1002 intact if already seeded
INSERT INTO customer (public_id, full_name, email_normalized, status, created_at)
SELECT
    'CUS-BULK-' || LPAD(i::text, 6, '0'),
    'Bulk Customer ' || i,
    'user' || i || '@example.test',
    CASE WHEN i % 10 < 7 THEN 'ACTIVE' ELSE 'PROSPECT' END,
    CURRENT_TIMESTAMP - ((i % 90) || ' days')::interval
FROM generate_series(1, 50000) AS s(i);

INSERT INTO customer (public_id, full_name, email_normalized, phone, status)
SELECT 'CUS-1001', 'Amina Khan', 'amina@example.com', '+1-555-0101', 'ACTIVE'
    WHERE NOT EXISTS (SELECT 1 FROM customer WHERE public_id = 'CUS-1001');

INSERT INTO customer (public_id, full_name, email_normalized, phone, status)
SELECT 'CUS-1002', 'Ravi Singh', 'ravi@example.com', '+1-555-0102', 'PROSPECT'
    WHERE NOT EXISTS (SELECT 1 FROM customer WHERE public_id = 'CUS-1002');

SELECT status, COUNT(*) FROM customer GROUP BY status ORDER BY status;