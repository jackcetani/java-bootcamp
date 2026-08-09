package com.northstar.crm.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class CustomerMetrics {
    private final MeterRegistry registry;
    private final Timer getTimer;

    public CustomerMetrics(MeterRegistry registry) {
        this.registry = registry;
        this.getTimer = Timer.builder("crm.customer.get.latency").register(registry);
    }

    public void recordCreate(String result) {
        Counter.builder("crm.customer.create").tag("result", result).register(registry).increment();
    }

    public void recordGet(String result) {
        Counter.builder("crm.customer.get").tag("result", result).register(registry).increment();
    }

    public void recordGetLatency(long durationNanos) {
        getTimer.record(durationNanos, TimeUnit.NANOSECONDS);
    }
}