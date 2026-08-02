package com.northstar.crm.mapper;

import com.northstar.crm.dto.CustomerRequestDTO;
import com.northstar.crm.dto.CustomerResponseDTO;
import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;

import java.time.ZoneOffset;

public final class CustomerMapper {
    private CustomerMapper() {}

    public static Customer toEntity(CustomerRequestDTO req) {
        Customer customer = new Customer();
        customer.setCustomerId(req.getCustomerId());
        customer.setFullName(req.getFullName());
        customer.setEmail(req.getEmail());
        customer.setStatus(CustomerStatus.valueOf(req.getStatus()));
        return customer;
    }

    public static CustomerResponseDTO toResponse(Customer entity) {
        return CustomerResponseDTO.of(
                entity.getCustomerId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getStatus().name(),
                entity.getCreatedAt() != null
                        ? entity.getCreatedAt().toInstant(ZoneOffset.UTC) : null,
                null);
    }
}