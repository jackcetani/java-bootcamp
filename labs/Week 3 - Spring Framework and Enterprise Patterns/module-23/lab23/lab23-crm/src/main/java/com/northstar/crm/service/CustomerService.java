package com.northstar.crm.service;

import com.northstar.crm.model.Customer;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CustomerService {
  private final Map<String, Customer> store = new ConcurrentHashMap<>();

  public CustomerService() {
    store.put("CUS-1001", Customer.amina());
    store.put("CUS-1002", Customer.ravi());
  }

  public Customer create(Customer customer) {
    store.put(customer.getId(), customer);
    return customer;
  }

  public Optional<Customer> findById(String id) {
    return Optional.ofNullable(store.get(id));
  }
}