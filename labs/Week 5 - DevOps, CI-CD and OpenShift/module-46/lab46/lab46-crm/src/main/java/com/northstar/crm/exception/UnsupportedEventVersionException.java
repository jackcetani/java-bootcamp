package com.northstar.crm.exception;

public class UnsupportedEventVersionException extends RuntimeException {
    public UnsupportedEventVersionException(int eventVersion) {
        super("Unsupported event version: " + eventVersion);
    }
}