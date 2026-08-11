# Exercise 01 - DTO Constraints:


Plan constraints for `CustomerRequest` / status update fields.

| Field | Constraint idea |
| --- | --- |
| name | `@NotBlank` |
| email | `@Email` + `@NotBlank` |
| customerId | `@NotBlank` / pattern for CUS-#### |
| status | `@NotNull` + allowed values |