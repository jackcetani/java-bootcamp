# SOAP design notes — Lab 13

## Concepts to Discuss
1. **Main data flow (partner → SOAP contract → future endpoint → CustomerService)**\
partner sends SOAP/XML -> this static contract -> (Lab 24) Spring-WS endpoint -> `CustomerService`.
2. **Trust boundary: schema validation vs service business rules**\
XSD validates the shape. Business rules like duplicate ID rejection stay inside `CustomerService`, not the schema.
3. **Success/failure contract for GetCustomer unknown IDs**\
`GetCustomer` for an unknown ID returns `soapenv:Client` fault or `CUSTOMER_NOT_FOUND`, not a 200 with empty fields.
4. **Stable identity (CUS-1001) vs display fields**\
CUS-1001 is the stable identity. fullName / status / email are mutable display fields that can change without affecting the ID.
5. **Retry/idempotency: Create vs Get vs Update semantics**\
`Get` is safe to retry, but `Create` is not because you risk creating a duplicate. `Update` is safe if sent with the same target status.
6. **Static WSDL files vs generating WSDL only at runtime**\
Static means partners can review the exact contract in version control before anything is hosted.
7. **Correlation header/field for support (lab-request-001)**\
`correlationId` is an optional field on every request/response element specifically so support can trace `lab-request-001` end-to-end once this is live.
8. **Two instances serving the same WSDL version—what must stay identical?**\
Two instances serving the same WSDL version must have identical namespaces, operation names, and element shapes. Any deviation from this breaks partner tooling.
9. **Why document/literal over RPC/encoded for this lab?**\
We chose document/literal over RPC/encoded in this lab because it is more modern and in the interoperable SOAP style. RPC/encoded is legacy and poorly supprted by modern tools.
10. **What must not change between Lab 13 and Lab 24 without a version bump?**\
You must not change anything about the namespace, operation names, or element structure between lab 13 and lab 24 without a version bump.

## Reflection Questions
1. **Which design decision most affected partner usability?**\
The design desicison that most affected partner usability was making `correlationId` optional on every element. Otherwise, a WS-Addressing header would've been required, a much lower barrier for a partner to adopt.
2. **Which failure was hardest to diagnose (namespace vs element name)?**\
A namespace mismatch or a wrong value producing an unknown element rather than an obvious namespace error.
3. **What evidence proves the contract is implementable in Lab 24?**\
The WSDL's `portType`/`binding`/`service` describe the three callable operations. Thus, lab 24 just needs `@Endpoint` methods matching these exact signatures.
4. **What breaks first at ten times the field count without versioning?**\
Without versioning, the partners' generated client code starts silently mismatching fields, since nothing forces them to regenerate against a new schema version.
5. **Which concern should move to shared infrastructure (WSDL hosting, WS-Security)?**\
WSDL hosting and WS-Security policy enforcement should move over. Both belong at a gateway/infrastructure layer.
6. **What must change before real customer data is used?**\
Actual authorization with WS-Security or a gateway. Also, a live, non-placeholder endpoint.
7. **How does this lab connect to Labs 8–12 domain work and Lab 24 SOAP hosting?**\
It is the same customer/status setup carried forward with nothing changed. Lab 24 will simply ask us to implement the files.
8. **What metric or log field matters most once the endpoint is live?**\
The metrix that matters most is the fault rate broken down by fault code. For example, `CUSTOMER_NOT_FOUND` vs `VALIDATION_FAILED`. This tells you whether partners are sending bad data.
9. **(Forward look) If REST arrives later, what from this SOAP contract should stay conceptually identical?**\
What staus are the semantics of the three operations `create`/`update`/`get`. Also, the stable `CUS-####` identity. Only the wire format changes, not the underlying contract shape.

## Retry semantics
- GetCustomer: safe to retry any number of times.
- CreateCustomer: NOT safe to blindly retry — a retried create after a timeout could
  produce a duplicate-ID fault on the second attempt (which is at least a safe failure,
  not a duplicate record).
- UpdateCustomer: effectively idempotent for the same target status.

## Lab 24 forward link
Lab 24 implements Spring-WS `@Endpoint` methods against this exact contract, wired to
`CustomerService` from Labs 12/15. No XML shape here changes when that happens.


1. **Contract-first vs code-first:**\
We published the XSD/WSDL before any server exists, so partners can build and test against a stable, version-controlled contract instead of reverse-engineering whatever a Java class happens to look like this sprint.
2. **document/literal choice:**\
Chosen over RPC/encoded because it's the modern, WS-I-compliant, widely interoperable style. RPC/encoded is legacy and poorly supported by current tooling.
3. **Correlation placement:**\
`correlationId` sits as an optional field inside each request/response element rather than a SOAP header. It is simplest to implement for this lab. A header-based approach is the Bonus Challenge 1 upgrade.
4. **Fault shapes:**\
Both not-found (`CUSTOMER_NOT_FOUND`) and validation (`VALIDATION_FAILED`) are `soapenv:Client` faults, since both stem from something the caller sent wrong, not a server-side failure.
5. **Lab 24 vs static here:**\
Lab 24 hosts a live endpoint at a real address implementing these operations against `CustomerService`. The XSD, WSDL structure, namespace, and operation names all stay exactly as they are in this lab.
