package com.northstar.crm.service;

import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.exception.BusinessException;
import com.northstar.crm.repository.CustomerRepository;
import com.northstar.crm.repository.InMemoryCustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceTests {

    private CustomerRepository repository;
    private CustomerValidator validator;
    private CustomerService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCustomerRepository();
        validator = new CustomerValidator(repository);
        service = new DefaultCustomerService(repository, validator);
    }

    private Customer amina() {
        Customer c = new Customer();
        c.setCustomerId("CUS-1001");
        c.setFullName("Amina Khan");
        c.setEmail("amina.khan@example.com");
        c.setStatus(CustomerStatus.ACTIVE);
        return c;
    }

    private Customer ravi() {
        Customer c = new Customer();
        c.setCustomerId("CUS-1002");
        c.setFullName("Ravi Singh");
        c.setEmail("ravi.singh@example.com");
        c.setStatus(CustomerStatus.PROSPECT);
        return c;
    }

    @Test
    void addAndFindAmina() {
        service.addCustomer(amina());
        assertEquals("Amina Khan", service.findById("CUS-1001").orElseThrow().getFullName());
    }

    @Test
    void activateRaviFromProspect() {
        service.addCustomer(ravi());
        var activated = service.changeStatus("CUS-1002", CustomerStatus.ACTIVE, "lab-request-001");
        assertEquals(CustomerStatus.ACTIVE, activated.getStatus());
    }

    @Test
    void duplicateCustomerIdRejected() {
        service.addCustomer(amina());
        Customer dup = amina();
        assertThrows(BusinessException.class, () -> service.addCustomer(dup));
    }

    @Test
    void illegalTransitionThrowsAndLeavesStatusUnchanged() {
        service.addCustomer(amina());
        assertThrows(BusinessException.class, () ->
                service.changeStatus("CUS-1001", CustomerStatus.PROSPECT, "lab-request-001"));
        assertEquals(CustomerStatus.ACTIVE, service.findById("CUS-1001").orElseThrow().getStatus());
    }

    @Test
    void changeStatusOnUnknownCustomerThrowsNotFound() {
        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.changeStatus("CUS-9999", CustomerStatus.ACTIVE, "lab-request-001"));
        assertEquals("CUSTOMER_NOT_FOUND", ex.getCode());
        assertEquals("lab-request-001", ex.getCorrelationId());
    }

    @Test
    void listAllReturnsBothCustomers() {
        service.addCustomer(amina());
        service.addCustomer(ravi());
        assertEquals(2, service.listAll().size());
    }

    @Test
    void duplicateEmailRejected() {
        service.addCustomer(amina());
        Customer sameEmail = ravi();
        sameEmail.setEmail("amina.khan@example.com");
        assertThrows(BusinessException.class, () -> service.addCustomer(sameEmail));
    }
}