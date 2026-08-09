package com.northstar.crm;

import com.northstar.crm.model.Customer;
import com.northstar.crm.repository.InMemoryCustomerRepository;
import com.northstar.crm.service.CustomerService;
import com.northstar.crm.service.NotificationService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {
  @Test
  void createAndGetWithoutSpringContext() {
    var repo = new InMemoryCustomerRepository();
    var notify = mock(NotificationService.class);
    var service = new CustomerService(repo, notify);

    service.create(Customer.amina(), "lab-request-001");

    Customer found = service.get("CUS-1001");
    assertEquals("Amina Khan", found.getName());
    verify(notify).notifyCreated("CUS-1001", "lab-request-001");
  }
}