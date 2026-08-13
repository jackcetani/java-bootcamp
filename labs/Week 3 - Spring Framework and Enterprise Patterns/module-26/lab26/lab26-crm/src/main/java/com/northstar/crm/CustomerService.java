package com.northstar.crm;

import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.exception.CustomerNotFoundException;
import com.northstar.crm.exception.DuplicateCustomerException;
import com.northstar.crm.exception.IllegalStatusTransitionException;
import com.northstar.crm.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
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

  public Customer getRequired(String customerId) {
    return customers.findByCustomerId(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(customerId));
  }

  public Customer create(Customer customer) {
    if (customers.existsByCustomerId(customer.getCustomerId())) {
      throw new DuplicateCustomerException(customer.getCustomerId());
    }
    return customers.save(customer);
  }

  public Customer updateStatus(String customerId, CustomerStatus next) {
    Customer c = getRequired(customerId);
    if (!ALLOWED.getOrDefault(c.getStatus(), Set.of()).contains(next)) {
      throw new IllegalStatusTransitionException(c.getStatus(), next);
    }
    c.setStatus(next);
    return customers.save(c);
  }

  public List<Customer> list() {
    return customers.findAll();
  }
}