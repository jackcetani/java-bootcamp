package com.northstar.crm;

import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.repository.CustomerRepository;
import com.northstar.crm.repository.InMemoryCustomerRepository;
import com.northstar.crm.service.CustomerService;
import com.northstar.crm.service.CustomerValidator;
import com.northstar.crm.service.DefaultCustomerService;

public class Main {
    public static void main(String[] args) {
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
    }
}
