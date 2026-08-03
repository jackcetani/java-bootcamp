package com.northstar.crm;

import com.northstar.crm.api.ApiResult;
import com.northstar.crm.api.CustomerApiFacade;
import com.northstar.crm.dto.CustomerRequestDTO;
import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.repository.CustomerRepository;
import com.northstar.crm.repository.InMemoryCustomerRepository;
import com.northstar.crm.service.CustomerService;
import com.northstar.crm.service.CustomerValidator;
import com.northstar.crm.service.DefaultCustomerService;

public class Main {
    public static void main(String[] args) {
        CustomerRepository repo = new InMemoryCustomerRepository();
        CustomerValidator validator = new CustomerValidator(repo);
        CustomerService service = new DefaultCustomerService(repo, validator);
        CustomerApiFacade api = new CustomerApiFacade(service);
        String correlationId = "lab-request-001";

        Customer amina = new Customer();
        amina.setCustomerId("CUS-1001");
        amina.setFullName("Amina Khan");
        amina.setEmail("amina.khan@example.com");
        amina.setStatus(CustomerStatus.ACTIVE);
        service.addCustomer(amina);

        Customer ravi = new Customer();
        ravi.setCustomerId("CUS-1002");
        ravi.setFullName("Ravi Singh");
        ravi.setEmail("ravi.singh@example.com");
        ravi.setStatus(CustomerStatus.PROSPECT);
        service.addCustomer(ravi);

        // 400 — validation
        CustomerRequestDTO invalid = new CustomerRequestDTO();
        invalid.setCustomerId("CUS-1003");
        invalid.setFullName("Bad Email");
        invalid.setEmail("not-an-email");
        invalid.setStatus("PROSPECT");
        ApiResult badResult = api.create(invalid, correlationId);
        if (badResult instanceof ApiResult.Fail fail) {
            System.out.println(fail.error().toJson());
        }

        // 404 — not found
        ApiResult notFound = api.getById("CUS-9999", correlationId);
        if (notFound instanceof ApiResult.Fail fail) {
            System.out.println(fail.error().toJson());
        }


        // 409 — illegal transition
        ApiResult conflict = api.changeStatus("CUS-1001", CustomerStatus.PROSPECT, correlationId);
        if (conflict instanceof ApiResult.Fail fail) {
            System.out.println(fail.error().toJson());
        }
        System.out.println("CUS-1001 still: "
                + service.findById("CUS-1001").orElseThrow().getStatus());
    }
}