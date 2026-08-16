package com.northstar.crm.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificationHandler {

    private static final Logger log = LoggerFactory.getLogger(NotificationHandler.class);
    private final List<CustomerEvent> handled = new CopyOnWriteArrayList<>();

    public void handle(CustomerEvent event) {
        log.info("notification_sent eventId={} customerId={} correlationId={}",
                event.eventId(), event.customerId(), event.correlationId());
        handled.add(event);
    }

    public List<CustomerEvent> events() {
        return List.copyOf(handled);
    }
}