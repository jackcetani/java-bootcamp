package com.northstar.crm.event;

import com.northstar.crm.exception.InvalidCustomerEventException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventListener {

    private static final Logger log = LoggerFactory.getLogger(CustomerEventListener.class);
    private final ProcessedEventStore store;
    private final NotificationHandler handler;
    private final io.micrometer.core.instrument.Counter processedCounter;
    private final io.micrometer.core.instrument.Counter duplicateCounter;

    public CustomerEventListener(ProcessedEventStore store, NotificationHandler handler,
                                 io.micrometer.core.instrument.MeterRegistry meterRegistry) {
        this.store = store;
        this.handler = handler;
        this.processedCounter = meterRegistry.counter("crm.kafka.events.processed", "topic", "crm.customer-events.v1", "outcome", "success");
        this.duplicateCounter = meterRegistry.counter("crm.kafka.events.processed", "topic", "crm.customer-events.v1", "outcome", "duplicate");
    }

    @KafkaListener(topics = "${crm.kafka.customer-events-topic}")
    public void onCustomerEvent(
            @Payload CustomerEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        if (key == null || !key.equals(event.customerId())) {
            throw new InvalidCustomerEventException(
                    "key mismatch: key=" + key + " customerId=" + event.customerId());
        }

        log.info("customer_event_received id={} customerId={} correlationId={}",
                event.eventId(), event.customerId(), event.correlationId());

        if (!store.markIfNew(event.eventId())) {
            log.info("duplicate_event_ignored id={}", event.eventId());
            duplicateCounter.increment();
            return;
        }

        handler.handle(event);
        processedCounter.increment();
    }
}