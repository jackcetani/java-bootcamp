package com.northstar.crm.exception;

import com.northstar.crm.entity.CustomerStatus;

public class InvalidStatusTransitionException extends BusinessException {
    public InvalidStatusTransitionException(CustomerStatus from, CustomerStatus to) {
        super("Illegal status transition " + from + " -> " + to);
    }
}