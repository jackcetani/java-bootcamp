-- replace TRUNC(created_at) filters with half-open tstz range
EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_id FROM customer
WHERE created_at::date = DATE '2026-07-01';

EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_id FROM customer
WHERE created_at >= TIMESTAMP '2026-07-01 00:00:00'
  AND created_at <  TIMESTAMP '2026-07-02 00:00:00';

-- deep OFFSET page, for direct comparison against the keyset query below
EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_id, public_id, created_at
FROM customer
WHERE status = 'ACTIVE'
ORDER BY created_at DESC, customer_id DESC
OFFSET 5000 ROWS FETCH NEXT 20 ROWS ONLY;

-- keyset page: WHERE (created_at, customer_id) < ($ts, $id) ORDER BY ... LIMIT 50
EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_id, public_id, created_at
FROM customer
WHERE status = 'ACTIVE'
  AND (created_at, customer_id) < (TIMESTAMP '2026-07-01 00:00:00', 999999999)
ORDER BY created_at DESC, customer_id DESC
    FETCH FIRST 20 ROWS ONLY;

-- compare nested loop vs hash join for customer -> account
EXPLAIN (ANALYZE, BUFFERS)
SELECT c.public_id, a.account_id, a.balance
FROM customer c
         JOIN account a ON a.customer_id = c.customer_id
WHERE c.public_id = 'CUS-1001';

EXPLAIN (ANALYZE, BUFFERS)
SELECT c.public_id, a.account_id, a.balance
FROM customer c
         JOIN account a ON a.customer_id = c.customer_id
WHERE c.status = 'ACTIVE';