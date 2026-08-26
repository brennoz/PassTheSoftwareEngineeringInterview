package com.interview.ood.q059_game_provider_integration.solution;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// TDD note: no mocking framework needed here — SlotForgeClient/ReelMasterXClient are given
// fake third-party SDKs already built as small configurable test doubles (see their
// constructors), which is realistic: a real third-party SDK usually can't be mocked cleanly
// anyway (final classes, no interface), so adapters are commonly tested against a
// hand-rolled fake exactly like this rather than Mockito.
class SolutionTest {

    private final Currency gbp = Currency.getInstance("GBP");
    private final Money oneRound = new Money(new BigDecimal("1.00"), gbp);

    @Test
    void slotForgeAdapterConvertsPenceToPoundsCorrectly() {
        SlotForgeClient client = new SlotForgeClient(275, List.of("BAR", "BAR", "BAR"), true);
        SlotForgeAdapter adapter = new SlotForgeAdapter(client);

        SpinResult result = adapter.spin(new GameSession("s1", "p1", "slotforge"), oneRound);

        assertEquals(0, result.winAmount().amount().compareTo(new BigDecimal("2.75")));
        assertTrue(result.isWin());
    }

    @Test
    void slotForgeAdapterRejectsAStakeWithSubPenceFraction() {
        SlotForgeClient client = new SlotForgeClient(0, List.of(), false);
        SlotForgeAdapter adapter = new SlotForgeAdapter(client);
        Money fractionalStake = new Money(new BigDecimal("1.005"), gbp);

        // longValueExact() must throw rather than silently truncate £1.005 to 100p.
        assertThrows(ArithmeticException.class, () ->
                adapter.spin(new GameSession("s1", "p1", "slotforge"), fractionalStake));
    }

    @Test
    void reelMasterXAdapterPollsUntilCompleteAndConvertsDoubleToBigDecimalSafely() {
        // Completes on the 3rd poll — proves the adapter actually loops, not just polls once.
        ReelMasterXClient client = new ReelMasterXClient(3, 5.50, List.of("WILD", "WILD", "WILD"));
        ReelMasterXAdapter adapter = new ReelMasterXAdapter(client, "test-key");

        SpinResult result = adapter.spin(new GameSession("s2", "p1", "reelmasterx"), oneRound);

        assertEquals(0, result.winAmount().amount().compareTo(new BigDecimal("5.50")));
        assertTrue(result.isWin());
    }

    @Test
    void reelMasterXAdapterReturnsFailedSpinIfProviderNeverCompletesInTime() {
        // pollsUntilComplete is far beyond MAX_POLL_ATTEMPTS (5) — this is the resilience
        // case: a stuck/broken provider must not hang the caller forever.
        ReelMasterXClient client = new ReelMasterXClient(1000, 5.50, List.of("WILD", "WILD", "WILD"));
        ReelMasterXAdapter adapter = new ReelMasterXAdapter(client, "test-key");

        SpinResult result = adapter.spin(new GameSession("s2", "p1", "reelmasterx"), oneRound);

        assertFalse(result.success());
        assertFalse(result.isWin());
    }

    @Test
    void registryRoutesToTheCorrectAdapterByProviderId() {
        GameProviderRegistry registry = new GameProviderRegistry(Map.of(
                "slotforge", new SlotForgeAdapter(new SlotForgeClient(100, List.of("A"), true)),
                "reelmasterx", new ReelMasterXAdapter(new ReelMasterXClient(1, 2.00, List.of("B")), "key")
        ));

        SpinResult slotForgeResult = registry.playSpin(new GameSession("s1", "p1", "slotforge"), oneRound);
        SpinResult reelMasterXResult = registry.playSpin(new GameSession("s2", "p1", "reelmasterx"), oneRound);

        assertEquals(0, slotForgeResult.winAmount().amount().compareTo(new BigDecimal("1.00")));
        assertEquals(0, reelMasterXResult.winAmount().amount().compareTo(new BigDecimal("2.00")));
    }

    @Test
    void registryThrowsForAnUnknownProviderId() {
        GameProviderRegistry registry = new GameProviderRegistry(Map.of(
                "slotforge", new SlotForgeAdapter(new SlotForgeClient(0, List.of(), false))
        ));

        assertThrows(IllegalArgumentException.class, () ->
                registry.playSpin(new GameSession("s1", "p1", "unknown-provider"), oneRound));
    }

    @Test
    void spinResultIsNotAWinWhenWinAmountIsZero() {
        SlotForgeClient client = new SlotForgeClient(0, List.of("LEMON", "BAR", "CHERRY"), false);
        SlotForgeAdapter adapter = new SlotForgeAdapter(client);

        SpinResult result = adapter.spin(new GameSession("s1", "p1", "slotforge"), oneRound);

        assertFalse(result.isWin());
    }
}
