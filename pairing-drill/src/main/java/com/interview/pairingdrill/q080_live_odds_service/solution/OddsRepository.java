package com.interview.pairingdrill.q080_live_odds_service.solution;

// Redeclared in solution package so save() accepts solution.OddsSnapshot (BigDecimal odds).
public interface OddsRepository {
    void save(OddsSnapshot snapshot);
}
