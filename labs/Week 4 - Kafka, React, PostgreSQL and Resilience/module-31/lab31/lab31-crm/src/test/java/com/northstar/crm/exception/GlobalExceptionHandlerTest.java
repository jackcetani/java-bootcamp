package com.northstar.crm.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void notFound_mapsTo404WithCorrelation() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getHeader("X-Correlation-Id")).thenReturn("lab-request-001");
        Mockito.when(req.getRequestURI()).thenReturn("/api/customers/CUS-9999");

        var response = handler.notFound(new CustomerNotFoundException("CUS-9999"), req);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("lab-request-001", response.getBody().correlationId());
    }

    @Test
    void conflict_defaultsCorrelationWhenHeaderMissing() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getHeader("X-Correlation-Id")).thenReturn(null);
        Mockito.when(req.getRequestURI()).thenReturn("/api/customers");

        var response = handler.conflict(new DuplicateCustomerException("CUS-1001"), req);

        assertEquals(409, response.getStatusCode().value());
        assertEquals("lab-request-001", response.getBody().correlationId());
    }

    @Test
    void fallback_returnsSafe500WithoutLeakingDetail() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getHeader("X-Correlation-Id")).thenReturn("lab-request-001");
        Mockito.when(req.getRequestURI()).thenReturn("/api/customers");

        var response = handler.fallback(new RuntimeException("db password=hunter2"), req);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Unexpected error", response.getBody().message());
        assertFalse(response.getBody().message().contains("hunter2"));
    }
}