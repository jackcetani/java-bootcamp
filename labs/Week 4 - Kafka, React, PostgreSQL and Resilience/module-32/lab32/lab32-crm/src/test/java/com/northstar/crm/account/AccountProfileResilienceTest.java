package com.northstar.crm.account;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "account.api.base-url=http://localhost:8089")
class AccountProfileResilienceTest {

  private static final String CUSTOMER_ID = "CUS-1001";

  @Autowired
  private AccountProfileService service;

  @Autowired
  private CircuitBreakerRegistry circuitBreakerRegistry;

  private AccountServiceStub stub;

  @BeforeEach
  void startStub() {
    // @SpringBootTest reuses one Spring context (and one singleton "accountProfile"
    // circuit breaker) across every test method in this class. Without an explicit
    // reset, whichever test trips the breaker OPEN leaves it OPEN for every test
    // that runs after it, regardless of that test's own WireMock stub being healthy.
    circuitBreakerRegistry.circuitBreaker("accountProfile").reset();
    stub = new AccountServiceStub(8089);
    stub.start();
  }

  @AfterEach
  void stopStub() {
    stub.stop();
  }

  @Test
  void healthyCall_returnsAvailable() throws ExecutionException, InterruptedException {
    stub.stubHealthy(CUSTOMER_ID);

    AccountSummary summary = service.find(CUSTOMER_ID).get();

    assertThat(summary.available()).isTrue();
    assertThat(summary.customerId()).isEqualTo(CUSTOMER_ID);
  }

  @Test
  void retryRecoversFromTransient503_thenSucceeds() throws ExecutionException, InterruptedException {
    stub.stubRecovery(CUSTOMER_ID);

    AccountSummary summary = service.find(CUSTOMER_ID).get();

    assertThat(summary.available()).isTrue();
    assertThat(stub.requestCount("/accounts/" + CUSTOMER_ID)).isGreaterThanOrEqualTo(2);
  }

  @Test
  void openCircuit_failsFastWithoutHittingStub() throws ExecutionException, InterruptedException {
    stub.stubPermanentFailure(CUSTOMER_ID);

    // Drive enough failing calls to cross minimum-number-of-calls (5) and failure-rate-threshold (50%).
    // If your run doesn't trip OPEN on the first try, raise this loop count — exact call count needed
    // depends on Retry/CircuitBreaker aspect ordering (see Concepts to Discuss #9).
    for (int i = 0; i < 8; i++) {
      service.find(CUSTOMER_ID).get();
    }

    int countBeforeProbe = stub.requestCount("/accounts/" + CUSTOMER_ID);

    long start = System.nanoTime();
    AccountSummary summary = service.find(CUSTOMER_ID).get();
    long elapsedMs = (System.nanoTime() - start) / 1_000_000;

    assertThat(summary.available()).isFalse();
    assertThat(elapsedMs).isLessThan(500);
    assertThat(stub.requestCount("/accounts/" + CUSTOMER_ID)).isEqualTo(countBeforeProbe);
  }

  @Test
  void timeout_returnsUnavailableFallback() throws ExecutionException, InterruptedException {
    stub.stubSlow(CUSTOMER_ID, 3000);

    long start = System.nanoTime();
    AccountSummary summary = service.find(CUSTOMER_ID).get();
    long elapsedMs = (System.nanoTime() - start) / 1_000_000;

    assertThat(summary.available()).isFalse();
    assertThat(summary.accounts()).isEmpty();
    assertThat(elapsedMs).isLessThan(3000);
  }
}