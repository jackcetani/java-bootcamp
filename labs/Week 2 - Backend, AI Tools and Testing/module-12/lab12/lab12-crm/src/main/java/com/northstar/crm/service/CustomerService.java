package com.northstar.crm.service;

import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CustomerService {

    private final Map<String, Customer> customersById = new HashMap<>();
    private String correlationId = "lab-request-001";

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    private String correlationId() {
        return correlationId;
    }

    public Customer createCustomer(String customerId, String fullName, String email,
                                   String phone, CustomerStatus status) {
        requireNonBlank(customerId, "customerId");
        requireNonBlank(fullName, "fullName");
        requireUniqueId(customerId);

        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setFullName(fullName);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setStatus(status != null ? status : CustomerStatus.PROSPECT);
        customer.setCreatedAt(LocalDateTime.now());

        customersById.put(customerId, customer);
        return customer;
    }

    public Customer getCustomer(String customerId) {
        Customer found = customersById.get(customerId);
        if (found == null) {
            throw new IllegalArgumentException(
                    "Customer not found: " + customerId + " correlationId=" + correlationId());
        }
        return found;
    }

    public Customer updateStatus(String customerId, CustomerStatus newStatus) {
        Customer customer = requireExisting(customerId);
        customer.setStatus(newStatus);
        return customer;
    }

    private void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }

    private void requireUniqueId(String customerId) {
        if (customersById.containsKey(customerId)) {
            throw new IllegalStateException(
                    "duplicate customerId: " + customerId + " correlationId=" + correlationId());
        }
    }

    private Customer requireExisting(String customerId) {
        Customer found = customersById.get(customerId);
        if (found == null) {
            throw new IllegalArgumentException(
                    "Customer not found: " + customerId + " correlationId=" + correlationId());
        }
        return found;
    }
}