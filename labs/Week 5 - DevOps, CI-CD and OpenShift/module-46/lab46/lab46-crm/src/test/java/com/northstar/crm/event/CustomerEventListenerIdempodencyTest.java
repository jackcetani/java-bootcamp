package com.northstar.crm.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CustomerEventListenerIdempotencyTest {

    @Autowired ProcessedEventStore processedEventStore;
    @Autowired NotificationHandler notificationHandler;
    @Autowired CustomerEventListener listener;

    @Test
    void duplicateEventForCus1002IsNoOp() {
        UUID eventId = UUID.randomUUID();
        CustomerEvent event = new CustomerEvent(
                eventId, "CustomerStatusChanged", 1, Instant.now(),
                "CUS-1002", "lab-request-001", "customer-service",
                new CustomerData(null, "PROSPECT", "ACTIVE"));

        listener.onCustomerEvent(event, "CUS-1002");
        int afterFirst = notificationHandler.events().size();

        listener.onCustomerEvent(event, "CUS-1002"); // same eventId — should be a no-op
        int afterSecond = notificationHandler.events().size();

        assertThat(afterSecond).isEqualTo(afterFirst);
        assertThat(processedEventStore.markIfNew(eventId)).isFalse(); // already seen
    }
}