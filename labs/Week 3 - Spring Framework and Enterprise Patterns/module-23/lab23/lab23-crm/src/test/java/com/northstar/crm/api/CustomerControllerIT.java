package com.northstar.crm.api;

import com.northstar.crm.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerIT {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @Test
    void createAndGetCus1001() {
        var headers = new HttpHeaders();
        headers.set("X-Correlation-Id", "lab-request-001");
        headers.setContentType(MediaType.APPLICATION_JSON);
        var body = "{\"id\":\"CUS-1001\",\"name\":\"Amina Khan\",\"status\":\"ACTIVE\"}";
        var created = rest.postForEntity(
                "http://localhost:" + port + "/api/customers",
                new HttpEntity<>(body, headers),
                Customer.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(rest.getForEntity("http://localhost:" + port + "/api/customers/CUS-1001", Customer.class)
                .getBody().getId()).isEqualTo("CUS-1001");
    }

    @Test
    void missingCustomerReturns404() {
        var response = rest.getForEntity(
                "http://localhost:" + port + "/api/customers/CUS-MISSING", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}