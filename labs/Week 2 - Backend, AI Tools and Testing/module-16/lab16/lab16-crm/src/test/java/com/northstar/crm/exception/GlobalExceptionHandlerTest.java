package com.northstar.crm.exception;

import com.northstar.crm.dto.CustomerRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mapsNotFound() {
        var err = handler.fromBusiness(BusinessException.notFound("CUS-9999", "lab-request-001"));
        assertEquals(404, err.getStatus());
        assertEquals("lab-request-001", err.getCorrelationId());
    }

    @Test
    void mapsValidationEmail() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        CustomerRequestDTO dto = new CustomerRequestDTO("CUS-1003", "Bad Email", "not-an-email", "PROSPECT");
        Set<ConstraintViolation<CustomerRequestDTO>> violations = validator.validate(dto);

        var err = handler.fromValidation(violations, "lab-request-001");
        assertEquals(400, err.getStatus());
        assertTrue(err.getErrors().containsKey("email"));
    }

    @Test
    void mapsConflict() {
        var err = handler.fromBusiness(
                BusinessException.conflict("illegal status transition ACTIVE -> PROSPECT", "lab-request-001"));
        assertEquals(409, err.getStatus());
    }
}