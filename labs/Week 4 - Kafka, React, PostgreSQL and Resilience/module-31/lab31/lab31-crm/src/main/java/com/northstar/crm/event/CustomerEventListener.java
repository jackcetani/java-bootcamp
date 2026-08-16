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

    public CustomerEventListener(ProcessedEventStore store, NotificationHandler handler) {
        this.store = store;
        this.handler = handler;
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
            return;
        }

        handler.handle(event);
    }
}