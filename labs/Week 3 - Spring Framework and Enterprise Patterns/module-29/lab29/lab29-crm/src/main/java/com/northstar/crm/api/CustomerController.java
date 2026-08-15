package com.northstar.crm.controller;

import com.northstar.crm.dto.CustomerRequest;
import com.northstar.crm.dto.CustomerResponse;
import com.northstar.crm.dto.StatusUpdateRequest;
import com.northstar.crm.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
  private final CustomerService service;

  public CustomerController(CustomerService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<CustomerResponse> create(
          @Valid @RequestBody CustomerRequest request,
          @RequestHeader(value = "X-Correlation-Id", defaultValue = "lab-request-001") String correlationId) {
    var saved = service.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(CustomerResponse.from(saved));
  }

  @GetMapping("/{customerId}")
  public CustomerResponse get(@PathVariable String customerId) {
    return CustomerResponse.from(service.get(customerId));
  }

  @PatchMapping("/{customerId}/status")
  public CustomerResponse updateStatus(
          @PathVariable String customerId,
          @Valid @RequestBody StatusUpdateRequest request) {
    return CustomerResponse.from(service.updateStatus(customerId, request.status()));
  }
}