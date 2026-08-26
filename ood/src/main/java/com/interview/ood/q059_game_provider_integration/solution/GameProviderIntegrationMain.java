package com.interview.ood.q059_game_provider_integration.solution;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Map;

public class GameProviderIntegrationMain {

    public static void main(String[] args) {
        Currency gbp = Currency.getInstance("GBP");
        Money stake = new Money(new BigDecimal("1.00"), gbp);

        SlotForgeClient slotForgeClient = new SlotForgeClient(250, List.of("CHERRY", "CHERRY", "BAR"), true);
        ReelMasterXClient reelMasterXClient = new ReelMasterXClient(3, 5.50, List.of("WILD", "WILD", "WILD"));

        GameProviderRegistry registry = new GameProviderRegistry(Map.of(
                "slotforge", new SlotForgeAdapter(slotForgeClient),
                "reelmasterx", new ReelMasterXAdapter(reelMasterXClient, "demo-api-key")
        ));

        SpinResult slotForgeResult = registry.playSpin(
                new GameSession("s1", "player-1", "slotforge"), stake);
        System.out.println("SlotForge  -> win=" + slotForgeResult.isWin()
                + " amount=" + slotForgeResult.winAmount().amount() + " symbols=" + slotForgeResult.symbols());

        SpinResult reelMasterXResult = registry.playSpin(
                new GameSession("s2", "player-1", "reelmasterx"), stake);
        System.out.println("ReelMasterX -> win=" + reelMasterXResult.isWin()
                + " amount=" + reelMasterXResult.winAmount().amount() + " symbols=" + reelMasterXResult.symbols());

        // Same caller code (registry.playSpin) for both providers, despite one being a
        // single sync call and the other a handshake + submit + poll loop underneath —
        // that's the Adapter pattern paying off.
    }
}
