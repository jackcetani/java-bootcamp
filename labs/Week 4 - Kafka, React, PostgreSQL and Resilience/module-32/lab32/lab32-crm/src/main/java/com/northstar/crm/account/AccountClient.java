package com.northstar.crm.account;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class AccountClient {

  private final RestClient restClient;

  public AccountClient(@Value("${account.api.base-url}") String baseUrl) {
    this.restClient = RestClient.builder().baseUrl(baseUrl).build();
  }

  public AccountSummary fetch(String customerId) {
    try {
      RemoteAccounts remote = restClient.get()
              .uri("/accounts/{customerId}", customerId)
              .header("X-Correlation-Id", "lab-request-001")
              .retrieve()
              .body(RemoteAccounts.class);
      List<AccountSummary.AccountEntry> accounts = remote == null ? List.of() : remote.accounts();
      return new AccountSummary(customerId, true, accounts);
    } catch (RestClientResponseException ex) {
      if (ex.getStatusCode().is5xxServerError()) {
        throw new TemporaryAccountException("account service returned " + ex.getStatusCode());
      }
      throw ex;
    } catch (ResourceAccessException ex) {
      throw new TemporaryAccountException("account service unreachable: " + ex.getMessage());
    }
  }

  private record RemoteAccounts(List<AccountSummary.AccountEntry> accounts) {}
}