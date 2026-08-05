package com.northstar.crm.integration;

import com.northstar.crm.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerApiIT {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @Test
    void getAminaReturns200() {
        ResponseEntity<Customer> response = rest.getForEntity(
                "http://localhost:" + port + "/api/customers/CUS-1001", Customer.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CUS-1001", response.getBody().getCustomerId());
    }

    @Test
    void createEchoesCorrelationHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Correlation-Id", "lab-request-001");
        headers.setContentType(MediaType.APPLICATION_JSON);
        Customer body = new Customer("CUS-3001", "Test Correlation", "test.correlation@example.com", "PROSPECT");
        HttpEntity<Customer> request = new HttpEntity<>(body, headers);

        ResponseEntity<Customer> response = rest.exchange(
                "http://localhost:" + port + "/api/customers",
                HttpMethod.POST, request, Customer.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("lab-request-001", response.getHeaders().getFirst("X-Correlation-Id"));
        assertEquals("CUS-3001", response.getBody().getCustomerId());
    }

    @Test
    void missingCustomerReturns404() {
        ResponseEntity<Customer> response = rest.getForEntity(
                "http://localhost:" + port + "/api/customers/CUS-9999", Customer.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}