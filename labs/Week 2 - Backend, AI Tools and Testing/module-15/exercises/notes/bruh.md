---

# LAB 15 — Service Layer Design

## Step 1 — repository interface

```powershell
cd $env:USERPROFILE\java-bootcamp\examples
Copy-Item -Recurse lab14-crm lab15-crm
cd lab15-crm
```

`repository/CustomerRepository.java`:

```java
package com.northstar.crm.repository;

import com.northstar.crm.entity.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(String customerId);
    boolean existsById(String customerId);
    boolean existsByEmail(String email);
    List<Customer> findAll();
}
```

`repository/InMemoryCustomerRepository.java`:

```java
package com.northstar.crm.repository;

import com.northstar.crm.entity.Customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryCustomerRepository implements CustomerRepository {
    private final Map<String, Customer> customersById = new HashMap<>();

    @Override
    public Customer save(Customer customer) {
        customersById.put(customer.getCustomerId(), customer);
        return customer;
    }

    @Override
    public Optional<Customer> findById(String customerId) {
        return Optional.ofNullable(customersById.get(customerId));
    }

    @Override
    public boolean existsById(String customerId) {
        return customersById.containsKey(customerId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return customersById.values().stream()
                .anyMatch(c -> c.getEmail() != null && c.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(customersById.values());
    }
}
```

## Step 2 — `CustomerService` interface

```java
package com.northstar.crm.service;

import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Customer addCustomer(Customer customer);
    Optional<Customer> findById(String customerId);
    List<Customer> listAll();
    Customer changeStatus(String customerId, CustomerStatus newStatus, String correlationId);
}
```

