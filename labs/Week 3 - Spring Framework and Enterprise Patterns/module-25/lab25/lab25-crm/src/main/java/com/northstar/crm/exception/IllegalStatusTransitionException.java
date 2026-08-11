package com.northstar.crm.exception;

import com.northstar.crm.entity.CustomerStatus;

public class IllegalStatusTransitionException extends RuntimeException {
    public IllegalStatusTransitionException(CustomerStatus from, CustomerStatus to) {
        super("Illegal status transition " + from + " -> " + to);
    }
}