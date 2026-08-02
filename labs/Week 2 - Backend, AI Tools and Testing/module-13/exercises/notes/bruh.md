---

# LAB 13 — SOAP API Design (Contract-First)

## Setup

```powershell
cd $env:USERPROFILE\java-bootcamp\examples
New-Item -ItemType Directory -Force -Path lab13-crm\contracts, lab13-crm\samples, lab13-crm\docs | Out-Null
New-Item -ItemType Directory -Force -Path ..\..\notes\screenshots\lab-13 | Out-Null
```

## `docs/operation-matrix.md`

```markdown
| Operation | Purpose | Key inputs | Key outputs |
| --------- | ------- | ---------- | ----------- |
| CreateCustomer | Register a new CRM customer | fullName, email, phone?, status? | customer (with ID) |
| UpdateCustomer | Change mutable fields / status | customerId, optional fields | customer |
| GetCustomer | Fetch one customer by ID | customerId | customer |
```

## `contracts/customer.xsd`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           xmlns:tns="http://northstar.com/crm/customer"
           targetNamespace="http://northstar.com/crm/customer"
           elementFormDefault="qualified">

  <xs:simpleType name="CustomerStatus">
    <xs:restriction base="xs:string">
      <xs:enumeration value="PROSPECT"/>
      <xs:enumeration value="ACTIVE"/>
      <xs:enumeration value="SUSPENDED"/>
      <xs:enumeration value="CLOSED"/>
    </xs:restriction>
  </xs:simpleType>

  <xs:complexType name="CustomerType">
    <xs:sequence>
      <xs:element name="customerId" type="xs:string"/>
      <xs:element name="fullName" type="xs:string"/>
      <xs:element name="email" type="xs:string"/>
      <xs:element name="phone" type="xs:string" minOccurs="0"/>
      <xs:element name="status" type="tns:CustomerStatus"/>
      <xs:element name="createdAt" type="xs:dateTime"/>
    </xs:sequence>
  </xs:complexType>

  <xs:element name="createCustomerRequest">
    <xs:complexType>
      <xs:sequence>
        <xs:element name="fullName" type="xs:string"/>
        <xs:element name="email" type="xs:string"/>
        <xs:element name="phone" type="xs:string" minOccurs="0"/>
        <xs:element name="status" type="tns:CustomerStatus" minOccurs="0"/>
        <xs:element name="correlationId" type="xs:string" minOccurs="0"/>
      </xs:sequence>
    </xs:complexType>
  </xs:element>
  <xs:element name="createCustomerResponse">
    <xs:complexType>
      <xs:sequence>
        <xs:element name="customer" type="tns:CustomerType"/>
      </xs:sequence>
    </xs:complexType>
  </xs:element>

  <xs:element name="updateCustomerRequest">
    <xs:complexType>
      <xs:sequence>
        <xs:element name="customerId" type="xs:string"/>
        <xs:element name="fullName" type="xs:string" minOccurs="0"/>
        <xs:element name="email" type="xs:string" minOccurs="0"/>
        <xs:element name="phone" type="xs:string" minOccurs="0"/>
        <xs:element name="status" type="tns:CustomerStatus" minOccurs="0"/>
        <xs:element name="correlationId" type="xs:string" minOccurs="0"/>
      </xs:sequence>
    </xs:complexType>
  </xs:element>
  <xs:element name="updateCustomerResponse">
    <xs:complexType>
      <xs:sequence>
        <xs:element name="customer" type="tns:CustomerType"/>
      </xs:sequence>
    </xs:complexType>
  </xs:element>

  <xs:element name="getCustomerRequest">
    <xs:complexType>
      <xs:sequence>
        <xs:element name="customerId" type="xs:string"/>
        <xs:element name="correlationId" type="xs:string" minOccurs="0"/>
      </xs:sequence>
    </xs:complexType>
  </xs:element>
  <xs:element name="getCustomerResponse">
    <xs:complexType>
      <xs:sequence>
        <xs:element name="customer" type="tns:CustomerType"/>
      </xs:sequence>
    </xs:complexType>
  </xs:element>
