package com.interview.pairingdrill.q078_payment_search.answer;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
        PaymentSpec spec = buildSpec(criteria).and(new FlaggedForRiskSpec());
        return repository.findAll().stream()
            .filter(spec::isSatisfiedBy)
            .collect(Collectors.toList());
    }

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
