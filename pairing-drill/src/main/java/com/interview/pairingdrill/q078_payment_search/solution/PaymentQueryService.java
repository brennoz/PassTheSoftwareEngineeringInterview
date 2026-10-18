package com.interview.pairingdrill.q078_payment_search.solution;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

// Why this design over the seeded god-method:
// The seeded search() and findForRisk() each contained an inline filter block with
// identical status/amount logic. Adding a new filter (e.g. currency, merchant) meant
// editing every method — a classic Open/Closed Principle violation.
//
// Specification pattern: each filter criterion becomes an independent, testable class
// implementing PaymentSpec. Composing them with .and()/.or() replaces the nested
// if/else chain. The service becomes a thin orchestrator: build a spec from criteria,
// stream through it. Adding CurrencySpec tomorrow requires zero changes here.
//
// Why buildSpec() is private not public:
// It is a factory helper for this service's own use cases. Making it public would expose
// internal wiring details and invite callers to bypass the service contract.
//
// Why Objects.requireNonNull at entry:
// Null criteria would silently return all payments — a data leak in a compliance context.
// Fail fast with a clear message rather than an NPE buried inside the stream.
@Service
public class PaymentQueryService {

    private final PaymentRepository repository;

    public PaymentQueryService(PaymentRepository repository) {
        this.repository = repository;
    }

    public List<Payment> search(SearchCriteria criteria) {
        Objects.requireNonNull(criteria, "criteria must not be null");
        PaymentSpec spec = buildSpec(criteria);
        return repository.findAll().stream()
            .filter(spec::isSatisfiedBy)
            .collect(Collectors.toList());
    }

    public List<Payment> findForRisk(SearchCriteria criteria) {
        Objects.requireNonNull(criteria, "criteria must not be null");
        // Why .and(new FlaggedForRiskSpec()) appended last:
        // The seeded findForRisk() omitted the type filter entirely — a silent bug
        // (risk-flagged CARD payments could be returned when the caller asked for WALLET only).
        // Composing with the shared buildSpec() ensures all criteria are always applied,
        // then the risk flag is layered on top. The seeded duplication that caused the bug
        // is now structurally impossible — there is one place where filters are assembled.
        PaymentSpec spec = buildSpec(criteria).and(new FlaggedForRiskSpec());
        return repository.findAll().stream()
            .filter(spec::isSatisfiedBy)
            .collect(Collectors.toList());
    }

    // buildSpec: assembles a composite PaymentSpec from the caller's criteria.
    // Starts from PaymentSpec.any() (identity — matches everything) and AND-chains
    // each non-null criterion. If all fields are null the result is any() — all payments pass.
    // What NOT to do: start from null and branch with "if spec == null assign, else and-chain"
    // — that's a null-accumulator pattern that requires every branch to handle null.
    // The identity element (any()) removes the special case entirely.
    private PaymentSpec buildSpec(SearchCriteria criteria) {
        PaymentSpec spec = PaymentSpec.any();
        if (criteria.minAmount() != null || criteria.maxAmount() != null) {
            spec = spec.and(new AmountRangeSpec(criteria.minAmount(), criteria.maxAmount()));
        }
        if (criteria.status() != null) {
            spec = spec.and(new StatusSpec(criteria.status()));
        }
        if (criteria.type() != null) {
            spec = spec.and(new TypeSpec(criteria.type()));
        }
        return spec;
    }
}
