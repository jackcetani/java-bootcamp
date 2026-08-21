SELECT public_id, status FROM customer ORDER BY public_id;

SELECT c.public_id, a.account_number, a.balance
FROM customer c LEFT JOIN account a ON a.customer_id = c.customer_id
ORDER BY c.public_id;

SELECT h.customer_id, c.public_id, h.old_status, h.new_status, h.correlation_id
FROM customer_status_history h
         JOIN customer c ON c.customer_id = h.customer_id;