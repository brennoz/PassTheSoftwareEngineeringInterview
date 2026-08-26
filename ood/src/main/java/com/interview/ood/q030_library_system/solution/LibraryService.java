package com.interview.ood.q030_library_system.solution;

import java.math.BigDecimal;
import java.time.LocalDate;

// Pattern: thin orchestrator — this class coordinates BookCopy (status via CopyState),
// Member (borrow-limit + loan list) and FineCalculationStrategy (fine formula). It holds NO
// business rules of its own beyond sequencing calls to the objects that own each rule. This
// is what let CopyState's guards, Member's borrow limit, and the fine Strategy all evolve
// independently without ever touching this class's method bodies.
public class LibraryService {

    private final FineCalculationStrategy fineStrategy;

    public LibraryService(FineCalculationStrategy fineStrategy) {
        this.fineStrategy = fineStrategy;
    }

    // ── TDD note — Test 1 (written first) ───────────────────────────────────────────
    // checkout_changes_copy_status_to_borrowed: the simplest possible case, one member, one
    // copy, no limit in play. This test alone forces checkout() to (a) call copy.checkout()
    // so status flips via the State pattern, and (b) return a Loan the caller can act on
    // later — not void, because Test 4/5 need a Loan handle to pass into returnBook().
    //
    // ── TDD note — Test 2 (written second) ──────────────────────────────────────────
    // checkout_throws_when_borrow_limit_reached: needs Test 1's checkout() already working
    // (three successful checkouts set up the precondition) before testing the fourth fails.
    // The order of operations here matters and is itself a design decision worth saying out
    // loud: the limit is checked BEFORE copy.checkout() runs, so a member at their limit
    // never leaves a copy's state half-transitioned — either the whole checkout succeeds, or
    // nothing changes.
    public Loan checkout(Member member, BookCopy copy, int loanDays) {
        if (member.hasReachedBorrowLimit()) {
            throw new BorrowLimitExceededException(member.getId());
        }
        copy.checkout();
        Loan loan = new Loan(member, copy, loanDays);
        member.addActiveLoan(loan);
        return loan;
    }

    // ── TDD note — Test 4 (written fourth, after Test 3 proved the State guards fire) ──
    // return_on_time_produces_no_fine: the round trip — only meaningful once checkout works,
    // since you need a borrowed copy to return. Returns null (not a zero-amount Fine) when
    // nothing is owed — a cleaner signal than a Fine object every caller has to remember to
    // amount-check.
    //
    // ── TDD note — Test 5 (written fifth, new — closes the original suite's gap) ───────
    // return_after_due_date_produces_fine: the original 4-test suite never exercised the
    // "a fine actually gets produced" path — only "no fine when on time" was proven. A
    // negative loanDays at checkout (e.g. -5) backdates dueDate into the past, making the
    // loan overdue immediately without needing to fake the clock — the simplest way to set
    // up this precondition without adding test-only constructors to Loan.
    public Fine returnBook(Member member, Loan loan) {
        loan.setReturnedOn(LocalDate.now());
        loan.getCopy().returnCopy();
        member.removeLoan(loan);
        BigDecimal amount = fineStrategy.calculate(loan);
        // Never == or != on BigDecimal — compareTo is the only correct comparison. The
        // historical bug on this exact question used `fine == BigDecimal.ZERO` (reference
        // identity, not value) AND had the ternary branches inverted — see
        // answer/attempt1/ for exactly what that looked like.
        return amount.compareTo(BigDecimal.ZERO) > 0 ? new Fine(loan, amount) : null;
    }
}
