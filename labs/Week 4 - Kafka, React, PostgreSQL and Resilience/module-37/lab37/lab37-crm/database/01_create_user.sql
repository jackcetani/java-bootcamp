-- Run as the postgres superuser (the Docker image's default admin).
CREATE ROLE crm_app WITH LOGIN PASSWORD 'CrmLab_Strong1';
GRANT CONNECT ON DATABASE crm TO crm_app;
GRANT USAGE, CREATE ON SCHEMA public TO crm_app;
-- Do NOT grant SUPERUSER or CREATEDB.