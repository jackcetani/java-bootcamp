package com.northstar.crm.dto;

import com.northstar.crm.entity.CustomerStatus;

public record StatusUpdateRequest(CustomerStatus status) {
}