</xs:schema>
```

## `contracts/CustomerService.wsdl`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<definitions name="CustomerService"
             targetNamespace="http://northstar.com/crm/customer"
             xmlns="http://schemas.xmlsoap.org/wsdl/"
             xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
             xmlns:tns="http://northstar.com/crm/customer"
             xmlns:xsd="http://www.w3.org/2001/XMLSchema">

  <types>
    <xsd:schema>
      <xsd:import namespace="http://northstar.com/crm/customer"
                  schemaLocation="customer.xsd"/>
    </xsd:schema>
  </types>

  <message name="CreateCustomerRequestMessage">
    <part name="body" element="tns:createCustomerRequest"/>
  </message>
  <message name="CreateCustomerResponseMessage">
    <part name="body" element="tns:createCustomerResponse"/>
  </message>
  <message name="UpdateCustomerRequestMessage">
    <part name="body" element="tns:updateCustomerRequest"/>
  </message>
  <message name="UpdateCustomerResponseMessage">
    <part name="body" element="tns:updateCustomerResponse"/>
  </message>
  <message name="GetCustomerRequestMessage">
    <part name="body" element="tns:getCustomerRequest"/>
  </message>
  <message name="GetCustomerResponseMessage">
    <part name="body" element="tns:getCustomerResponse"/>
  </message>

  <portType name="CustomerPortType">
    <operation name="CreateCustomer">
      <input message="tns:CreateCustomerRequestMessage"/>
      <output message="tns:CreateCustomerResponseMessage"/>
    </operation>
    <operation name="UpdateCustomer">
      <input message="tns:UpdateCustomerRequestMessage"/>
      <output message="tns:UpdateCustomerResponseMessage"/>
    </operation>
    <operation name="GetCustomer">
      <input message="tns:GetCustomerRequestMessage"/>
      <output message="tns:GetCustomerResponseMessage"/>
    </operation>
  </portType>

  <binding name="CustomerSoapBinding" type="tns:CustomerPortType">
    <soap:binding transport="http://schemas.xmlsoap.org/soap/http" style="document"/>
    <operation name="CreateCustomer">
      <soap:operation soapAction="http://northstar.com/crm/customer/CreateCustomer"/>
      <input><soap:body use="literal"/></input>
      <output><soap:body use="literal"/></output>
    </operation>
    <operation name="UpdateCustomer">
      <soap:operation soapAction="http://northstar.com/crm/customer/UpdateCustomer"/>
      <input><soap:body use="literal"/></input>
      <output><soap:body use="literal"/></output>
    </operation>
    <operation name="GetCustomer">
      <soap:operation soapAction="http://northstar.com/crm/customer/GetCustomer"/>
      <input><soap:body use="literal"/></input>
      <output><soap:body use="literal"/></output>
    </operation>
  </binding>

  <service name="CustomerService">
    <port name="CustomerSoapPort" binding="tns:CustomerSoapBinding">
      <!-- Placeholder only — Lab 24 hosts a real URL under /ws -->
      <soap:address location="http://localhost:8080/ws"/>
    </port>
  </service>
</definitions>
```

## `samples/` (all eight files)

`createCustomerRequest.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cus="http://northstar.com/crm/customer">
  <soapenv:Header/>
  <soapenv:Body>
    <cus:createCustomerRequest>
      <cus:fullName>Amina Khan</cus:fullName>
      <cus:email>amina.khan@example.com</cus:email>
      <cus:phone>+1-555-0101</cus:phone>
      <cus:status>ACTIVE</cus:status>
      <cus:correlationId>lab-request-001</cus:correlationId>
    </cus:createCustomerRequest>
  </soapenv:Body>
</soapenv:Envelope>
```

`createCustomerResponse.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cus="http://northstar.com/crm/customer">
  <soapenv:Body>
    <cus:createCustomerResponse>
      <cus:customer>
        <cus:customerId>CUS-1001</cus:customerId>
        <cus:fullName>Amina Khan</cus:fullName>
        <cus:email>amina.khan@example.com</cus:email>
        <cus:phone>+1-555-0101</cus:phone>
        <cus:status>ACTIVE</cus:status>
        <cus:createdAt>2026-07-14T17:00:00Z</cus:createdAt>
      </cus:customer>
    </cus:createCustomerResponse>
  </soapenv:Body>
</soapenv:Envelope>
```

