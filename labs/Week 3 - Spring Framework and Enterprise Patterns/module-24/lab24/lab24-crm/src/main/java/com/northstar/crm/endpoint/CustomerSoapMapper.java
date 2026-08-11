package com.northstar.crm.endpoint;

import com.northstar.crm.endpoint.jaxb.CustomerStatus;
import com.northstar.crm.endpoint.jaxb.CustomerType;
import com.northstar.crm.model.Customer;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;

@Component
public class CustomerSoapMapper {

  private final Random idSuffix = new Random();

  public CustomerType toSoap(Customer customer) {
    CustomerType type = new CustomerType();
    type.setCustomerId(customer.getId());
    type.setFullName(customer.getName());
    type.setEmail(customer.getEmail());
    type.setStatus(CustomerStatus.fromValue(customer.getStatus()));
    type.setCreatedAt(nowUtc());
    return type;
  }

  public Customer fromCreateRequest(String fullName, String email, CustomerStatus status) {
    String id = "CUS-" + (2000 + idSuffix.nextInt(7000));
    String statusValue = status != null ? status.value() : "PROSPECT";
    return new Customer(id, fullName, email, statusValue);
  }

  private XMLGregorianCalendar nowUtc() {
    try {
      GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    } catch (DatatypeConfigurationException e) {
      throw new IllegalStateException("Unable to build XML timestamp", e);
    }
  }
}