(This renames Lab 12's concrete `CustomerService` class — see `DefaultCustomerService`
below — to an interface. Update `CustomerApiFacade`'s field type accordingly.)

## Step 3 — `CustomerValidator.java`

```java
package com.northstar.crm.service;

import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.repository.CustomerRepository;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class CustomerValidator {
    private static final Map<CustomerStatus, Set<CustomerStatus>> ALLOWED =
            new EnumMap<>(CustomerStatus.class);

    static {
        ALLOWED.put(CustomerStatus.PROSPECT, EnumSet.of(CustomerStatus.ACTIVE, CustomerStatus.CLOSED));
        ALLOWED.put(CustomerStatus.ACTIVE, EnumSet.of(CustomerStatus.SUSPENDED, CustomerStatus.CLOSED));
        ALLOWED.put(CustomerStatus.SUSPENDED, EnumSet.of(CustomerStatus.ACTIVE, CustomerStatus.CLOSED));
        ALLOWED.put(CustomerStatus.CLOSED, EnumSet.noneOf(CustomerStatus.class));
    }

    private final CustomerRepository repository;

    public CustomerValidator(CustomerRepository repository) {
        this.repository = repository;
    }

    public void validateNew(Customer customer) {
        if (customer.getCustomerId() == null || customer.getCustomerId().isBlank()) {
            throw new IllegalArgumentException("customerId is required");
        }
        if (repository.existsById(customer.getCustomerId())) {
            throw new IllegalStateException("duplicate customerId: " + customer.getCustomerId());
        }
        if (repository.existsByEmail(customer.getEmail())) {
            throw new IllegalStateException("duplicate email: " + customer.getEmail());
        }
    }

    public void validateTransition(CustomerStatus from, CustomerStatus to, String correlationId) {
        Set<CustomerStatus> allowed = ALLOWED.getOrDefault(from, Set.of());
        if (!allowed.contains(to)) {
            throw new IllegalStateException(
                    "illegal status transition " + from + " -> " + to + " [" + correlationId + "]");
        }
    }
}
```

## Step 4 — `DefaultCustomerService.java`

```java
package com.northstar.crm.service;

import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

public class DefaultCustomerService implements CustomerService {
    private final CustomerRepository repository;
    private final CustomerValidator validator;

    public DefaultCustomerService(CustomerRepository repository, CustomerValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @Override
    public Customer addCustomer(Customer customer) {
        validator.validateNew(customer);
        return repository.save(customer);
    }

    @Override
    public Optional<Customer> findById(String customerId) {
        return repository.findById(customerId);
    }

    @Override
    public List<Customer> listAll() {
        return List.copyOf(repository.findAll());
    }

    @Override
    public Customer changeStatus(String customerId, CustomerStatus newStatus, String correlationId) {
        Customer existing = repository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "customer not found [" + correlationId + "]: " + customerId));
        validator.validateTransition(existing.getStatus(), newStatus, correlationId);
        existing.setStatus(newStatus);
        return repository.save(existing);
    }
}
```

Wiring (`Main.java`, and update `CustomerApiFacade`'s constructor param type to
`CustomerService`):

```java
CustomerRepository repo = new InMemoryCustomerRepository();
CustomerValidator validator = new CustomerValidator(repo);
CustomerService service = new DefaultCustomerService(repo, validator);

Customer amina = new Customer();
amina.setCustomerId("CUS-1001");
amina.setFullName("Amina Khan");
amina.setEmail("amina.khan@example.com");
amina.setStatus(CustomerStatus.ACTIVE);
service.addCustomer(amina);

Customer ravi = new Customer();
ravi.setCustomerId("CUS-1002");
ravi.setFullName("Ravi Singh");
ravi.setEmail("ravi.singh@example.com");
ravi.setStatus(CustomerStatus.PROSPECT);
service.addCustomer(ravi);

Customer activated = service.changeStatus("CUS-1002", CustomerStatus.ACTIVE, "lab-request-001");
System.out.printf("activated %s status=%s%n", activated.getCustomerId(), activated.getStatus());

try {
    service.changeStatus("CUS-1001", CustomerStatus.PROSPECT, "lab-request-001");
} catch (IllegalStateException ex) {
    System.out.println("expected failure: " + ex.getMessage());
}
System.out.println("CUS-1001 still: " + service.findById("CUS-1001").orElseThrow().getStatus());
```

Expected: `activated CUS-1002 status=ACTIVE`, then `expected failure: illegal status
transition ACTIVE -> PROSPECT [lab-request-001]`, then `CUS-1001 still: ACTIVE`.

## Step 7 — `CustomerValidatorTest.java`

```java
package com.northstar.crm.service;

import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.repository.InMemoryCustomerRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomerValidatorTest {

    @Test
    void allowsProspectToActive() {
        var validator = new CustomerValidator(new InMemoryCustomerRepository());
        assertDoesNotThrow(() ->
                validator.validateTransition(CustomerStatus.PROSPECT, CustomerStatus.ACTIVE, "lab-request-001"));
    }

    @Test
    void rejectsActiveToProspect() {
        var validator = new CustomerValidator(new InMemoryCustomerRepository());
        assertThrows(IllegalStateException.class, () ->
                validator.validateTransition(CustomerStatus.ACTIVE, CustomerStatus.PROSPECT, "lab-request-001"));
    }

    @Test
    void rejectsClosedToActive() {
        var validator = new CustomerValidator(new InMemoryCustomerRepository());
        assertThrows(IllegalStateException.class, () ->
                validator.validateTransition(CustomerStatus.CLOSED, CustomerStatus.ACTIVE, "lab-request-001"));
    }
}
```

## `docs/service-layer-notes.md` / README

```markdown
## Allowed transitions
PROSPECT  -> ACTIVE, CLOSED
ACTIVE    -> SUSPENDED, CLOSED
SUSPENDED -> ACTIVE, CLOSED
CLOSED    -> (none)

## Same-status policy
`changeStatus(id, sameStatus, ...)` is currently treated as an illegal transition
(not in any ALLOWED set as a self-loop) — documented choice: reject, don't silently
no-op, so callers get clear feedback rather than ambiguous success.

## Wiring (Spring DI preview)
Manual today: `new InMemoryCustomerRepository()` -> `new CustomerValidator(repo)` ->
`new DefaultCustomerService(repo, validator)`. Later: the same three constructor
parameters become `@Autowired` beans — no rule logic changes.
```

## `notes/lab15-answers.md`

### Concepts to Discuss
1. **Flow:** facade -> service -> validator (business meaning) + repository (storage).
2. **Trust boundary:** Bean Validation (Lab 14) checks shape at the edge; `CustomerValidator` checks meaning (uniqueness, legal transitions) one layer in.
3. **Contract:** create -> `IllegalStateException` on duplicate ID/email; `changeStatus` -> `IllegalArgumentException` if unknown ID, `IllegalStateException` if illegal transition.
4. **Stable vs mutable:** `CUS-1001` fixed; `status` is exactly what this lab's validator governs the mutation of.
5. **Retry/idempotency:** calling `changeStatus` to the *same* status it's already at is currently rejected (see same-status policy) rather than treated as a safe no-op.
6. **In-memory vs future JPA:** same interface (`CustomerRepository`), different implementation — that's the whole point of the interface existing.
7. **Correlation on illegal transitions:** every `CustomerValidator` failure carries `lab-request-001` for support tracing.
8. **Two JVMs:** independent memory; real activation races need a real database with proper constraints, not this in-memory Map.
9. **Why constructor DI beats service locators:** Labs 17–18 can hand `DefaultCustomerService` a fake repository/validator directly through the constructor — no static lookup to fight with in tests.
10. **What Spring changes vs doesn't:** wiring (`new` -> `@Autowired`) changes; the `ALLOWED` transition table and validator logic stay exactly the same.

### Security and Production Review
1. Untrusted: all client fields reaching the service.
2. Enforced: shape at the facade (Lab 14), meaning in `CustomerValidator` (this lab); real auth still absent.
3. Sensitive values: none beyond sample emails, held only in memory.
4. Safe to retry: `findById` always; `changeStatus` only if targeting the same already-current status is treated as safe (it isn't here — documented as reject).
5. After partial failure: no status write happens if `validateTransition` throws — order matters and is enforced (validate before mutate).
6. What ops monitors: rejected-transition counts, correlation IDs on failures.
7. Unacceptable in prod: in-memory storage; no auth on `changeStatus`.
8. Transition policy versioning: when product changes KYC rules, the `ALLOWED` static block is the single place to update — and it needs its own change-review process since it's business policy, not just code.

### Reflection Questions
1. **Design decision that most affected correctness:** wiring one shared `CustomerRepository` instance into both the validator and the service — two separate instances would silently break duplicate-detection.
2. **Hardest failure to diagnose:** status getting mutated before validation failed — reordering `validateTransition` before `setStatus` in `changeStatus` was the exact fix.
3. **Evidence it works:** `CustomerValidatorTest` plus the `Main` demo showing Ravi activated and Amina's illegal transition rejected with her status provably unchanged afterward.
4. **What breaks first at 10x load/concurrent activations:** the in-memory `HashMap` has no concurrency control — simultaneous activations could race; a real DB with proper transaction isolation is needed at that point.
5. **What moves to shared infra:** the transition-policy table itself is a strong candidate for a config service once product wants to change KYC rules without a code deploy.
6. **What must change before real data:** persistence swap to a real database with actual concurrency guarantees, plus real authn/authz on `changeStatus`.
7. **Connection to Labs 14/16–18:** Lab 14's facade now depends on this lab's `CustomerService` interface; Lab 16 will wrap these `IllegalStateException`s in richer typed exceptions; Labs 17–18 will mock `CustomerRepository`/`CustomerValidator` directly through these same constructors.
8. **Metric for rejected transitions:** rejected-transition count grouped by `from -> to` pair — tells product exactly which illegal transition users keep attempting.
9. **What stays identical when Spring injects `DefaultCustomerService`:** the constructor signature, the `ALLOWED` transition table, and every validation rule — only *who calls* `new DefaultCustomerService(...)` changes.