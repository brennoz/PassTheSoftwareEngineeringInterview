package com.interview.pairingdrill.q078_payment_search.solution;

// Why a named interface rather than raw Predicate<Payment>:
// Predicate<Payment> already has .and()/.or()/.negate() — we could use it directly.
// A named PaymentSpec interface adds one thing Predicate lacks: domain language.
// `PaymentSpec` reads in context; `Predicate<Payment>` is generic machinery.
// The interface is @FunctionalInterface so it still composes with lambdas and
// method references exactly as Predicate does.
//
// Why and()/or() as default methods:
// This is the Specification pattern's composition mechanism. The caller combines
// leaf specs with .and()/.or() without knowing their internals — each spec is a
// closed, independently-testable unit. Adding a CurrencySpec in the future means
// writing one new class; nothing else changes (Open/Closed Principle).
// What NOT to do: inline all criteria as if/else branches in the service — that is
// the shape of the seeded god-method; it violates OCP and guarantees duplication
// the moment a second query method needs the same filters.
@FunctionalInterface
public interface PaymentSpec {

    boolean isSatisfiedBy(Payment p);

    default PaymentSpec and(PaymentSpec other) {
        return p -> this.isSatisfiedBy(p) && other.isSatisfiedBy(p);
    }

    default PaymentSpec or(PaymentSpec other) {
        return p -> this.isSatisfiedBy(p) || other.isSatisfiedBy(p);
    }

    default PaymentSpec negate() {
        return p -> !this.isSatisfiedBy(p);
    }

    // Matches every payment — identity element for .and() composition.
    static PaymentSpec any() {
        return p -> true;
    }
}
