# Coding standards — Northstar CRM (Lab 8)

## Packages

- Root: `com.northstar.crm`
- Layers: `controller`, `service`, `repository`, `entity`, `dto`, `config`, `exception`

## Layers
- controller: transport / API mapping only
- service: business rules
- repository: persistence; save / find
- entity: domain model / fields (Customer)
- dto: request / response contracts
- config: wiring
- exception: domain and API failures

## Hard rules — dependency direction 

```text
controller -> service -> repository -> entity
controller -> dto
service    -> dto, entity, exception
repository -> entity
entity     -> (nothing in other CRM layers)
config     -> (wiring only; later may reference beans)
```

- Services must not depend on controllers.
- Entities must not carry HTTP or SOAP types.
- Repositories must not import controllers.
- No production passwords or API keys in source.
- Prefer CUS-#### for stable customer identities in examples.

## Naming

- Classes: PascalCase (`CustomerService`, `CustomerRepository`, `CustomerController`)
- Methods: camelCase (`createCustomer`, `findById`, `save`)
- Customer IDs: Cus-#### (`CUS-1001`, `CUS-1002`)
- Correlation: `lab-request-001`

## What must NOT live where

| Package | Must NOT own                                        |
| ------- |-----------------------------------------------------|
| controller | SQL, business rules, persistence details            |
| service | HTTP headers, JDBC details                          |
| repository | REST mapping, DTO shapes                            |
| entity | Request JSON shapes, HTTP types                     |
| dto | Persistence annotations (later JPA stays on entity) |
| config | Happy-path business logic |
|exception | Business rules | 

## DTO vs entity
`CustomerRequest`/`CustomerResponse` are what the outside world sends and receives, while `Customer` is what gets persisted. A request payload can omit fields (like `status`) and a response can omit fields the client shouldn't see that the entity might eventually carry. For this purpose they are separate classes. Never import `entity` types into `dto`, or vice versa.

## Exception handling expectations
- Domain failures get a named type under `exception`, extending `RuntimeException`, matching `CustomerNotFoundException`.
- Exception messages should be safe to eventually log or map to an API error body
- Do not handle exceptions with empty catch blocks once the actual logic exists.

## What not to commit
- `target/` 
- `.idea/`, `*.iml`
- `.env`
- real credentials, API keys, JDBC connection strings with passwords
- Real customer PII, only demo IDs and names

## Tooling
- JDK 21, Maven 3.9+
- `mvn -q clean compile` before every commit

## Lab 8 ban

No Spring, JPA, or Kafka imports in stubs.
