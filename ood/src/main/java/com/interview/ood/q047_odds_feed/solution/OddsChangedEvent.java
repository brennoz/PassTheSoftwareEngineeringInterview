package com.interview.ood.q047_odds_feed.solution;

import java.math.BigDecimal;

// Why a record, not a mutable class: an event is a fact about something that already
// happened — "these odds moved from X to Y" — and facts don't change after the fact. A
// mutable OddsChangedEvent would let one listener alter previousOdds/newOdds before the
// next listener in line sees it, making notification order silently change what later
// listeners observe. Same reasoning as q044's Account guard: a shared fact must not be
// something any one reader can quietly corrupt for the next reader.
public record OddsChangedEvent(String eventName, String selectionName,
                                BigDecimal previousOdds, BigDecimal newOdds) {
}
