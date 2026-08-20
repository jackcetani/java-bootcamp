package com.northstar.crm.controller;

import com.northstar.crm.dto.CustomerRequest;
import com.northstar.crm.dto.CustomerResponse;
import com.northstar.crm.dto.CustomerUpdateRequest;
import com.northstar.crm.dto.StatusUpdateRequest;
import com.northstar.crm.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public List<CustomerResponse> list() {
        return service.list().stream().map(CustomerResponse::from).toList();
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(
            @Valid @RequestBody CustomerRequest request,
            @RequestHeader(value = "X-Correlation-Id", defaultValue = "lab-request-001") String correlationId) {
        var saved = service.create(request, correlationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerResponse.from(saved));
    }

    @GetMapping("/{customerId}")
    public CustomerResponse get(@PathVariable String customerId) {
        return CustomerResponse.from(service.get(customerId));
    }

    @PutMapping("/{customerId}")
    public CustomerResponse update(
            @PathVariable String customerId,
            @Valid @RequestBody CustomerUpdateRequest request,
            @RequestHeader(value = "X-Correlation-Id", defaultValue = "lab-request-001") String correlationId) {
        return CustomerResponse.from(service.update(customerId, request, correlationId));
    }

    @PatchMapping("/{customerId}/status")
    public CustomerResponse updateStatus(
            @PathVariable String customerId,
            @Valid @RequestBody StatusUpdateRequest request,
            @RequestHeader(value = "X-Correlation-Id", defaultValue = "lab-request-001") String correlationId) {
        return CustomerResponse.from(service.updateStatus(customerId, request.status(), correlationId));
    }
}