package com.northstar.crm.dto;

import com.northstar.crm.entity.CustomerStatus;
import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
        @NotBlank String customerId,
        @NotBlank String fullName,
        String email,
        CustomerStatus status) {
}