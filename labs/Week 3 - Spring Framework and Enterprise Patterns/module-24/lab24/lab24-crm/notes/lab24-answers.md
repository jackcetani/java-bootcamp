# Lab 24 Answers:

## Reflection Questions
1. **Which design decision most affected correctness (contract-first)?**\
The most crucial design decision in this lab was keeping the XSD's exact namespace and element names specified in the guide. Any drift could cause a contract mismatch between the schema and wire format. Everything else should then be generated with `jaxb2-maven-plugin`.
2. **Which failure was hardest to diagnose (payload root vs WSS)?**\
The hardest failure to diagnose was telling a `@PayloadRoot` namespace/localPart mismatch apart from a WS-Security rejection. Both look like the request just didn't work, but one is endpoint routing and the other happens before the endpoint is reached. A crucial difference, but hard to spot if you don't know what to look for.
3. **What evidence proves SOAP and REST share rules?**\
One piece of evidence they share rules is updating `CUS-1002` via SOAP's `updateCustomerStatus`, then reading the same new status block with REST's `GET /api/customers/CUS-1002`. If these differed, it would prove that they were pulling from their own rules. The single injected `CustomerService` bean prevents this.
4. **What breaks first at ten concurrent partners on in-memory storage?**\
At ten times the concurrent partners on in-memory storage, the non-idempotent `create` becomes a lot riskier. With a bunch of partners retrying `create`, duplicate records would become a big problem that affected all users.
5. **Which concern should move to shared platform (WSDL hosting, credential rotation)?**\
First, WSDL/XSD hosting at a stable versioned URL would be the obvious change to shared. Also, proper credential rotation and management for UsernameToken secrets should be shared, not one static secret like this lab has.
6. **What must change before real partner traffic?**\
A few things. First, implementing HTTPS and PaswordDigest. Also, real persistence is needed before real traffic / data hits the app. Lastly, we still need to answer the gap with `create`'s idempotency.
7. **How does this lab connect to Labs 13, 16, 23, and 25?**\
Lab 13 established the concepts for the SOAP contracts. Lab 16 built the exception-hierarchy pattern `CustomerNotFoundException` and `DuplicateCustomerException` follow in this lab. Lab 23 built the `CustomerService` and REST layer this labs endpoint delegates to. Lastly, Lab 25 will refactor the repo under `CustomerService` without touching the `@PayloadRoot` signatures.
8. **Which fault code or WSDL element matters most when a partner says “it doesn’t work”?**\
The most important fault code / WSDL element for this would be the `faultcode` field. It would read either 'CLIENT' or 'SERVER', quickly telling an operator if a problem is with the partner or a server bug.
9. **(Forward look) What must remain stable when Lab 25 swaps repository implementation?**\
`CustomerService`'s public method signatures MUST remain unchanged. `CustomerEndpoint` and `CustomerController` are the only things called through, so Lab 25 can change the storage underneath without touching either contract.