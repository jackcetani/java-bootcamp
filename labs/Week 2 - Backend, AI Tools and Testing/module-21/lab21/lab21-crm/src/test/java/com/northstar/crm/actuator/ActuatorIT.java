package com.northstar.crm.actuator;

import com.northstar.crm.health.CrmReadinessIndicator;
import com.northstar.crm.model.Customer;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ActuatorIT {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @Autowired
    CrmReadinessIndicator readiness;

    @Test
    void healthAndProbesAreUp() {
        ResponseEntity<Map> health = rest.getForEntity("http://localhost:" + port + "/actuator/health", Map.class);
        assertTrue(health.getStatusCode().is2xxSuccessful());
        assertEquals("UP", health.getBody().get("status"));

        ResponseEntity<Map> liveness = rest.getForEntity(
                "http://localhost:" + port + "/actuator/health/liveness", Map.class);
        assertTrue(liveness.getStatusCode().is2xxSuccessful());
        assertEquals("UP", liveness.getBody().get("status"));

        ResponseEntity<Map> readinessResponse = rest.getForEntity(
                "http://localhost:" + port + "/actuator/health/readiness", Map.class);
        assertTrue(readinessResponse.getStatusCode().is2xxSuccessful());
        assertEquals("UP", readinessResponse.getBody().get("status"));
    }

    @Test
    void readinessCanGoDownWhileLivenessStaysUp() {
        readiness.setReady(false);
        try {
            ResponseEntity<Map> readinessResponse = rest.getForEntity(
                    "http://localhost:" + port + "/actuator/health/readiness", Map.class);
            assertNotEquals("UP", readinessResponse.getBody().get("status"));

            ResponseEntity<Map> livenessResponse = rest.getForEntity(
                    "http://localhost:" + port + "/actuator/health/liveness", Map.class);
            assertEquals("UP", livenessResponse.getBody().get("status"));
        } finally {
            readiness.setReady(true); // always restore, even if an assertion above failed
        }
    }

    @Test
    void createMetricAppearsAfterTraffic() {
        Customer body = new Customer("CUS-6001", "Metrics Test", "metrics.test@example.com", "PROSPECT");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Correlation-Id", "lab-request-001");
        HttpEntity<Customer> request = new HttpEntity<>(body, headers);

        rest.exchange("http://localhost:" + port + "/api/customers", HttpMethod.POST, request, Customer.class);

        ResponseEntity<Map> metricsResponse = rest.getForEntity(
                "http://localhost:" + port + "/actuator/metrics/crm.customer.create", Map.class);
        assertTrue(metricsResponse.getStatusCode().is2xxSuccessful());
        assertNotNull(metricsResponse.getBody());
        assertTrue(metricsResponse.getBody().containsKey("measurements"));
    }
}