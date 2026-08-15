package com.northstar.crm.service;

import com.northstar.crm.dto.CustomerRequest;
import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.exception.CustomerNotFoundException;
import com.northstar.crm.exception.DuplicateCustomerException;
import com.northstar.crm.exception.InvalidStatusTransitionException;
import com.northstar.crm.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Service
public class CustomerService {
  private static final Map<CustomerStatus, Set<CustomerStatus>> ALLOWED = new EnumMap<>(CustomerStatus.class);
  static {
    ALLOWED.put(CustomerStatus.PROSPECT, EnumSet.of(CustomerStatus.ACTIVE, CustomerStatus.CLOSED));
    ALLOWED.put(CustomerStatus.ACTIVE, EnumSet.of(CustomerStatus.SUSPENDED, CustomerStatus.CLOSED));
    ALLOWED.put(CustomerStatus.SUSPENDED, EnumSet.of(CustomerStatus.ACTIVE, CustomerStatus.CLOSED));
    ALLOWED.put(CustomerStatus.CLOSED, EnumSet.noneOf(CustomerStatus.class));
  }

  private final CustomerRepository customers;

  public CustomerService(CustomerRepository customers) {
    this.customers = customers;
  }

  public Customer create(CustomerRequest request) {
    if (customers.existsByCustomerId(request.customerId())) {
      throw new DuplicateCustomerException(request.customerId());
    }
    Customer customer = new Customer(request.customerId(), request.fullName(), request.email(), request.status());
    return customers.save(customer);
  }

  public Customer get(String customerId) {
    return customers.findByCustomerId(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(customerId));
  }

  public Customer updateStatus(String customerId, CustomerStatus next) {
    Customer c = get(customerId);
    if (!ALLOWED.getOrDefault(c.getStatus(), Set.of()).contains(next)) {
      throw new InvalidStatusTransitionException(c.getStatus(), next);
    }
    c.setStatus(next);
    return customers.save(c);
  }
}