package com.northstar.crm.account;

import java.math.BigDecimal;
import java.util.List;

public record AccountSummary(String customerId, boolean available, List<AccountEntry> accounts) {

  public static AccountSummary unavailable(String customerId) {
    return new AccountSummary(customerId, false, List.of());
  }

  public record AccountEntry(String accountId, String type, BigDecimal balance) {}
}