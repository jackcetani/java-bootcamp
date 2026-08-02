package com.northstar.crm;

import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.service.CustomerService;

public class Main {
    public static void main(String[] args) {
        System.out.println("Lab 12 — refactor CustomerService (doStuff → clean API)");
        CustomerService service = new CustomerService();
        service.createCustomer("CUS-1001", "Amina Khan", "amina.khan@example.com", null, CustomerStatus.ACTIVE);
        service.createCustomer("CUS-1002", "Ravi Singh", "ravi.singh@example.com", null, CustomerStatus.PROSPECT);

        System.out.println("Amina: " + service.getCustomer("CUS-1001").getFullName());
        service.updateStatus("CUS-1002", CustomerStatus.ACTIVE);
        System.out.println("Ravi status: " + service.getCustomer("CUS-1002").getStatus());

        try {
            service.createCustomer("CUS-1001", "Dup", "x@example.com", null, CustomerStatus.PROSPECT);
        } catch (IllegalStateException ex) {
            System.out.println("expected: " + ex.getMessage());
        }
        try {
            service.getCustomer("CUS-9999");
        } catch (IllegalArgumentException ex) {
            System.out.println("expected: " + ex.getMessage());
        }
    }
}
