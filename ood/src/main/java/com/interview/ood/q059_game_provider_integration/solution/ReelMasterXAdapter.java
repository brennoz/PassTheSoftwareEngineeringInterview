package com.interview.ood.q059_game_provider_integration.solution;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Pattern: Adapter (Concrete Adapter) — wraps ReelMasterXClient. This is where the real
// pattern-justifying work happens: the caller (GameProviderRegistry) calls one synchronous
// spin() method and has NO idea a handshake + submit + poll loop happened underneath. That
// hiding is the entire point of Adapter over just calling the SDK inline everywhere spin
// logic is needed.
//
// System design note — Thread.sleep-based polling: this is a deliberate simplification for
// a single-process demo. In a real distributed system, blocking a caller's thread on
// Thread.sleep while polling a third party doesn't scale: each in-flight spin ties up a
// thread for the whole poll duration, so throughput is capped by thread pool size, not by
// actual provider latency. Production alternatives: (1) provider pushes a webhook/callback
// on completion instead of us polling, or (2) submitSpin() returns immediately and the
// caller subscribes to a Kafka/SQS completion event, decoupling request from response
// entirely (matches this project's outbox/saga patterns elsewhere in the lab). Bounded
// retries (MAX_POLL_ATTEMPTS) here also matter for a different reason: an adapter that
// polls forever turns one slow/broken provider into an unbounded resource leak — this is
// exactly the kind of "no timeout on an external call" issue this project's code-review
// rubric flags. A production version would likely wrap this in a resilience4j
// CircuitBreaker + Retry so a provider having a bad day gets short-circuited instead of
// degrading every request that touches it.
//
// Why BigDecimal.valueOf(double) and never `new BigDecimal(double)`: `new BigDecimal(0.1)`
// captures the exact binary floating-point value stored for 0.1
// (0.1000000000000000055511151231257827021181583404541015625...), not the decimal literal
// a human typed. `BigDecimal.valueOf(double)` goes via `Double.toString(double)` first,
// giving exactly what the double's canonical string representation shows. For money
// converted from a third party's double, valueOf is the only safe choice.
public class ReelMasterXAdapter implements GameProviderAdapter {

    private static final int MAX_POLL_ATTEMPTS = 5;
    private static final long POLL_INTERVAL_MILLIS = 5;

    private final ReelMasterXClient client;
    private final String apiKey;

    public ReelMasterXAdapter(ReelMasterXClient client, String apiKey) {
        this.client = client;
        this.apiKey = apiKey;
    }

    @Override
    public SpinResult spin(GameSession session, Money stake) {
        String token = client.authenticate(apiKey);
        String ticketId = client.submitSpin(token, session.sessionId(), stake.amount().doubleValue());

        for (int attempt = 0; attempt < MAX_POLL_ATTEMPTS; attempt++) {
            ReelMasterXPollResponse response = client.poll(ticketId);
            if ("COMPLETE".equals(response.status())) {
                Money winAmount = new Money(
                        BigDecimal.valueOf(response.winAmountInPounds()).setScale(2, RoundingMode.HALF_UP),
                        stake.currency());
                return new SpinResult(winAmount, response.symbols(), true);
            }
            if ("FAILED".equals(response.status())) {
                return SpinResult.failed(stake.currency());
            }
            sleepBriefly();
        }
        // Exhausted MAX_POLL_ATTEMPTS without COMPLETE — treat as failure rather than hang
        // forever. A caller-facing "spin failed, provider timed out" beats an unbounded wait.
        return SpinResult.failed(stake.currency());
    }

    private void sleepBriefly() {
        try {
            Thread.sleep(POLL_INTERVAL_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
