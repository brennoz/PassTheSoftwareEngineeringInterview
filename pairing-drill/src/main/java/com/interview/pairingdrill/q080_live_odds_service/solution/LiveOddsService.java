package com.interview.pairingdrill.q080_live_odds_service.solution;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class LiveOddsService {

    // Fix 1: ConcurrentHashMap instead of HashMap.
    // HashMap is not thread-safe — concurrent put() calls from multiple odds-feed threads can
    // corrupt the internal bucket array (infinite loop in Java 7, silent lost updates in Java 8+).
    // ConcurrentHashMap uses bucket-level locking: concurrent reads are lock-free (safe for
    // getOdds()), concurrent writes only lock the affected bucket (not the whole map).
    // Alternative rejected: Collections.synchronizedMap(new HashMap<>()) wraps every operation
    // in a single global lock — safe, but a single bottleneck for all threads.
    // What NOT to do: keeping HashMap and adding synchronized to just acceptUpdate() leaves
    // getOdds() reading from an unsynchronized HashMap — still broken.
    private final Map<String, BigDecimal> currentOdds = new ConcurrentHashMap<>();

    // Fix 2: ConcurrentHashMap.newKeySet() instead of HashSet.
    // HashSet is backed by a HashMap — same thread-safety problem. ConcurrentHashMap.newKeySet()
    // is a Set view backed by a ConcurrentHashMap — safe for concurrent add/remove/contains.
    // Alternative rejected: Collections.synchronizedSet(new HashSet<>()) — single global lock,
    // same throughput problem as synchronizedMap.
    // What NOT to do: using a plain HashSet with synchronized on suspendMarket() but not on
    // the contains() check in acceptUpdate() — the check still reads the HashSet unsafely.
    private final Set<String> suspendedMarkets = ConcurrentHashMap.newKeySet();

    // Fix 3: AtomicLong instead of long + ++.
    // long updateCount++ is a read-modify-write: read current value, add 1, write back. Three
    // separate CPU instructions — another thread can interleave between any of them.
    // AtomicLong.incrementAndGet() uses a single CPU CAS (compare-and-swap) instruction — atomic
    // by definition, no explicit locking needed.
    // Alternative rejected: synchronized on getUpdateCount() alone doesn't fix the write race
    // in acceptUpdate(). You'd need synchronized on both methods, which then serialises reads.
    // AtomicLong lets unsynchronized reads still see a consistent value.
    private final AtomicLong updateCount = new AtomicLong(0);

    private final OddsRepository oddsRepository;
    private final MarketRepository marketRepository;

    public LiveOddsService(OddsRepository oddsRepository, MarketRepository marketRepository) {
        this.oddsRepository = oddsRepository;
        this.marketRepository = marketRepository;
    }

    // Fix 4: synchronized on acceptUpdate AND suspendMarket/reopenMarket.
    // The core bug: acceptUpdate checks suspendedMarkets.contains() then acts on the result.
    // Between the check and the act, another thread can call suspendMarket() — the market is
    // now suspended but acceptUpdate doesn't know, so it writes odds to a suspended market.
    // In sport betting this is a regulatory violation: bets were accepted on stale/invalid odds.
    //
    // synchronized makes the check+act atomic with respect to the suspend/reopen operations.
    // ConcurrentHashMap.newKeySet() is still correct here: the synchronized block protects
    // the compound operation; ConcurrentHashMap protects unsynchronized single-operation reads
    // (isSuspended(), getOdds()) from corrupt internal state.
    //
    // Alternative rejected: using a ReentrantReadWriteLock — correct and higher throughput for
    // read-heavy workloads, but adds complexity. synchronized is simpler and sufficient here.
    // What NOT to do: using volatile on the collections — volatile only guarantees visibility
    // of the reference, not the contents of the object it points to. Completely wrong fix.
    public synchronized void acceptUpdate(String marketId, BigDecimal newOdds, long version) {
        if (suspendedMarkets.contains(marketId)) {
            return;
        }
        // Fix 5: BigDecimal for odds — double 1.65 cannot be represented exactly in IEEE 754
        // (stored as 1.6499999999999998...). payout = stake * odds on millions of bets
        // accumulates rounding error that becomes real money at settlement time.
        // Always use BigDecimal for any financial or odds value in a betting system.
        currentOdds.put(marketId, newOdds);
        updateCount.incrementAndGet(); // fix 3
        oddsRepository.save(new OddsSnapshot(marketId, newOdds, version));
    }

    // synchronized — must be mutually exclusive with acceptUpdate to prevent the check-then-act
    // race described above.
    public synchronized void suspendMarket(String marketId) {
        suspendedMarkets.add(marketId);
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("Market not found: " + marketId));
        // Fix 6: MarketStatus.SUSPENDED instead of the String "SUSPENDED".
        // String-based status: typo "SUSPENEDED" silently compiles and stores a value no query
        // will ever match — market appears ACTIVE to every downstream consumer.
        // Enum: typo is a compile error. Refactoring is rename-safe. Switch expressions are
        // exhaustive (the compiler forces you to handle every status).
        market.setStatus(MarketStatus.SUSPENDED);
        marketRepository.save(market);
    }

    public synchronized void reopenMarket(String marketId) {
        suspendedMarkets.remove(marketId);
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("Market not found: " + marketId));
        market.setStatus(MarketStatus.ACTIVE); // fix 6
        marketRepository.save(market);
    }

    // Not synchronized: read-only, ConcurrentHashMap guarantees safe concurrent reads.
    // Synchronizing reads too would eliminate all concurrency benefit for a read-heavy service.
    public BigDecimal getOdds(String marketId) {
        return currentOdds.getOrDefault(marketId, BigDecimal.ZERO);
    }

    public boolean isSuspended(String marketId) {
        return suspendedMarkets.contains(marketId);
    }

    public long getUpdateCount() {
        return updateCount.get();
    }
}
