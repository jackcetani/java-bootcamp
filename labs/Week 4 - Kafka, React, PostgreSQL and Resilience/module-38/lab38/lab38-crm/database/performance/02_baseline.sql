ANALYZE customer;

-- EXPLAIN (ANALYZE, BUFFERS) email lookup for a known address
EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_id, public_id, full_name, status
FROM customer
WHERE email_normalized = 'user000001@example.test';

-- EXPLAIN list ACTIVE customers ORDER BY created_at, customer_id LIMIT 50 OFFSET 0
EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_id, public_id, created_at
FROM customer
WHERE status = 'ACTIVE'
ORDER BY created_at DESC, customer_id DESC
    LIMIT 50 OFFSET 0;