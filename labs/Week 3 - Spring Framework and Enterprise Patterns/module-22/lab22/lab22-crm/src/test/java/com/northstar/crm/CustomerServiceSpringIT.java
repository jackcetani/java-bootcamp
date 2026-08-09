package com.northstar.crm;

import com.northstar.crm.model.Customer;
import com.northstar.crm.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerServiceSpringIT {

    @Autowired
    CustomerService customerService;

    @Test
    void springWiredServiceCreatesAndGetsCustomer() {
        Customer created = customerService.create(Customer.amina(), "lab-request-001");
        assertEquals("CUS-1001", created.getId());

        Customer found = customerService.get("CUS-1001");
        assertEquals("Amina Khan", found.getName());
    }
}