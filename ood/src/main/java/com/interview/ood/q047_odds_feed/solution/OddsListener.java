package com.interview.ood.q047_odds_feed.solution;

// Pattern: Observer — the interface OddsFeed depends on instead of any concrete listener
// class. Alternative rejected: giving OddsFeed a fixed set of typed hooks (e.g.
// onPriceDisplayUpdate(), onLiabilityCheck()) instead of one generic listener interface —
// rejected because every new downstream reaction (a settlement trigger, an audit logger)
// would require editing OddsFeed itself to add another hook, exactly the coupling Observer
// exists to remove.
public interface OddsListener {
    void onOddsChanged(OddsChangedEvent event);
}
