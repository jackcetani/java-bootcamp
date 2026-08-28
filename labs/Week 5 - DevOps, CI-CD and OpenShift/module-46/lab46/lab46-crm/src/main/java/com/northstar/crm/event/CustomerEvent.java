package com.northstar.crm.event;

import com.northstar.crm.exception.UnsupportedEventVersionException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Immutable CRM customer domain event. */
public record CustomerEvent(
        UUID eventId,
        String eventType,
        int eventVersion,
        Instant occurredAt,
        String customerId,
        String correlationId,
        String source,
        CustomerData data) {

    public CustomerEvent {
        Objects.requireNonNull(eventId, "eventId is required");
        Objects.requireNonNull(customerId, "customerId is required");
        if (eventVersion != 1) {
            throw new UnsupportedEventVersionException(eventVersion);
        }
    }
}