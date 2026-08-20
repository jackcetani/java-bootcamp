package com.northstar.crm.service;

import com.northstar.crm.dto.CustomerRequest;
import com.northstar.crm.dto.CustomerUpdateRequest;
import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.event.CustomerData;
import com.northstar.crm.event.CustomerEvent;
import com.northstar.crm.event.CustomerEventPublisher;
import com.northstar.crm.exception.CustomerNotFoundException;
import com.northstar.crm.exception.DuplicateCustomerException;
import com.northstar.crm.exception.InvalidStatusTransitionException;
import com.northstar.crm.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class CustomerService {
  private static final Map<CustomerStatus, Set<CustomerStatus>> ALLOWED = new EnumMap<>(CustomerStatus.class);
  private static final String EVENT_SOURCE = "customer-service";

  static {
    ALLOWED.put(CustomerStatus.PROSPECT, EnumSet.of(CustomerStatus.ACTIVE, CustomerStatus.CLOSED));
    ALLOWED.put(CustomerStatus.ACTIVE, EnumSet.of(CustomerStatus.SUSPENDED, CustomerStatus.CLOSED));
    ALLOWED.put(CustomerStatus.SUSPENDED, EnumSet.of(CustomerStatus.ACTIVE, CustomerStatus.CLOSED));
    ALLOWED.put(CustomerStatus.CLOSED, EnumSet.noneOf(CustomerStatus.class));
  }

  private final CustomerRepository customers;
  private final CustomerEventPublisher events;

  public CustomerService(CustomerRepository customers, CustomerEventPublisher events) {
    this.customers = customers;
    this.events = events;
  }

  public Customer create(CustomerRequest request, String correlationId) {
    String customerId = (request.customerId() != null && !request.customerId().isBlank())
            ? request.customerId()
            : generateCustomerId();

    if (customers.existsByCustomerId(customerId)) {
      throw new DuplicateCustomerException(customerId);
    }
    Customer customer = new Customer(customerId, request.fullName(), request.email(), request.phone(), request.status());
    Customer saved = customers.save(customer);

    events.publish(new CustomerEvent(
            UUID.randomUUID(),
            "CustomerCreated",
            1,
            Instant.now(),
            saved.getCustomerId(),
            correlationId,
            EVENT_SOURCE,
            new CustomerData(saved.getFullName(), null, saved.getStatus().name())));

    return saved;
  }

  public List<Customer> list() {
    return customers.findAll();
  }

  public Customer get(String customerId) {
    return customers.findByCustomerId(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(customerId));
  }

  public Customer update(String customerId, CustomerUpdateRequest request, String correlationId) {
    Customer existing = get(customerId);
    existing.setFullName(request.fullName());
    existing.setEmail(request.email());
    existing.setPhone(request.phone());
    existing.setStatus(request.status());
    return customers.save(existing);
  }

  public Customer updateStatus(String customerId, CustomerStatus next, String correlationId) {
    Customer c = get(customerId);
    CustomerStatus previous = c.getStatus();
    if (!ALLOWED.getOrDefault(previous, Set.of()).contains(next)) {
      throw new InvalidStatusTransitionException(previous, next);
    }
    c.setStatus(next);
    Customer saved = customers.save(c);

    events.publish(new CustomerEvent(
            UUID.randomUUID(),
            "CustomerStatusChanged",
            1,
            Instant.now(),
            saved.getCustomerId(),
            correlationId,
            EVENT_SOURCE,
            new CustomerData(null, previous.name(), saved.getStatus().name())));

    return saved;
  }

  private String generateCustomerId() {
    return "CUS-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
  }
}