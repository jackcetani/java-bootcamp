package com.northstar.crm.exception;

public class InvalidCustomerEventException extends RuntimeException {
    public InvalidCustomerEventException(String message) {
        super(message);
    }
}