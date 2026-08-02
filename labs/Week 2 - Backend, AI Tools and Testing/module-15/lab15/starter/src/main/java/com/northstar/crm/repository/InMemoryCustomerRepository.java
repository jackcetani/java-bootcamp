package com.northstar.crm.repository;

import com.northstar.crm.entity.Customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryCustomerRepository implements CustomerRepository {
    private final Map<String, Customer> customersById = new HashMap<>();

    @Override
    public Customer save(Customer customer) {
        customersById.put(customer.getCustomerId(), customer);
        return customer;
    }

    @Override
    public Optional<Customer> findById(String customerId) {
        return Optional.ofNullable(customersById.get(customerId));
    }

    @Override
    public boolean existsById(String customerId) {
        return customersById.containsKey(customerId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return customersById.values().stream()
                .anyMatch(c -> c.getEmail() != null && c.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(customersById.values());
    }
}