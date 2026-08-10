# Lab 23 — Auto-config vs ownership

## Three things Boot auto-configured

1. The embedded Tomcat server and `DispatcherServlet`/Spring MVC request pipeline: `spring-boot-starter-web`, no manual servlet container setup.
2. Jackson JSON serialization/deserialization for `Customer` request and response bodies: no manual `ObjectMapper` configuration.
3. `/actuator/health` and `/actuator/info` machinery: including the health-indicator aggregation logic

## Three things you still own

1. Domain rules: which fields are required, and business rules are all our code completely. Boot has no opinion on CRM validation logic.
2. Actuator exposure policy: Boot defaults to a conservative exposure, but which endpoints to open and to whom is entirely a decision we have to make and document, not something Boot decides safely for us.
3. Secrets and environment-specific configuration strategy: the `dev`/`prod` profile teaser here shows that config can vary by environment, but real secret management (Lab 26) is still something we have to design, not something auto-configuration provides.

## Concepts to Discuss
1. **Main flow: HTTP → Boot MVC → service map → JSON response**\
   HTTP request --> Boot's autoconfigured MVC dispatch --> `@Valid` Bean Validation --> `CustomerController` --> `CustomerService` in-memory map --> JSON response
2. **Trust boundary: @Valid / @NotBlank at the controller before store put**\
   `@Valid`/`@NotBlank` in the Controller is where invalid input gets rejected before anything reaches the service or database. Trust boundary is enforced right at the HTTP entry point in this lab.
3. **Success/failure contracts: 201/200 vs 404; correlation echoed on create**\
   201 --> `create` with the correlation header echoed. 200 on a found `GET`, 404 on `CUS-MISSING`, and 400 on a blank required field, which is found via bean validation.
4. **Stable fixtures (CUS-1001) vs random IDs in demos**\
   `CUS-1001` and `CUS-1002` are our stable fixtures and are the ONLY IDs that should ever appear in demos, screenshots, and tests. Anything randomly generated isn't good evidence for tests, and real customer data should NEVER be used for testing.
5. **Idempotency: GET safe; POST create may overwrite map key today — document honesty**\
   GET is always safe to repeat. POST with `create` is not however. Repeating a `create` for the same ID overwrites the map entry silently, rather than throwing an error.
6. **Why embedded Tomcat is a local shortcut vs reverse-proxy + hardened Actuator in prod**\
   Using embedded Tomcat locally is great for development as it gives you a full working server in one command. Production deployments on the other hand almost always sit an embedded server behind a reverse proxy with a hardened Actuator. The local setup should never be exposed publicly.
7. **Evidence operators need: startup banner, health JSON, curl transcripts**\
   The startup banner would provide evidence that the process actually started, and the health JSON proves it's serving traffic. THe curl transcripts for both fixtures prove the actual business logic works.
8. **Two instances: in-memory state does not share — Lab 25/27 implications**\
   Since `CustomerService`'s map only live in one JVM's memory, two running instances behind a load balancer would each have their own independent customer registry. Lab 25-27 will address this with real persistence fit for prod.
9. **What auto-config provided (server, Jackson, DispatcherServlet) vs what you own**\
   Auto-config provides the embedded TomCat server and MVC request pipeline, the Jackson JSON serialization, and the entire Actuator, including `health` and `info`. The things WE own still are the domain rules, the Actuator exposure policy, and the responsibility of maintaining a secret and environment config strategy.
10. **What Lab 24 adds (SOAP) without abandoning this REST contract**\
    Lab 24 puts a soap endpoint beside this exact API, both delegating to `CustomerService`. The API paths, status codes, and JSON shape we've made here needs to stay the same. SOAP is additive, not replacing anything.

