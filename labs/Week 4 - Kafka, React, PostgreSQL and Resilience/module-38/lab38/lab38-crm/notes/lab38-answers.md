# Lab 38 Answers

## Reflection Questions

1. **Which design decision most affected correctness of page results?**\
The must crucial design decision was including `customer_id` as a tie-breaker in every `ORDER BY`. Without it, rows sharing the same `created_at` value have no guaranteed stable order, which would silently duplicate or skip rows across paginated requests.
2. **What evidence proves the email index was worth the write cost?**\
The direct before/after `EXPLAIN (ANALYZE, BUFFERS)` comparison is evidence the email index was worth the write cost. A `Seq Scan` with real buffer counts in the hundreds dropping to a handful of buffers on an `Index Scan` for the identical query and bind value, captured in the same session.
3. **Which failure was hardest to diagnose (wrong plan, skew, ties)?**\
A `DROP INDEX`/`CREATE INDEX` hanging indefinitely with no error message would be the hardest failure to diagnose. There terminal just sits there and nothing in the output tells you why. The real cause is usually another session holding an open transaction on the same table, which blocks the DDL from acquiring the lock it needs. 

