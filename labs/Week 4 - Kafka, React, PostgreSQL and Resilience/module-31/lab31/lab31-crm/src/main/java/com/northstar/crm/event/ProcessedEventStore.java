package com.northstar.crm.event;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ProcessedEventStore {

    private final Set<UUID> seen = ConcurrentHashMap.newKeySet();

    /** @return true if this is the first time seeing eventId */
    public boolean markIfNew(UUID eventId) {
        return seen.add(eventId);
    }
}