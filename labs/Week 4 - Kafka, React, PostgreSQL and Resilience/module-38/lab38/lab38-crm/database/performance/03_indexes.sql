-- UNIQUE index on customer.email_normalized (if not already)
CREATE UNIQUE INDEX ux_customer_email_norm ON customer (email_normalized);

-- index supporting (status, created_at, customer_id) list queries
CREATE INDEX ix_customer_status_created
    ON customer (status, created_at DESC, customer_id DESC);

ANALYZE customer;