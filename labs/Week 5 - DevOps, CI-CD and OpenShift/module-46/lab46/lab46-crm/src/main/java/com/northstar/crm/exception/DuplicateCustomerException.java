package com.northstar.crm.exception;

public class DuplicateCustomerException extends BusinessException {
    public DuplicateCustomerException(String customerId) {
        super("Duplicate customer: " + customerId);
    }
}