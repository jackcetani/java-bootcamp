package com.northstar.crm.service;

import com.northstar.crm.model.Customer;
import com.northstar.crm.repository.CustomerRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
  private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

  private final CustomerRepository repository;
  private final NotificationService notifications;

  public CustomerService(CustomerRepository repository, NotificationService notifications) {
    this.repository = repository;
    this.notifications = notifications;
  }

  public Customer create(Customer customer, String correlationId) {
    if (customer.getId() == null || customer.getId().isBlank()) {
      throw new IllegalArgumentException("customerId is required");
    }
    if (customer.getName() == null || customer.getName().isBlank()) {
      throw new IllegalArgumentException("fullName is required");
    }
    Customer saved = repository.save(customer);
    notifications.notifyCreated(saved.getId(), correlationId);
    return saved;
  }

  public Customer get(String id) {
    return repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
  }

  @PostConstruct
  void init() {
    log.info("CustomerService ready");
  }

  @PreDestroy
  void shutdown() {
    log.info("CustomerService shutting down");
  }
}