package com.northstar.crm.account;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccountProfileService {

  private static final Logger log = LoggerFactory.getLogger(AccountProfileService.class);

  private final AccountClient client;
  private final ExecutorService executor = Executors.newCachedThreadPool();

  public AccountProfileService(AccountClient client) {
    this.client = client;
  }

  @Retry(name = "accountProfile", fallbackMethod = "fallback")
  @CircuitBreaker(name = "accountProfile")
  @TimeLimiter(name = "accountProfile")
  public CompletableFuture<AccountSummary> find(String customerId) {
    return CompletableFuture.supplyAsync(() -> client.fetch(customerId), executor);
  }

  @SuppressWarnings("unused")
  private CompletableFuture<AccountSummary> fallback(String customerId, Throwable ex) {
    log.warn("account_profile_degraded customerId={} cause={}", customerId, ex.getClass().getSimpleName());
    return CompletableFuture.completedFuture(AccountSummary.unavailable(customerId));
  }
}