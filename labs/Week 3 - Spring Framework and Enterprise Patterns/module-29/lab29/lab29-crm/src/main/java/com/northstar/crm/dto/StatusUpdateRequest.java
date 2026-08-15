package com.northstar.crm.dto;

import com.northstar.crm.entity.CustomerStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(@NotNull CustomerStatus status) {}