package com.northstar.crm.service;

import com.northstar.crm.account.Account;
import com.northstar.crm.account.AccountRepository;
import com.northstar.crm.account.TransactionLog;
import com.northstar.crm.account.TransactionLogRepository;
import com.northstar.crm.exception.AccountNotFoundException;
import com.northstar.crm.exception.InsufficientFundsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {
  private final AccountRepository accountRepository;
  private final TransactionLogRepository transactionLogRepository;

  public TransferService(AccountRepository accountRepository,
                         TransactionLogRepository transactionLogRepository) {
    this.accountRepository = accountRepository;
    this.transactionLogRepository = transactionLogRepository;
  }

  @Transactional
  public TransactionLog transfer(String fromAccountId, String toAccountId,
                                 BigDecimal amount, String correlationId) {
    Account from = accountRepository.findById(fromAccountId)
            .orElseThrow(() -> new AccountNotFoundException(fromAccountId));

    if (from.getBalance().compareTo(amount) < 0) {
      throw new InsufficientFundsException(fromAccountId);
    }

    // Reconciled vs the guide's literal ordering: ACC-FORCE-FAIL is deliberately never
    // persisted (see AccountSeed), so this check must run BEFORE looking up `to` --
    // otherwise it would fail as a generic AccountNotFoundException instead of the
    // intentional, documented "Forced failure for rollback demo".
    if ("ACC-FORCE-FAIL".equals(toAccountId)) {
      throw new IllegalStateException("Forced failure for rollback demo");
    }

    Account to = accountRepository.findById(toAccountId)
            .orElseThrow(() -> new AccountNotFoundException(toAccountId));

    from.setBalance(from.getBalance().subtract(amount));
    to.setBalance(to.getBalance().add(amount));
    accountRepository.save(from);
    accountRepository.save(to);

    TransactionLog log = new TransactionLog();
    log.setCorrelationId(correlationId);
    log.setFromAccountId(fromAccountId);
    log.setToAccountId(toAccountId);
    log.setAmount(amount);
    return transactionLogRepository.save(log);
  }
}