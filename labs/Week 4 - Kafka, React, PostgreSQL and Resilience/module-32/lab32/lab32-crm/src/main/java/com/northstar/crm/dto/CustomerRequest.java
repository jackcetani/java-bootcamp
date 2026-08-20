package com.northstar.crm.dto;

import com.northstar.crm.entity.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @Pattern(regexp = "CUS-\\d{4}") String customerId,
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank @Email String email,
        String phone,
        @NotNull CustomerStatus status
) {}