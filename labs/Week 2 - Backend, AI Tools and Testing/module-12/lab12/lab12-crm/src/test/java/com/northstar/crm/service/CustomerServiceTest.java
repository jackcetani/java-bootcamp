package com.northstar.crm.service;

import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceTest {

    @Test
    void createAminaKhanThenGetById() {
        CustomerService svc = new CustomerService();
        Customer created = svc.createCustomer(
                "CUS-1001", "Amina Khan", "amina.khan@example.com", null, CustomerStatus.ACTIVE);
        assertEquals("CUS-1001", created.getCustomerId());
        assertEquals(CustomerStatus.ACTIVE, created.getStatus());
        assertEquals("Amina Khan", svc.getCustomer("CUS-1001").getFullName());
    }

    @Test
    void duplicateIdRejected() {
        CustomerService svc = new CustomerService();
        svc.createCustomer("CUS-1002", "Ravi Singh", "ravi.singh@example.com", null, CustomerStatus.PROSPECT);
        assertThrows(IllegalStateException.class, () ->
                svc.createCustomer("CUS-1002", "Other", "x@example.com", null, CustomerStatus.PROSPECT));
    }

    @Test
    void unknownCustomerFailsClearly() {
        CustomerService svc = new CustomerService();
        assertThrows(IllegalArgumentException.class, () -> svc.getCustomer("CUS-9999"));
    }
}