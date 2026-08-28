package com.northstar.crm.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"crm.customer-events.v1"})
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "crm.kafka.customer-events-topic=crm.customer-events.v1"
})
class CustomerEventIntegrationTest {

    @Autowired
    private CustomerEventPublisher publisher;

    @Autowired
    private NotificationHandler handler;

    @Test
    void publishesAndConsumesCustomerCreated() {
        var createdEvent = new CustomerEvent(
                UUID.randomUUID(), "CustomerCreated", 1, Instant.now(),
                "CUS-1001", "lab-request-001", "customer-service",
                new CustomerData("Amina Khan", null, "ACTIVE"));

        publisher.publish(createdEvent);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(handler.events())
                        .extracting(CustomerEvent::eventId)
                        .contains(createdEvent.eventId()));
    }

    @Test
    void duplicateEventIdIsIgnored() {
        var event = new CustomerEvent(
                UUID.randomUUID(), "CustomerCreated", 1, Instant.now(),
                "CUS-1002", "lab-request-001", "customer-service",
                new CustomerData("Ravi Singh", null, "PROSPECT"));

        publisher.publish(event);
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(handler.events())
                        .extracting(CustomerEvent::eventId)
                        .contains(event.eventId()));

        long firstCount = handler.events().stream()
                .filter(e -> e.eventId().equals(event.eventId()))
                .count();

        publisher.publish(event);

        await().pollDelay(Duration.ofSeconds(2)).atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            long afterCount = handler.events().stream()
                    .filter(e -> e.eventId().equals(event.eventId()))
                    .count();
            assertThat(afterCount).isEqualTo(firstCount);
        });
    }
}