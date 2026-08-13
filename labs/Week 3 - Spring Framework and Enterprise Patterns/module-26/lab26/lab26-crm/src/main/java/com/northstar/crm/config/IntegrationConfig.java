package com.northstar.crm.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(NorthstarIntegrationProperties.class)
public class IntegrationConfig {
}