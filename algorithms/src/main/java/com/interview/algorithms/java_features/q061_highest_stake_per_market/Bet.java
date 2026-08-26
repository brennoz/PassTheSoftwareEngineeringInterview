package com.interview.algorithms.java_features.q061_highest_stake_per_market;

import java.math.BigDecimal;

public record Bet(String id, String marketId, BigDecimal stake) {
}