`getCustomerRequest.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cus="http://northstar.com/crm/customer">
  <soapenv:Body>
    <cus:getCustomerRequest>
      <cus:customerId>CUS-1002</cus:customerId>
      <cus:correlationId>lab-request-001</cus:correlationId>
    </cus:getCustomerRequest>
  </soapenv:Body>
</soapenv:Envelope>
```

`getCustomerResponse.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cus="http://northstar.com/crm/customer">
  <soapenv:Body>
    <cus:getCustomerResponse>
      <cus:customer>
        <cus:customerId>CUS-1002</cus:customerId>
        <cus:fullName>Ravi Singh</cus:fullName>
        <cus:email>ravi.singh@example.com</cus:email>
        <cus:status>PROSPECT</cus:status>
        <cus:createdAt>2026-07-14T17:05:00Z</cus:createdAt>
      </cus:customer>
    </cus:getCustomerResponse>
  </soapenv:Body>
</soapenv:Envelope>
```

`updateCustomerRequest.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cus="http://northstar.com/crm/customer">
  <soapenv:Body>
    <cus:updateCustomerRequest>
      <cus:customerId>CUS-1002</cus:customerId>
      <cus:status>ACTIVE</cus:status>
      <cus:correlationId>lab-request-001</cus:correlationId>
    </cus:updateCustomerRequest>
  </soapenv:Body>
</soapenv:Envelope>
```

`updateCustomerResponse.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cus="http://northstar.com/crm/customer">
  <soapenv:Body>
    <cus:updateCustomerResponse>
      <cus:customer>
        <cus:customerId>CUS-1002</cus:customerId>
        <cus:fullName>Ravi Singh</cus:fullName>
        <cus:email>ravi.singh@example.com</cus:email>
        <cus:status>ACTIVE</cus:status>
        <cus:createdAt>2026-07-14T17:05:00Z</cus:createdAt>
      </cus:customer>
    </cus:updateCustomerResponse>
  </soapenv:Body>
</soapenv:Envelope>
```

`fault-customerNotFound.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
  <soapenv:Body>
    <soapenv:Fault>
      <faultcode>soapenv:Client</faultcode>
      <faultstring>Customer not found: CUS-9999 (correlationId=lab-request-001)</faultstring>
      <detail>
        <errorCode>CUSTOMER_NOT_FOUND</errorCode>
      </detail>
    </soapenv:Fault>
  </soapenv:Body>
</soapenv:Envelope>
```

`fault-validation.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
  <soapenv:Body>
    <soapenv:Fault>
      <faultcode>soapenv:Client</faultcode>
      <faultstring>fullName is required (correlationId=lab-request-001)</faultstring>
      <detail>
        <errorCode>VALIDATION_FAILED</errorCode>
      </detail>
    </soapenv:Fault>
  </soapenv:Body>
</soapenv:Envelope>
```

## `docs/soap-design-notes.md`

```markdown
# SOAP Design Notes — Lab 13

## Concepts to Discuss
1. Main data flow: partner sends SOAP/XML -> this static contract -> (Lab 24) Spring-WS
   endpoint -> `CustomerService`.
2. Trust boundary: XSD validates *shape* (element presence/type); business rules like
   duplicate-ID rejection stay inside `CustomerService`, not the schema.
3. GetCustomer for an unknown ID returns a `soapenv:Client` fault
   (`CUSTOMER_NOT_FOUND`), not a 200 with empty fields.
4. `CUS-1001` is the stable identity; `fullName`/`status`/`email` are mutable display
   fields that can change without affecting the ID.
5. Retry/idempotency: Get is trivially safe to retry; Create is not (risks a duplicate);
   Update is safe if sent with the same target status (idempotent in effect, if not
   formally guaranteed here).
6. Static WSDL files (this lab) vs generating WSDL at runtime (Lab 24) — static means
   partners can review the exact contract in version control before anything is hosted.
7. `correlationId` is an optional field on every request/response element specifically
   so support can trace `lab-request-001` end-to-end once this is live.
8. Two instances serving the same WSDL version must agree on namespace, operation
   names, and element shapes exactly — any drift breaks partner tooling silently.
9. document/literal (not RPC/encoded) because it's the modern, interoperable SOAP
   style — RPC/encoded is legacy and poorly supported by current tooling.
10. Nothing about the namespace, operation names, or element structure may change
    between Lab 13 and Lab 24 without a version bump (e.g. a `/customer/v2` namespace).

## Retry semantics
- GetCustomer: safe to retry any number of times.
- CreateCustomer: NOT safe to blindly retry — a retried create after a timeout could
  produce a duplicate-ID fault on the second attempt (which is at least a safe failure,
  not a duplicate record).
- UpdateCustomer: effectively idempotent for the same target status.

## Lab 24 forward link
Lab 24 implements Spring-WS `@Endpoint` methods against this exact contract, wired to
`CustomerService` from Labs 12/15. No XML shape here changes when that happens.
```

