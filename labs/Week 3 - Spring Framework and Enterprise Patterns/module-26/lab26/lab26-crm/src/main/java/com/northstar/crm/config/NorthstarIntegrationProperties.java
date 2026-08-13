package com.northstar.crm.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "northstar.integration")
public record NorthstarIntegrationProperties(
        @NotBlank String apiKey,
        @Positive long timeoutMs) {
}