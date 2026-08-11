package com.northstar.crm.endpoint;

import com.northstar.crm.endpoint.jaxb.*;
import com.northstar.crm.model.Customer;
import com.northstar.crm.service.CustomerService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class CustomerEndpoint {
  private static final String NAMESPACE = "http://northstar.com/crm/customer";

  private final CustomerService customerService;
  private final CustomerSoapMapper mapper;

  public CustomerEndpoint(CustomerService customerService, CustomerSoapMapper mapper) {
    this.customerService = customerService;
    this.mapper = mapper;
  }

  @PayloadRoot(namespace = NAMESPACE, localPart = "getCustomerRequest")
  @ResponsePayload
  public GetCustomerResponse getCustomer(@RequestPayload GetCustomerRequest request) {
    Customer customer = customerService.get(request.getCustomerId());
    GetCustomerResponse response = new GetCustomerResponse();
    response.setCustomer(mapper.toSoap(customer));
    return response;
  }

  @PayloadRoot(namespace = NAMESPACE, localPart = "createCustomerRequest")
  @ResponsePayload
  public CreateCustomerResponse createCustomer(@RequestPayload CreateCustomerRequest request) {
    Customer customer = mapper.fromCreateRequest(
            request.getFullName(), request.getEmail(), request.getStatus());
    Customer saved = customerService.create(customer, "lab24-001");
    CreateCustomerResponse response = new CreateCustomerResponse();
    response.setCustomer(mapper.toSoap(saved));
    return response;
  }

  @PayloadRoot(namespace = NAMESPACE, localPart = "updateCustomerStatusRequest")
  @ResponsePayload
  public UpdateCustomerStatusResponse updateCustomerStatus(
          @RequestPayload UpdateCustomerStatusRequest request) {
    Customer updated = customerService.updateStatus(
            request.getCustomerId(), request.getStatus().value());
    UpdateCustomerStatusResponse response = new UpdateCustomerStatusResponse();
    response.setCustomer(mapper.toSoap(updated));
    return response;
  }

  @PayloadRoot(namespace = NAMESPACE, localPart = "listCustomersRequest")
  @ResponsePayload
  public ListCustomersResponse listCustomers(@RequestPayload ListCustomersRequest request) {
    ListCustomersResponse response = new ListCustomersResponse();
    for (Customer c : customerService.list()) {
      response.getCustomer().add(mapper.toSoap(c));
    }
    return response;
  }
}