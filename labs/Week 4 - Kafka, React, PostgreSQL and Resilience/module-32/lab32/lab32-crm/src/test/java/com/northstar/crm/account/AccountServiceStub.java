package com.northstar.crm.account;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.stubbing.Scenario;

/** Test-only helper wrapping WireMock scenarios for the account-profile dependency. */
public class AccountServiceStub {

    private final WireMockServer server;

    public AccountServiceStub(int port) {
        this.server = new WireMockServer(WireMockConfiguration.options().port(port));
    }

    public void start() {
        server.start();
        configureFor("localhost", server.port());
    }

    public void stop() {
        server.stop();
    }

    public int requestCount(String urlPathPattern) {
        return server.findAll(getRequestedFor(urlMatching(urlPathPattern))).size();
    }

    /** First call 503, second call onward 200 with empty accounts — Step 2 recovery scenario. */
    public void stubRecovery(String customerId) {
        server.stubFor(get("/accounts/" + customerId)
                .inScenario("recovery").whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(503))
                .willSetStateTo("available"));
        server.stubFor(get("/accounts/" + customerId)
                .inScenario("recovery").whenScenarioStateIs("available")
                .willReturn(okJson("{\"accounts\":[]}")));
    }

    /** Always 200 with empty accounts — healthy path. */
    public void stubHealthy(String customerId) {
        server.stubFor(get("/accounts/" + customerId).willReturn(okJson("{\"accounts\":[]}")));
    }

    /** Always 503 — for driving the circuit breaker to OPEN. */
    public void stubPermanentFailure(String customerId) {
        server.stubFor(get("/accounts/" + customerId).willReturn(aResponse().withStatus(503)));
    }

    /** Delayed 200 response — for TimeLimiter tests. */
    public void stubSlow(String customerId, int delayMillis) {
        server.stubFor(get("/accounts/" + customerId)
                .willReturn(okJson("{\"accounts\":[]}").withFixedDelay(delayMillis)));
    }
}