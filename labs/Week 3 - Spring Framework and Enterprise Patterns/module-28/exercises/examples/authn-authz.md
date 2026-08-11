# Exercise 01 - Authentication vs. Authorization:

## Goal

Explain 401 vs 403 with Northstar agent/admin examples.

## Definitions
**Authorization**: the process that determines what a user, app, or service is allowed to do with data and resources they can access.
**Authentication**: the process of verifying the identity of a user, device, or system to ensure they are who they claim to be before granting access.

| Status | Meaning | CRM example |
| --- | --- | --- |
| 401 | Not authenticated | No/invalid Bearer token |
| 403 | Authenticated but forbidden | `agent1` hits `/api/admin/**` |
| 200 | Allowed | `agent1` GET `CUS-1001` |

### Step 3 — Lab users

Record `agent1` (AGENT) and `admin1` (ADMIN).

### Step 4 — Correlation ≠ auth

`lab-request-001` is operational metadata — never treat it as a credential.

## Expected result

401/403/200 CRM examples and lab users documented.