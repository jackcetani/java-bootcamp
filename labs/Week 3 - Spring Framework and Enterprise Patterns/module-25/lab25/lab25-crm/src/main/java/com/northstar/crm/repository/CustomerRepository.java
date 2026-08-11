package com.northstar.crm.repository;

import com.northstar.crm.entity.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
  Customer save(Customer customer);
  Optional<Customer> findByCustomerId(String customerId);
  List<Customer> findAll();
  boolean existsByCustomerId(String customerId);
  void deleteByCustomerId(String customerId);
}