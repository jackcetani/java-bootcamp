package com.northstar.crm.service;

import com.northstar.crm.CustomerService;
import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.exception.CustomerNotFoundException;
import com.northstar.crm.exception.DuplicateCustomerException;
import com.northstar.crm.exception.IllegalStatusTransitionException;
import com.northstar.crm.repository.InMemoryCustomerRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceTest {

  @Test
  void updateStatus_movesProspectToActive() {
    var repo = new InMemoryCustomerRepository();
    var service = new CustomerService(repo);
    var updated = service.updateStatus("CUS-1002", CustomerStatus.ACTIVE);
    assertEquals(CustomerStatus.ACTIVE, updated.getStatus());
    assertEquals("Ravi Singh", updated.getFullName());
  }

  @Test
  void getRequired_throws_whenMissing() {
    var service = new CustomerService(new InMemoryCustomerRepository());
    assertThrows(CustomerNotFoundException.class,
            () -> service.getRequired("CUS-9999"));
  }

  @Test
  void create_rejectsDuplicateId() {
    var service = new CustomerService(new InMemoryCustomerRepository());
    var duplicate = new Customer("CUS-1001", "Someone Else", "x@example.com", CustomerStatus.PROSPECT);
    assertThrows(DuplicateCustomerException.class, () -> service.create(duplicate));
  }

  @Test
  void updateStatus_rejectsIllegalTransition() {
    var service = new CustomerService(new InMemoryCustomerRepository());
    assertThrows(IllegalStatusTransitionException.class,
            () -> service.updateStatus("CUS-1001", CustomerStatus.PROSPECT));
    assertEquals(CustomerStatus.ACTIVE, service.getRequired("CUS-1001").getStatus());
  }
}