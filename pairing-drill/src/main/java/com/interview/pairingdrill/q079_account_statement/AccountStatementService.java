package com.interview.pairingdrill.q079_account_statement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
        System.out.println("Generating statement for accountId=" + accountId
                + ", from=" + from + ", to=" + to);
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));

            List<Transaction> allTxns = transactionRepository.findByAccountId(accountId);
            List<Transaction> inRange = allTxns.stream()
                    .filter(t -> !t.getDate().isBefore(from) && !t.getDate().isAfter(to))
                    .collect(Collectors.toList());

            double totalCredits = inRange.stream()
                    .filter(t -> "CREDIT".equals(t.getType()))
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            double totalDebits = inRange.stream()
                    .filter(t -> "DEBIT".equals(t.getType()))
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            return new Statement(account, inRange, totalCredits, totalDebits);

        } catch (Exception e) {
            return null;
        }
    }

    public List<Statement> generateBatch(List<String> accountIds, LocalDate from, LocalDate to) {
        List<Statement> statements = new ArrayList<>();

        for (String accountId : accountIds) {
            Account account = accountRepository.findById(accountId).orElse(null);
            if (account == null) {
                continue;
            }

            List<Transaction> allTxns = transactionRepository.findByAccountId(accountId);
            List<Transaction> inRange = allTxns.stream()
                    .filter(t -> !t.getDate().isBefore(from) && !t.getDate().isAfter(to))
                    .collect(Collectors.toList());

            double totalCredits = inRange.stream()
                    .filter(t -> "CREDIT".equals(t.getType()))
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            double totalDebits = inRange.stream()
                    .filter(t -> "DEBIT".equals(t.getType()))
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            statements.add(new Statement(account, inRange, totalCredits, totalDebits));
        }

        return statements;
    }
}
