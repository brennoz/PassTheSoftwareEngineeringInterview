package com.interview.pairingdrill.q079_account_statement.answer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class AccountStatementService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountStatementService(AccountRepository accountRepository,
                                   TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Statement generateStatement(String accountId, LocalDate from, LocalDate to) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));


        List<Transaction> allTxns = transactionRepository.findByAccountId(account.getAccountId());                

        return getStatementByAccount(account, from, to, allTxns);
    }

    public List<Statement> generateBatch(List<String> accountIds, LocalDate from, LocalDate to) {

        List<Statement> statements = new ArrayList<>();

        List<Account> accounts = accountRepository.findAllById(accountIds);

        List<Transaction> txs = transactionRepository.findByAccountIds(accounts.stream().map(acc -> acc.getAccountId()).toList());

        for (Account account : accounts) {

            List<Transaction> txByAccount = txs.stream()
                                                .filter(t -> t.getAccountId().equals(account.getAccountId()))
                                                .toList();

            statements.add(getStatementByAccount(account, from, to, txByAccount));
        }

        return statements;
    }

    private Statement getStatementByAccount(Account account, LocalDate from, LocalDate to, List<Transaction> txs) {

        List<Transaction> inRange = txs.stream()
                .filter(t -> !t.getDate().isBefore(from) && !t.getDate().isAfter(to))
                .toList();

        Map<TransactionType, BigDecimal> totals = inRange.stream()
                .collect(Collectors.groupingBy(Transaction::getType, Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));

        return new Statement(account, inRange, totals.getOrDefault(TransactionType.CREDIT, BigDecimal.ZERO), totals.getOrDefault(TransactionType.DEBIT, BigDecimal.ZERO));
    }
}
