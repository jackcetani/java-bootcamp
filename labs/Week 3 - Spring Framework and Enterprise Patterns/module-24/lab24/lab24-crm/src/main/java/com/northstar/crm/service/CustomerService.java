package com.northstar.crm.service;

import com.northstar.crm.exception.CustomerNotFoundException;
import com.northstar.crm.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CustomerService {
  private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
  private final Map<String, Customer> store = new ConcurrentHashMap<>();

  public CustomerService() {
    store.put("CUS-1001", Customer.amina());
    store.put("CUS-1002", Customer.ravi());
  }

  public Customer create(Customer customer, String correlationId) {
    log.info("createCustomer correlationId={} customerId={}", correlationId, customer.getId());
    store.put(customer.getId(), customer);
    return customer;
  }

  public Customer get(String id) {
    Customer c = store.get(id);
    if (c == null) {
      throw new CustomerNotFoundException(id);
    }
    return c;
  }

  public Customer updateStatus(String id, String status) {
    Customer c = get(id);
    c.setStatus(status);
    return c;
  }

  public List<Customer> list() {
    return List.copyOf(store.values());
  }
}