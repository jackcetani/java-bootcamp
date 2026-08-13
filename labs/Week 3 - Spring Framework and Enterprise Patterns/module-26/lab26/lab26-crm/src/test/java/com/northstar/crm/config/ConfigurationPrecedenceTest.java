package com.northstar.crm.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "northstar.integration.timeout-ms=1234")
class ConfigurationPrecedenceTest {

    @Autowired
    NorthstarIntegrationProperties properties;

    @Test
    void explicitPropertyOverridesProfileYaml() {
        // application-test.yml sets timeout-ms: 500. @TestPropertySource simulates a
        // higher-precedence override (same tier as CLI/-D) and must win.
        assertEquals(1234L, properties.timeoutMs());
    }
}