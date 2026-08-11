# Lab 24 — SOAP notes

Correlation / evidence id: `lab24-001`

## Test-profile security exclusion (Step 8)

`CustomerEndpointTest` runs with `@ActiveProfiles("test")`, under which
`WebServiceConfig.addInterceptors` skips registering `securityInterceptor()`. This is
the second option Step 8's troubleshooting table names directly. \
**Trade-off:** the automated test does not exercise WS-Security at all. That coverage instead comes from
the manual secured/unsecured curl evidence from Failure Experiment 5, which is real proof
the interceptor is registered and working in the actual running application.

## Concepts to Discuss
1. **Why contract-first beats contract-last for partner SOAP**\
   The XSD is versioned independent of any Java implementation, so a partner can generate their own client and validate requests against it before server code even exists. Contract-last on the other hand ties the partner's integration to our implementation.
2. **@PayloadRoot vs REST @RequestMapping**\
   `@PayloadRoot` dispatches on the XML element's namespace and the local name inside `/ws`. `@RequestMapping` on the other hand dispatches on HTTP methods and the URL path. SOAP has one URL and many possible body shapes, while REST has many URLs but only one shape per URL.
3. **Why endpoint must not re-implement lifecycle rules**\
   `CustomerEndpoint` should only extract fields and call `CustomerService`. Since every business rule live in only one place, SOAP and REST can never drift into different behavior for the same customer.
4. **What SOAP fault communicates vs REST ErrorResponse**\
   A SOAP fault gives a `faultcode`, `faultstring`, and optional `detail` field. It is the XML / SOAP equivalent of an HTTP status code and JSON error body.
5. **Idempotency: getCustomer vs createCustomer retries**\
   `getCustomer` is always safe to retry, while `createCustomer` is not. Failure experiment 4 shows us that a repeated `create` call silently duplicates rather than rejecting, as has been the case for the previous labs.
6. **HTTPS (transport) vs WS-Security (message) and why both matter**\
   HTTPS protects the data IN TRANSIT between endpoints, but it does NOT say anything about who sent a message once it arrives to its destination. WS-Security's UsernameToken proves the sender identity, no matter what happens during transport. Prod needs both.
7. **Partner evidence pack: WSDL, sample XML, fault XML**\
   Partners would need the live WSDL, sample request/response XML, and the fault XML to get the full picture of the service and build confidently off of it.
8. **Two Boot instances: in-memory state does not share**\
   Again as in previous labs, the in-memory `CustomerService` map is per JVM instance. Two SOAP instances behind a load balancer would each have an independent customer registry.
9. **What must never appear in fault strings (stack traces, secrets)**\
   Things that should never appear in fault strings include stack traces, internal file paths, or any secrets (in this case, `lab24-shared-secret`). A fault should tell the partner what went wrong in business terms, but should never expose internals of the application.
10. **What Lab 25 changes (repo) without rewriting @PayloadRoot methods**\
    Lab 25 refactors the repo layer under `CustomerService`. Since `CustomerEndpoint` only calls `CustomerService`, none of its `@PayloadRoot` signatures need to change, no matter what happens to the storage underneath.
