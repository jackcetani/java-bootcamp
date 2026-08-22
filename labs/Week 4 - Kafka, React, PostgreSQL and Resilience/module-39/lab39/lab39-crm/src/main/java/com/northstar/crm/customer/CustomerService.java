package com.northstar.crm.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

  private final CustomerRepository repository;

  public CustomerService(CustomerRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public CustomerEntity create(String publicId, String fullName, String email, String status) {
    CustomerEntity entity = new CustomerEntity();
    entity.setPublicId(publicId);
    entity.setFullName(fullName);
    entity.setEmail(email);
    entity.setStatus(status);
    return repository.save(entity);
  }

  @Transactional(readOnly = true)
  public Page<CustomerEntity> pageByStatus(String status, int page, int size) {
    var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending().and(Sort.by("customerId")));
    return repository.findByStatus(status, pageable);
  }
}