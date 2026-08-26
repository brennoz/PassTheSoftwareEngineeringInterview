package com.interview.algorithms.java_features.q061_highest_stake_per_market.solution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Trade-off vs Solution.java: the collision decision ("keep whichever has the higher stake")
// is a one-line BinaryOperator passed to toMap functionally; here it's an explicit
// containsKey-check-then-compare branch repeated at the call site. Same logic, but if a second
// place in the codebase needed "highest stake wins" merge semantics, this version's decision
// isn't reusable as a value the way BinaryOperator.maxBy(...) is — it would have to be
// copy-pasted or manually extracted into a helper method.
public class ImperativeApproach {

    public static Map<String, Bet> highestStakePerMarket(List<Bet> bets) {
        Map<String, Bet> result = new HashMap<>();
        for (Bet bet : bets) {
            Bet existing = result.get(bet.marketId());
            if (existing == null || bet.stake().compareTo(existing.stake()) > 0) {
                result.put(bet.marketId(), bet);
            }
        }
        return result;
    }
}
