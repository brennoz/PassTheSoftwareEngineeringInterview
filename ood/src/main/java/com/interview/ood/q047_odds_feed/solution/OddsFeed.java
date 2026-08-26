package com.interview.ood.q047_odds_feed.solution;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

// Pattern: Observer (Subject) — holds current odds per selection and a list of listeners,
// notifying all of them after every change. Alternative rejected: a plain ArrayList<OddsListener>
// with manual synchronized blocks around add/remove/iterate — rejected in favour of
// CopyOnWriteArrayList because publishOddsChange() iterates listeners while a listener's own
// onOddsChanged() could itself call addListener()/removeListener() (e.g. a listener
// unregistering itself after one alert); CopyOnWriteArrayList's iterator is a stable
// snapshot, so that in-flight mutation can never throw ConcurrentModificationException or
// corrupt the notification pass already underway.
// What NOT to do: don't fire listeners before updating currentOdds — a listener reading
// getLatestOdds() or re-deriving state from OddsFeed mid-notification must see the new
// price already in effect, not the stale one it's currently being told about.
public class OddsFeed {

    private final Map<String, BigDecimal> currentOdds = new ConcurrentHashMap<>();
    private final List<OddsListener> listeners = new CopyOnWriteArrayList<>();

    public void addListener(OddsListener listener) {
        listeners.add(listener);
    }

    public void removeListener(OddsListener listener) {
        listeners.remove(listener);
    }

    // Why getOrDefault(selectionName, newOdds) rather than requiring a prior price to
    // exist: a selection's very first published price has nothing to have "moved" from.
    // Defaulting previousOdds to newOdds gives that first publish a zero-percent move,
    // which is the only honest answer — treating it as a 100% move (against a missing
    // baseline of zero) would falsely trip MarketSuspensionListener on a brand new market.
    public void publishOddsChange(String eventName, String selectionName, BigDecimal newOdds) {
        BigDecimal previousOdds = currentOdds.getOrDefault(selectionName, newOdds);
        currentOdds.put(selectionName, newOdds);

        OddsChangedEvent event = new OddsChangedEvent(eventName, selectionName, previousOdds, newOdds);
        for (OddsListener listener : listeners) {
            listener.onOddsChanged(event);
        }
    }
}
