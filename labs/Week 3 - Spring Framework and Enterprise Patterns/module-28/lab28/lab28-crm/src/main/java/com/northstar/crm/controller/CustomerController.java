package com.northstar.crm.controller;

import com.northstar.crm.model.Customer;
import com.northstar.crm.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
  private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
  private final CustomerService customerService;

  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @GetMapping("/{id}")
  public Customer get(
          @PathVariable String id,
          @RequestHeader(value = "X-Correlation-Id", defaultValue = "lab-request-001") String correlationId) {
    log.info("get customer id={} correlationId={}", id, correlationId);
    return customerService.get(id);
  }
}