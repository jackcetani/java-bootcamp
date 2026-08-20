package com.northstar.crm.entity;

public class Customer {
  private String customerId;
  private String fullName;
  private String email;
  private String phone;
  private CustomerStatus status;

  public Customer() {}

  public Customer(String customerId, String fullName, String email, String phone, CustomerStatus status) {
    this.customerId = customerId;
    this.fullName = fullName;
    this.email = email;
    this.phone = phone;
    this.status = status;
  }

  public static Customer amina() {
    return new Customer("CUS-1001", "Amina Khan", "amina@example.com", "+1-555-0101", CustomerStatus.ACTIVE);
  }

  public static Customer ravi() {
    return new Customer("CUS-1002", "Ravi Singh", "ravi@example.com", "+1-555-0102", CustomerStatus.PROSPECT);
  }

  public String getCustomerId() { return customerId; }
  public void setCustomerId(String customerId) { this.customerId = customerId; }
  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public CustomerStatus getStatus() { return status; }
  public void setStatus(CustomerStatus status) { this.status = status; }
}