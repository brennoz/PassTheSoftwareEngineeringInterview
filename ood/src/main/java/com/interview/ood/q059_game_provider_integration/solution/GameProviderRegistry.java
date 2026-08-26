package com.interview.ood.q059_game_provider_integration.solution;

import java.util.Map;

// Pattern: Facade (thin) — one entry point (`playSpin`) for callers who don't want to know
// adapters exist as a concept, just "play a spin for this session." Deliberately NOT a
// Factory that constructs adapters internally: adapters are constructed once at startup
// (each wraps a stateful/configured SDK client, e.g. an API key) and injected here as a
// ready map — keeps this class free of any provider construction/config knowledge, so
// wiring a new provider only touches whatever composition root builds this map.
public class GameProviderRegistry {

    private final Map<String, GameProviderAdapter> adaptersByProviderId;

    public GameProviderRegistry(Map<String, GameProviderAdapter> adaptersByProviderId) {
        this.adaptersByProviderId = Map.copyOf(adaptersByProviderId);
    }

    public SpinResult playSpin(GameSession session, Money stake) {
        GameProviderAdapter adapter = adaptersByProviderId.get(session.providerId());
        if (adapter == null) {
            throw new IllegalArgumentException("Unknown provider: " + session.providerId());
        }
        return adapter.spin(session, stake);
    }
}
