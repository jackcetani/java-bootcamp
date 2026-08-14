package com.northstar.crm.service;

import com.northstar.crm.account.AccountRepository;
import com.northstar.crm.account.TransactionLog;
import com.northstar.crm.exception.AccountNotFoundException;
import com.northstar.crm.exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // rolls back each test method's DB writes automatically -- keeps seeds clean between tests
class TransferServiceTest {
  @Autowired TransferService transferService;
  @Autowired AccountRepository accounts;

  @Test
  void happyTransferMovesBothBalancesAndLogsCorrelation() {
    TransactionLog log = transferService.transfer(
            "ACC-1001-MAIN", "ACC-1001-LOYALTY", new BigDecimal("50.00"), "lab-request-001");

    assertEquals(0, new BigDecimal("950.00").compareTo(
            accounts.findById("ACC-1001-MAIN").orElseThrow().getBalance()));
    assertEquals(0, new BigDecimal("150.00").compareTo(
            accounts.findById("ACC-1001-LOYALTY").orElseThrow().getBalance()));
    assertEquals("lab-request-001", log.getCorrelationId());
  }

  @Test
  void forceFailRollsBackAndLeavesMainUnchanged() {
    BigDecimal before = accounts.findById("ACC-1001-MAIN").orElseThrow().getBalance();

    assertThrows(IllegalStateException.class, () ->
            transferService.transfer("ACC-1001-MAIN", "ACC-FORCE-FAIL",
                    new BigDecimal("10.00"), "lab-request-001"));

    assertEquals(0, before.compareTo(
            accounts.findById("ACC-1001-MAIN").orElseThrow().getBalance()));
  }

  @Test
  void insufficientFundsLeavesDestinationUnchanged() {
    BigDecimal loyaltyBefore = accounts.findById("ACC-1001-LOYALTY").orElseThrow().getBalance();

    assertThrows(InsufficientFundsException.class, () ->
            transferService.transfer("ACC-1001-MAIN", "ACC-1001-LOYALTY",
                    new BigDecimal("999999.00"), "lab-request-001"));

    assertEquals(0, loyaltyBefore.compareTo(
            accounts.findById("ACC-1001-LOYALTY").orElseThrow().getBalance()));
  }

  @Test
  void missingDestinationThrowsAccountNotFound() {
    assertThrows(AccountNotFoundException.class, () ->
            transferService.transfer("ACC-1001-MAIN", "ACC-DOES-NOT-EXIST",
                    new BigDecimal("10.00"), "lab-request-001"));
  }
}