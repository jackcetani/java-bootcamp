# Banking mini UML

```mermaid
classDiagram
    class Printable {
        <<interface>>
        +printDetails() void
    }

    class Customer {
        -String id
        -String name
        +printDetails() void
    }

    class Account {
        -double balance
        +deposit(double amount) void
        +withdraw(double amount) boolean
        +getBalance() double
        +getAccountType() String
    }

    class SavingsAccount {
        +getAccountType() String
    }

    class CurrentAccount {
        -double WITHDRAWAL_FEE
        +withdraw(double amount) boolean
        +getAccountType() String
    }

    class Transaction {
        -String transactionId
        -String type
        -double amount
    }

    Printable <|.. Customer : implements
    Account <|-- SavingsAccount : extends
    Account <|-- CurrentAccount : extends
    Customer "1" --> "0..*" Account : owns
    Account "1" --> "0..*" Transaction : records
```

- Inheritance: SavingsAccount and CurrentAccount are specialized Accounts.
Since they both extend Account, they are specialized Account instances.
- Interface realization: Customer promises Printable behavior.
Since Customer implements Printable, it MUST include a personal printDetails() method.
- Association: One Customer may own many Accounts.
True, a customer can have multiple instances of both SavingsAccounts and CurrentAccounts.
- Association: One Account may record many Transactions.
True, an account can have multiple transactions recorded, for both Savings and Current accounts.