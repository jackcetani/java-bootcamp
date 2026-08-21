# Lab 37 — PostgreSQL notes

## Least-privilege app user

`crm_app` can `CONNECT` and `CREATE`/`USAGE` on the `public` schema only — no `SUPERUSER`, no `CREATEDB`. The browser never touches this database directly, only the Spring backend does (running as `crm_app`).
