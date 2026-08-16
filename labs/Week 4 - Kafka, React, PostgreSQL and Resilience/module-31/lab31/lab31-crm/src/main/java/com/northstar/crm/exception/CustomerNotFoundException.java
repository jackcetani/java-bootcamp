package com.northstar.crm.exception;

public class CustomerNotFoundException extends BusinessException {
    public CustomerNotFoundException(String customerId) {
        super("Customer not found: " + customerId);
    }
}