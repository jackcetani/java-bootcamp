# Lab 22 Answers

## Reflection Questions
1. **Which design decision most affected correctness (constructor vs field injection)?**\
The most crucial design decision was choosing constructor injection over field injection for `CustomerService`. It is what makes `CustomerServiceTest`'s `new` call possible and what makes a missing collaborator fail loudly at startup instead of silently as a null field.
2. **Which failure was hardest to diagnose (scan issues, missing beans)?**\
The hardest failure to diagnose would be failure experiment 5's dual-store bug. The test looks like it should pass, and the failure only makes sense when you realize the service quietly built its own second, disconnected repository instance.
3. **What evidence proves the graph works (unit + IT + curls)?**\
All three of these things prove the graph works. The pure unit test proves the class are decoupled, the `@SpringBootTestIT` proves the spring graph behaves the same, and the curl trace against the running app proves it works with real HTTP. 
4. **What breaks first at ten times the bean count or request rate?**\
Keeping `dependency-graph.md` in sync with the actual constructor signatures gets very difficult at ten times the bean count. This si why it is good practice to generate the graph from the Actuator endpoint rather than maintaining it manually.
5. **Which concern should move to shared infrastructure (shared Boot starters, component scan conventions)?**\
A shared Spring Boot parent with a pre-agreed component-scan should be shared across every CRM module. As of now, each lab's `CrmApplication` independently ensures this, but a real service would want that convention enforced centrally.
6. **What must change before real customer data is used (persistence bean swap; still no PII logs)?**\
As always, no PII should be in notification or lifecycle logs. Even when the persistence bean swaps from `InMemoryCustomerRepository` to a real JDBC/JPA implementation, this rule must be enforced extremely strictly.
7. **How does this lab connect to Labs 18–21 (mocks, HTTP, logs, metrics)?**\
Lab 18 gave us practice with Mockito, showing us how `CustomerServiceTest`'s `mock(NotificationService.class)` works. Lab 19 gave us the HTTP endpoints this lab's controller uses, and Lab 20's correlation and PII logging rules are still enforced in `NotificationService`. Lastly, lab 21's Actuator health and metrics pattern is the same shape this lab formalizes for everything else.
8. **What metric or bean health signal matters most after DI is complete?**\
The metric / signal that matters most once DI is complete is whether the app context actually starts successfully. A missing-bean exception is an unambiguous startup failure, but a good sign. It fails loudly rather than silently misbehaving at runtime.
9. **(Forward look) Which bean would you replace first for JDBC/JPA production persistence?**\
I would replace only the `InMemoryCustomerRepository` bean. Since `CustomerService` depends on the `CustomerRepository` interface and not the concrete class, swapping in a JPA implementation only required a new `@Repository` call implementing the same two methods. `CustomerController`, `CustomerService`, and `CustomerServiceTest`'s fake unit test wouldn't need to change. This is the setup for Lab 25 almost exactly.