# Operation matrix — Lab 13

| Operation      | Purpose                        | Key inputs                       | Key outputs        |
|----------------|--------------------------------|----------------------------------|--------------------|
| CreateCustomer | Register a new CRM customer    | fullName, email, phone?, status? | customer (with ID) |
| UpdateCustomer | Change mutable fields / status | customerId, optional fields      | customer           |
| GetCustomer    | Fetch one customer by ID       | customerId                       | customer           |

Namespace: `http://northstar.com/crm/customer`  
Endpoint placeholder: `http://localhost:8080/ws` (**not live** in this lab)
