package com.northstar.crm.api;

import com.northstar.crm.dto.CustomerRequest;
import com.northstar.crm.dto.CustomerResponse;
import com.northstar.crm.dto.StatusUpdateRequest;
import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.exception.CustomerNotFoundException;
import com.northstar.crm.exception.DuplicateCustomerException;
import com.northstar.crm.exception.IllegalStatusTransitionException;
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

  @GetMapping("/{customerId}")
  public CustomerResponse get(
          @PathVariable String customerId,
          @RequestHeader(value = "X-Correlation-Id", defaultValue = "lab-request-001")
          String correlationId) {
    return CustomerResponse.from(service.getRequired(customerId));
  }

  @PatchMapping("/{customerId}/status")
  public CustomerResponse status(
          @PathVariable String customerId,
          @RequestBody StatusUpdateRequest body) {
    return CustomerResponse.from(service.updateStatus(customerId, body.status()));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CustomerResponse create(
          @Valid @RequestBody CustomerRequest request,
          @RequestHeader(value = "X-Correlation-Id", defaultValue = "lab-request-001")
          String correlationId) {
    CustomerStatus status = request.status() != null ? request.status() : CustomerStatus.PROSPECT;
    Customer customer = new Customer(request.customerId(), request.fullName(), request.email(), status);
    return CustomerResponse.from(service.create(customer));
  }

  @GetMapping
  public List<CustomerResponse> list() {
    return service.list().stream().map(CustomerResponse::from).toList();
  }

  @ExceptionHandler(CustomerNotFoundException.class)
  public ResponseEntity<String> handleNotFound(CustomerNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(DuplicateCustomerException.class)
  public ResponseEntity<String> handleDuplicate(DuplicateCustomerException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(IllegalStatusTransitionException.class)
  public ResponseEntity<String> handleIllegalTransition(IllegalStatusTransitionException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }
}