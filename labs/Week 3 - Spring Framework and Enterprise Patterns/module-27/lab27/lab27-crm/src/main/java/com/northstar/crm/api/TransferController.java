package com.northstar.crm.api;

import com.northstar.crm.account.TransactionLog;
import com.northstar.crm.exception.AccountNotFoundException;
import com.northstar.crm.exception.InsufficientFundsException;
import com.northstar.crm.service.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {
  private final TransferService transferService;

  public TransferController(TransferService transferService) {
    this.transferService = transferService;
  }

  @PostMapping
  public TransactionLog transfer(
          @RequestBody TransferRequest req,
          @RequestHeader(value = "X-Correlation-Id", defaultValue = "lab-request-001")
          String correlationId) {
    return transferService.transfer(
            req.fromAccountId(), req.toAccountId(), req.amount(), correlationId);
  }

  @ExceptionHandler(AccountNotFoundException.class)
  public ResponseEntity<String> handleNotFound(AccountNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(InsufficientFundsException.class)
  public ResponseEntity<String> handleInsufficientFunds(InsufficientFundsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<String> handleForcedFailure(IllegalStateException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }
}