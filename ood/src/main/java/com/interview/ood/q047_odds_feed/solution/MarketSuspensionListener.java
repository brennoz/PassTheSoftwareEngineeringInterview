package com.interview.ood.q047_odds_feed.solution;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

// Pattern: Observer (Concrete Observer) — reacts to the same OddsChangedEvent as
// PriceDisplayListener but for a completely different purpose (real "suspend the market on
// a drastic move" behaviour, not just showing a number). This is the actual point of
// Observer here: OddsFeed fires one event, and two independently-evolving concerns consume
// it without knowing about each other or about OddsFeed's internals.
// Alternative rejected: hardcoding the threshold as a constant — rejected because different
// markets/events need different suspension sensitivity in a real sportsbook (a football
// match-winner market and a tennis in-play market don't share one universal "drastic move"
// definition); the constructor parameter is what the question's constraints require.
// What NOT to do: don't suspend based on the raw odds difference (newOdds - previousOdds) —
// a move from 1.10 to 1.20 (a huge relative jump on short odds) and a move from 10.00 to
// 10.10 (negligible on long odds) both have the same raw difference of 0.10 but wildly
// different real-world significance; the percentage-of-previousOdds calculation below is
// what makes the threshold mean the same thing across every selection's odds range.
public class MarketSuspensionListener implements OddsListener {

    private final BigDecimal thresholdPercentage;
    private final Set<String> suspendedSelections = new CopyOnWriteArraySet<>();

    public MarketSuspensionListener(BigDecimal thresholdPercentage) {
        this.thresholdPercentage = thresholdPercentage;
    }

    @Override
    public void onOddsChanged(OddsChangedEvent event) {
        BigDecimal previousOdds = event.previousOdds();
        BigDecimal move = event.newOdds().subtract(previousOdds).abs();
        BigDecimal movePercentage = move
                .divide(previousOdds, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        if (movePercentage.compareTo(thresholdPercentage) >= 0) {
            suspendedSelections.add(event.selectionName());
        }
    }

    public boolean isSuspended(String selectionName) {
        return suspendedSelections.contains(selectionName);
    }
}
