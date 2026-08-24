# Lab 41 — Container runbook

Work in `module-41\lab41\lab41-crm`.

## Build

```powershell
docker build --pull -t crm-api:lab41 .
```
Image Id recorded: `sha256:0dd2a9bc936e3fdbb0481c2b63476180714cd096c567ad8193ff279db78c3994 166190335 "10001"`
RepoDigests: empty until pushed to a registry — not applicable in this local-only lab.

## Run

```powershell
Copy-Item .env.example .env.local
# edit .env.local: CRM_DB_PASSWORD=change-me
docker run -d --name crm-lab41 --network <your-confirmed-network-name> `
  --memory=512m --env-file .env.local -p 8080:8080 crm-api:lab41
```

## Verify

- Readiness: `curl.exe -fsS http://127.0.0.1:8080/actuator/health/readiness` → expect `{"status":"UP"}`
- CRM smoke: `curl.exe -fsS -H "X-Correlation-Id: lab-request-001" "http://127.0.0.1:8080/api/customers?status=ACTIVE"` → expect `200`
- User inside container: `docker exec crm-lab41 id` → expect `uid=10001(spring)`

## Stop / graceful shutdown

```powershell
docker stop --time 20 crm-lab41
```

## Registry (notes only — no credentials)

Tagging scheme for Lab 42: `crm-api:1.0.0-<git-short-sha>`, generated via:
```powershell
$gitSha = git rev-parse --short HEAD
docker tag crm-api:lab41 "crm-api:1.0.0-$gitSha"
```
Registry authentication (`docker login`) happens interactively at push time — credentials never touch this repo or any committed file.