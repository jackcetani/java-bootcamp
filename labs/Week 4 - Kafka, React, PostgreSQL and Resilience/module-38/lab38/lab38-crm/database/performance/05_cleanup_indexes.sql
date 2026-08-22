-- DROP INDEX ... to challenge each index; re-EXPLAIN; restore afterward
DROP INDEX IF EXISTS ix_customer_status_created;

EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_id, public_id, created_at
FROM customer
WHERE status = 'ACTIVE'
ORDER BY created_at DESC, customer_id DESC
    FETCH FIRST 20 ROWS ONLY;

CREATE INDEX ix_customer_status_created
    ON customer (status, created_at DESC, customer_id DESC);
ANALYZE customer;