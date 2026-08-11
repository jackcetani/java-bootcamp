package com.northstar.crm.exception;

public class DuplicateCustomerException extends RuntimeException {
    public DuplicateCustomerException(String id) {
        super("Duplicate customer: " + id);
    }
}