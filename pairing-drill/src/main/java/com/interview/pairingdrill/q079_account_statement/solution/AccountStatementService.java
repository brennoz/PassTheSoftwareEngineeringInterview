package com.interview.pairingdrill.q079_account_statement.solution;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

// Fix 1: private static final Logger — log only accountId, never account number / customer data.
//   The seeded System.out.println is doubly wrong: (a) stdout is not structured/rotatable/searchable,
//   and (b) if accountNumber were ever added to the log line it would be a GDPR / FCA data-
//   minimisation violation. A proper logger lets you control log level per environment and never
//   accidentally leak sensitive fields into a log aggregator (Splunk, Datadog).
//   Note: production code uses SLF4J (spring-boot-starter-logging); JUL used here to stay within
//   the pairing-drill module's minimal pom — the principle is identical.
// Fix 2: exception propagates — swallowing it returned null silently; the caller had no way to
//   distinguish "account not found" from "any runtime error" from "no transactions in range".
//   Let the exception surface so the controller can return the correct HTTP status.
// Fix 3: BigDecimal sums — never mapToDouble().sum() for money; see Transaction.java.
// Fix 4: TransactionType enum comparison — see TransactionType.java; == is correct for enums
//   (instances are singletons), more explicit than .equals().
// Fix 5: findByAccountIds bulk fetch — one repo call replaces N+1 in generateBatch.
// Fix 6: extracted helpers filterByDateRange() + buildStatement() — eliminates the copy-paste
//   between generateStatement and generateBatch. WHY extract: any future change applied to one
//   inline copy would silently miss the other. Duplication is invisible as a defect until it
//   diverges; extraction makes the shared logic a single point of change.
@Service
public class AccountStatementService {

    private static final Logger log =
            Logger.getLogger(AccountStatementService.class.getName());

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountStatementService(AccountRepository accountRepository,
                                   TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Statement generateStatement(String accountId, LocalDate from, LocalDate to) {
        // Fix 1: log only accountId — never accountNumber, customerId, or balance
        log.info(() -> "Generating statement accountId=" + accountId
                + " from=" + from + " to=" + to);

        // Fix 2: no try/catch — IllegalArgumentException propagates to the caller
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));

        // Fix 6: delegate to extracted helper — no inline duplication
        List<Transaction> inRange = filterByDateRange(
                transactionRepository.findByAccountId(accountId), from, to);

        return buildStatement(account, inRange);
    }

    public List<Statement> generateBatch(List<String> accountIds, LocalDate from, LocalDate to) {
        // Fix 5: one bulk fetch — findByAccountIds was already in the interface, just never called.
        // Seeded code called findByAccountId(accountId) inside a loop: O(N) repo calls.
        // At overnight settlement scale (thousands of accounts) that's thousands of DB round-trips.
        // groupingBy converts the flat list into a per-account lookup map in one pass.
        Map<String, List<Transaction>> txnsByAccount = transactionRepository
                .findByAccountIds(accountIds)
                .stream()
                .collect(Collectors.groupingBy(Transaction::getAccountId));

        return accountRepository.findAllById(accountIds).stream()
                .map(account -> buildStatement(
                        account,
                        filterByDateRange(
                                txnsByAccount.getOrDefault(account.getAccountId(), List.of()),
                                from, to)))
                .collect(Collectors.toList());
    }

    // Fix 6: extracted — eliminates duplication between generateStatement and generateBatch.
    // Alternative rejected: inline in both methods — any future predicate change requires two edits;
    // the second edit will eventually be forgotten, causing the two paths to diverge silently.
    private List<Transaction> filterByDateRange(List<Transaction> txns, LocalDate from, LocalDate to) {
        return txns.stream()
                .filter(t -> !t.getDate().isBefore(from) && !t.getDate().isAfter(to))
                .collect(Collectors.toList());
    }

    private Statement buildStatement(Account account, List<Transaction> inRange) {
        // Fix 3: BigDecimal.add() via reduce — never mapToDouble().sum() for monetary sums.
        // Fix 4: == on TransactionType — enum instances are singletons, == is both correct
        //   and more explicit; .equals() also works but allows accidental String comparisons.
        BigDecimal totalCredits = inRange.stream()
                .filter(t -> TransactionType.CREDIT == t.getType())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDebits = inRange.stream()
                .filter(t -> TransactionType.DEBIT == t.getType())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Bonus: Statement holds safe fields only, not the raw Account entity.
        // What NOT to do: pass the full Account object — downstream consumers (PDF job, REST
        // response) would gain access to customerId and full accountNumber without any deliberate
        // data-minimisation decision. Mask at this boundary so callers never have to think about it.
        return new Statement(
                account.getAccountId(),
                mask(account.getAccountNumber()),
                inRange,
                totalCredits,
                totalDebits);
    }

    private String mask(String accountNumber) {
        // Show only the last 4 chars: "20-00-00 12345678" → "****5678"
        if (accountNumber == null || accountNumber.length() < 4) return "****";
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }
}
