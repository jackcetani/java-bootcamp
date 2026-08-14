package com.northstar.crm.account;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class AccountSeed {
  @Bean
  CommandLineRunner seedAccounts(AccountRepository accounts) {
    return args -> {
      accounts.save(new Account("ACC-1001-MAIN", "CUS-1001", "MAIN", new BigDecimal("1000.00"), "ACTIVE"));
      accounts.save(new Account("ACC-1001-LOYALTY", "CUS-1001", "LOYALTY", new BigDecimal("100.00"), "ACTIVE"));
      accounts.save(new Account("ACC-1002-MAIN", "CUS-1002", "MAIN", new BigDecimal("250.00"), "ACTIVE"));
      // ACC-FORCE-FAIL is intentionally NOT persisted -- used to trigger rollback demos
    };
  }
}