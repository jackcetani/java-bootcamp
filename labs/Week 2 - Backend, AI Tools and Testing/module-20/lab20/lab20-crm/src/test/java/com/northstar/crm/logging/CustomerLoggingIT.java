package com.northstar.crm.logging;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(OutputCaptureExtension.class)
class CustomerLoggingIT {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @Test
    void getAminaLogsCorrelationWithoutPii(CapturedOutput output) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Correlation-Id", "lab-request-001");
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = rest.exchange(
                "http://localhost:" + port + "/api/customers/CUS-1001",
                HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(output.getOut().contains("lab-request-001"));
        assertTrue(output.getOut().contains("CUS-1001"));
        assertTrue(output.getOut().contains("customer.get"));
        assertFalse(output.getOut().contains("Amina"));
        assertFalse(output.getOut().contains("@example.com"));
    }
}
