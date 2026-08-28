package com.northstar.crm.dto;

import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;

public record CustomerResponse(String customerId, String fullName, String email, CustomerStatus status) {
    public static CustomerResponse from(Customer c) {
        return new CustomerResponse(c.getCustomerId(), c.getFullName(), c.getEmail(), c.getStatus());
    }
}