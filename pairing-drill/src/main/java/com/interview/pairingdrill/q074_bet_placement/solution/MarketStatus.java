package com.interview.pairingdrill.q074_bet_placement.solution;

import java.math.BigDecimal;

// Why enum-as-Strategy, not a separate class hierarchy: the seeded if/else chain violated
// Open/Closed — adding "DELAYED" meant editing an existing method and risking every other
// branch. Each status now owns its own acceptance rule; adding DELAYED is one new constant,
// zero lines touched on OPEN/SUSPENDED/IN_PLAY/CLOSED. A full State-pattern class-per-status
// would work too but is more ceremony than this needs — no per-status mutable fields here.
public enum MarketStatus {
    OPEN {
        @Override
        public BetResult checkAcceptance(Bet bet) {
            return null; // null = no rejection, proceed to the liability check
        }
    },
    SUSPENDED {
        @Override
        public BetResult checkAcceptance(Bet bet) {
            return BetResult.rejected("Market suspended");
        }
    },
    IN_PLAY {
        @Override
        public BetResult checkAcceptance(Bet bet) {
            if (bet.getStake().compareTo(IN_PLAY_STAKE_CAP) > 0) {
                return BetResult.rejected("Stake too high for in-play market");
            }
            return null;
        }
    },
    CLOSED {
        @Override
        public BetResult checkAcceptance(Bet bet) {
            return BetResult.rejected("Market closed");
        }
    };

    private static final BigDecimal IN_PLAY_STAKE_CAP = new BigDecimal("500.00");

    public abstract BetResult checkAcceptance(Bet bet);
}
