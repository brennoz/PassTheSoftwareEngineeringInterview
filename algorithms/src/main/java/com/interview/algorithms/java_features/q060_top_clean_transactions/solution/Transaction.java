package com.interview.algorithms.java_features.q060_top_clean_transactions.solution;

import java.math.BigDecimal;

public record Transaction(String id, BigDecimal amount, boolean flaggedForReview) {
}
