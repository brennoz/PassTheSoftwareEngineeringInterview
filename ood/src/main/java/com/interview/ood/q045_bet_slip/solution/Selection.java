package com.interview.ood.q045_bet_slip.solution;

import java.math.BigDecimal;

// Why a plain value object, not a Wager: a Selection is a pick inside an accumulator, not a
// stake-bearing bet in its own right. The alternative — Selection implements Wager, so
// AccumulatorBet could hold List<Wager> legs like BetSlip does — was considered and
// rejected: it would let a caller build an accumulator leg out of another SingleBet
// (implying that leg is independently staked), which is not how a real parlay works. Only
// the accumulator's own stake exists; legs are just odds + an eventual outcome.
public class Selection {

    private final String eventName;
    private final BigDecimal odds;
    private SelectionResult result;

    public Selection(String eventName, BigDecimal odds) {
        this.eventName = eventName;
        this.odds = odds;
        this.result = SelectionResult.PENDING;
    }

    public String getEventName() {
        return eventName;
    }

    public BigDecimal getOdds() {
        return odds;
    }

    public SelectionResult getResult() {
        return result;
    }

    // What NOT to do: don't expose a public setResult(SelectionResult) setter that any
    // caller can flip at will — settle() names the one legitimate operation (the event has
    // concluded) instead of a generic mutator that reads the same at a call site whether
    // it's a legitimate settlement or an accidental overwrite.
    public void settle(SelectionResult result) {
        this.result = result;
    }
}
