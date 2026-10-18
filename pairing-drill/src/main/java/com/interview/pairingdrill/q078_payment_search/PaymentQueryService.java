package com.interview.pairingdrill.q078_payment_search;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class PaymentQueryService {

    private final PaymentRepository repository;

    public PaymentQueryService(PaymentRepository repository) {
        this.repository = repository;
    }

    public List<Payment> search(SearchCriteria criteria) {
        List<Payment> all = repository.findAll();
        return all.stream()
            .filter(p -> {
                if (criteria.getMinAmount() > 0 && p.getAmount().doubleValue() < criteria.getMinAmount()) return false;
                if (criteria.getMaxAmount() > 0 && p.getAmount().doubleValue() > criteria.getMaxAmount()) return false;
                if (criteria.getStatus() != null && !p.getStatus().equals(criteria.getStatus())) return false;
                if (criteria.getType() != null && !p.getType().equals(criteria.getType())) return false;
                return true;
            })
            .collect(Collectors.toList());
    }

    public List<Payment> findForRisk(SearchCriteria criteria) {
        List<Payment> all = repository.findAll();
        return all.stream()
            .filter(p -> {
                if (criteria.getMinAmount() > 0 && p.getAmount().doubleValue() < criteria.getMinAmount()) return false;
                if (criteria.getStatus() != null && !p.getStatus().equals(criteria.getStatus())) return false;
                if (!p.isFlaggedForRisk()) return false;
                return true;
            })
            .collect(Collectors.toList());
    }
}