## README handoff checklist

```markdown
| # | Confirm | Result |
| - | ------- | ------ |
| 1 | Namespace URI published (`http://northstar.com/crm/customer`) | Pass |
| 2 | WSDL location placeholder documented as non-live | Pass |
| 3 | Three operations named and described | Pass |
| 4 | Sample success envelopes for CUS-1001 / CUS-1002 | Pass |
| 5 | Fault examples for not-found and validation | Pass |
| 6 | Correlation ID convention (`lab-request-001`) | Pass |
| 7 | Explicit note: implementation arrives in Lab 24 | Pass |
| 8 | Well-formedness checked via PowerShell `[xml]` | Pass |
```

```powershell
[xml](Get-Content -Raw contracts\customer.xsd) | Out-Null
[xml](Get-Content -Raw contracts\CustomerService.wsdl) | Out-Null
```

## `notes/lab13-answers.md`

### Security and Production Review
1. Untrusted: all body fields (`fullName`, `email`, `status`, etc.) — anything the partner sends.
2. Auth/validation later: schema enforces shape now; WS-Security and service-level rules are future work, documented but not built.
3. Sensitive values: none — samples stay fictional (Amina/Ravi) on purpose.
4. Safe to retry: Get, always; Create only with an idempotency key design not built yet.
5. After failure: a Fault response, no half-created customer implied by any sample.
6. What ops monitors later: fault rate and latency once Lab 24 actually hosts this.
7. Unacceptable in production: the `http://` placeholder with zero auth — that's explicitly why it's labeled placeholder.
8. Contract versioning: namespace string bump (`.../customer/v2`) is the mechanism.

### Reflection Questions
1. **Decision that most affected usability:** making `correlationId` optional on every element instead of a required WS-Addressing header — much lower barrier for a partner to adopt.
2. **Hardest failure to diagnose:** namespace mismatch — a wrong `xmlns:cus` value produces a confusing "unknown element" rather than an obvious namespace error.
3. **Evidence it's implementable in Lab 24:** the WSDL's `portType`/`binding`/`service` already fully describe three callable operations — Lab 24 just needs `@Endpoint` methods matching these exact signatures.
4. **What breaks first at 10x fields without versioning:** partners' generated client code starts silently mismatching fields, since nothing forces them to regenerate against a new schema version.
5. **What moves to shared infra:** WSDL hosting itself, and WS-Security policy enforcement — both belong at a gateway/infrastructure layer, not hand-rolled per service.
6. **What must change before real data:** actual auth (WS-Security or a gateway), plus a live, non-placeholder endpoint.
7. **Connection to Labs 8–12/24:** same customer/status vocabulary carried forward unchanged; Lab 24 is literally "go implement this exact file."
8. **Metric that matters once live:** fault rate broken down by fault code (`CUSTOMER_NOT_FOUND` vs `VALIDATION_FAILED`) — tells you immediately whether partners are sending bad data or looking up stale IDs.
9. **What stays conceptually identical if REST arrives later:** the three operations' semantics (create/update/get) and the stable `CUS-####` identity — only the wire format changes, not the underlying contract shape.