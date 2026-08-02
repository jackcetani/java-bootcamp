package com.northstar.crm.service;

import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.repository.InMemoryCustomerRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomerValidatorTest {

    @Test
    void allowsProspectToActive() {
        var validator = new CustomerValidator(new InMemoryCustomerRepository());
        assertDoesNotThrow(() ->
                validator.validateTransition(CustomerStatus.PROSPECT, CustomerStatus.ACTIVE, "lab-request-001"));
    }

    @Test
    void rejectsActiveToProspect() {
        var validator = new CustomerValidator(new InMemoryCustomerRepository());
        assertThrows(IllegalStateException.class, () ->
                validator.validateTransition(CustomerStatus.ACTIVE, CustomerStatus.PROSPECT, "lab-request-001"));
    }

    @Test
    void rejectsClosedToActive() {
        var validator = new CustomerValidator(new InMemoryCustomerRepository());
        assertThrows(IllegalStateException.class, () ->
                validator.validateTransition(CustomerStatus.CLOSED, CustomerStatus.ACTIVE, "lab-request-001"));
    }
}