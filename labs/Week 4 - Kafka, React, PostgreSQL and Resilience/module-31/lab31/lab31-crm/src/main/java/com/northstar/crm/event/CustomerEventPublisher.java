package com.northstar.crm.event;

import com.northstar.crm.exception.InvalidCustomerEventException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(CustomerEventPublisher.class);

    private final KafkaTemplate<String, CustomerEvent> kafkaTemplate;
    private final String topic;

    public CustomerEventPublisher(
            KafkaTemplate<String, CustomerEvent> kafkaTemplate,
            @Value("${crm.kafka.customer-events-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(CustomerEvent event) {
        Objects.requireNonNull(event, "event is required");
        if (event.customerId() == null || event.customerId().isBlank()) {
            throw new InvalidCustomerEventException("customerId is required to publish an event");
        }

        kafkaTemplate.send(topic, event.customerId(), event)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        log.error("customer_event_publish_failed id={} customerId={}",
                                event.eventId(), event.customerId(), error);
                    } else {
                        log.info("customer_event_published id={} customerId={} partition={} offset={}",
                                event.eventId(), event.customerId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}