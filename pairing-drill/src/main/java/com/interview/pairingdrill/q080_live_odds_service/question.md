# q080 — Live Odds Service (Pairing Drill)

**Category:** pairing-drill · **Flavor:** Concurrency / synchronization · **Domain:** Sport Betting
**Seniority:** Junior pairing partners (Emma + Noah) · **Difficulty:** Medium

---

## Scenario

You're on the in-play trading team at a sport-betting platform. During last night's Premier League fixture,
the on-call engineer was paged: `LiveOddsService` started throwing errors under load and — more worryingly —
some bets were accepted on markets that had already been suspended after a red card.

Your colleagues Emma and Noah are investigating with you. The service runs in a multi-pod Spring Boot
deployment: multiple threads process incoming odds-feed messages concurrently, and the market-event
consumer (suspension/reopen) runs on a separate thread pool.

The seeded `LiveOddsService` is the current production version. Your job: identify what's wrong and fix it.

---

## What to do

1. Open `answer/LiveOddsService.java` — this is your working copy.
2. Read through the code with Emma and Noah (see `transcript.md` for their opening observations).
3. Identify the issues, fix them in `answer/`, write tests in `answer/MyAnswerTest.java` (TDD encouraged).
4. Type `review` at any point for a mid-session check-in.
5. Type `done` when finished.

`solution/` is pre-seeded — you can peek, but the discipline is on you.

---

## Constraints

- Service runs on **multiple threads** simultaneously (odds-feed + market-event consumers)
- `getOdds()` and `isSuspended()` are called from a high-frequency read path — minimize lock contention
- Use `BigDecimal` for any odds or payout value
- No `System.out.println` — use a logger

---

## AWS / Distributed Environment

| Issue | What breaks in prod |
|---|---|
| Non-thread-safe collections | On a single JVM: `ConcurrentModificationException` or silent data loss. On multiple pods: each pod has its own in-memory map — odds seen by one pod are invisible to others; Redis/ElastiCache needed for shared state. |
| `long updateCount++` | Non-atomic on a single JVM (count drifts). Across pods: each pod counts its own updates — no single authoritative count; use a Redis `INCR` for a distributed atomic counter. |
| Check-then-act (suspension + odds write) | On a single JVM: race between odds-feed thread and market-event consumer. Across pods: impossible to fix with `synchronized` (different JVMs); requires a distributed lock (Redis `SET NX PX`) or a strongly-consistent data store. |
| `String` for market status | Fragile at the Kafka payload boundary: a producer typo ("SUSPENEDED") silently deserialized — no `@JsonProperty` validation catches it. An enum + Avro schema + Schema Registry rejects unknown values at the consumer. |

---

## Maven

```bash
# Run only this drill
mvn test -pl pairing-drill -Dtest="com.interview.pairingdrill.q080_live_odds_service.answer.MyAnswerTest" -Dit.test.skip

# Run solution tests
mvn test -pl pairing-drill -Dtest="com.interview.pairingdrill.q080_live_odds_service.solution.SolutionTest"

# Run both
mvn test -pl pairing-drill -Dtest="com.interview.pairingdrill.q080_live_odds_service.answer.MyAnswerTest,com.interview.pairingdrill.q080_live_odds_service.solution.SolutionTest"
```
