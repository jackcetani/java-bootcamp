# lab8-crm - Northstar CRM skeleton (Lab 8)

## Overview

A Maven skeleton for the Northstar Customer Management Platform, under package
`com.northstar.crm`. This lab (Module 8) builds **structure only**: seven layered
packages, compiling stub classes, and documentation — no HTTP framework, no database,
no real business logic yet. See `docs/layer-flow.md` for the intended request flow and
`docs/CODING-STANDARDS.md` for the layer rules.

## Compile and run (Windows)

```powershell
cd $env:USERPROFILE\java-bootcamp\examples\lab8-crm
mvn -q clean compile
java -cp target\classes com.northstar.crm.Main
```

Expected output:
```text
Northstar CRM skeleton — Lab 8
Packages: controller, service, repository, entity, dto, config, exception
Examples: CUS-1001 Amina Khan ACTIVE | CUS-1002 Ravi Singh PROSPECT
```
## Design decisions

- **Why layers?** Separating `controller` / `service` / `repository` / `entity` / `dto`
  now means Labs 9–12 can fill in real behavior without ever renaming a package.
  Everyone already agrees where validation, business rules, and persistence each live.
- **Why stubs that throw instead of empty method bodies?** `UnsupportedOperationException`
  makes an unimplemented path fail loudly and immediately if something calls it, instead
  of silently returning `null` and causing a confusing `NullPointerException` two layers
  away. 
- **Why DTOs separate from the entity?** `CustomerRequest`/`CustomerResponse` are
  contracts with the outside world; `Customer` is the persisted domain model. Keeping
  them separate means the API shape and the storage shape can evolve independently. Also,
  a field can be added to one without silently changing the other.

## Docs

- [`docs/CODING-STANDARDS.md`](docs/CODING-STANDARDS.md) — package rules, naming, DTO/entity split
- [`docs/layer-flow.md`](docs/layer-flow.md) — how a create-customer request will flow through layers


