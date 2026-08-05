package com.northstar.crm.api;

import com.northstar.crm.model.Customer;
import com.northstar.crm.service.CustomerService;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customers;

    public CustomerController(CustomerService customers) {
        this.customers = customers;
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId,
            @RequestBody Customer body) {
        String effectiveCorrelationId = correlationId != null ? correlationId : "lab-request-001";
        try {
            Customer created = customers.create(body, effectiveCorrelationId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("X-Correlation-Id", effectiveCorrelationId)
                    .body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .header("X-Correlation-Id", effectiveCorrelationId)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> get(@PathVariable String id) {
        return customers.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}