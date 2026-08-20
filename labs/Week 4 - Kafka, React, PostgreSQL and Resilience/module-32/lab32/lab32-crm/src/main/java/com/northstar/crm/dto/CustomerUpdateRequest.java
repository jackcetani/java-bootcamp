package com.northstar.crm.dto;

import com.northstar.crm.entity.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CustomerUpdateRequest(
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank @Email String email,
        String phone,
        @NotNull CustomerStatus status
) {}