package com.northstar.crm.exception;

import com.northstar.crm.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    var violations = ex.getBindingResult().getFieldErrors().stream()
            .sorted((a, b) -> a.getField().compareTo(b.getField()))
            .map(fe -> new ErrorResponse.FieldViolation(fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()))
            .toList();
    var body = new ErrorResponse(
            Instant.now(), 400, "Bad Request", "Validation failed",
            req.getRequestURI(), correlationId(req), violations);
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(CustomerNotFoundException.class)
  public ResponseEntity<ErrorResponse> notFound(CustomerNotFoundException ex, HttpServletRequest req) {
    var body = new ErrorResponse(
            Instant.now(), 404, "Not Found", ex.getMessage(),
            req.getRequestURI(), correlationId(req), List.of());
    return ResponseEntity.status(404).body(body);
  }

  @ExceptionHandler(DuplicateCustomerException.class)
  public ResponseEntity<ErrorResponse> conflict(DuplicateCustomerException ex, HttpServletRequest req) {
    var body = new ErrorResponse(
            Instant.now(), 409, "Conflict", ex.getMessage(),
            req.getRequestURI(), correlationId(req), List.of());
    return ResponseEntity.status(409).body(body);
  }

  @ExceptionHandler(InvalidStatusTransitionException.class)
  public ResponseEntity<ErrorResponse> illegalTransition(InvalidStatusTransitionException ex, HttpServletRequest req) {
    var body = new ErrorResponse(
            Instant.now(), 400, "Bad Request", ex.getMessage(),
            req.getRequestURI(), correlationId(req), List.of());
    return ResponseEntity.status(400).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> fallback(Exception ex, HttpServletRequest req) {
    log.error("Unhandled correlationId={}", correlationId(req), ex);
    var body = new ErrorResponse(
            Instant.now(), 500, "Internal Server Error", "Unexpected error",
            req.getRequestURI(), correlationId(req), List.of());
    return ResponseEntity.status(500).body(body);
  }

  private String correlationId(HttpServletRequest req) {
    String header = req.getHeader("X-Correlation-Id");
    return (header == null || header.isBlank()) ? "lab-request-001" : header;
  }
}