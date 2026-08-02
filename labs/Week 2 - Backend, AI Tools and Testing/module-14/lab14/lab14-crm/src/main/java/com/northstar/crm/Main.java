package com.northstar.crm;

import com.northstar.crm.api.CustomerApiFacade;
import com.northstar.crm.dto.CustomerRequestDTO;
import com.northstar.crm.dto.CustomerResponseDTO;
import com.northstar.crm.service.CustomerService;

public class Main {
    public static void main(String[] args) {
        CustomerApiFacade api = new CustomerApiFacade(new CustomerService());
        String correlationId = "lab-request-001";

        CustomerRequestDTO amina = new CustomerRequestDTO();
        amina.setCustomerId("CUS-1001");
        amina.setFullName("Amina Khan");
        amina.setEmail("amina.khan@example.com");
        amina.setStatus("Active");
        CustomerResponseDTO aminaResponse = api.create(amina, correlationId);
        System.out.println("Created: " + aminaResponse.getCustomerId() + " "
                + aminaResponse.getFullName() + " " + aminaResponse.getStatus());

        CustomerRequestDTO ravi = new CustomerRequestDTO();
        ravi.setCustomerId("CUS-1002");
        ravi.setFullName("Ravi Singh");
        ravi.setEmail("ravi.singh@example.com");
        ravi.setStatus("PROSPECT");
        CustomerResponseDTO raviResponse = api.create(ravi, correlationId);
        System.out.println("Created: " + raviResponse.getCustomerId() + " "
                + raviResponse.getFullName() + " " + raviResponse.getStatus());

        CustomerRequestDTO invalid = new CustomerRequestDTO();
        invalid.setCustomerId("CUS-1003");
        invalid.setFullName("Bad Email Test");
        invalid.setEmail("not-an-email");
        invalid.setStatus("PROSPECT");
        try {
            api.create(invalid, correlationId);
        } catch (IllegalArgumentException ex) {
            System.out.println("expected failure: " + ex.getMessage());
        }
    }
}