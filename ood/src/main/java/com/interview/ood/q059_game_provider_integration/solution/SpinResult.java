package com.interview.ood.q059_game_provider_integration.solution;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

// Why `success` and not a nullable Money: Money's own compact constructor already rejects
// null — reusing that guarantee here means callers never null-check a win amount, they
// check `success` instead. A failed spin still carries a valid zero-value Money, not null.
public record SpinResult(Money winAmount, List<String> symbols, boolean success) {

    public static SpinResult failed(Currency currency) {
        return new SpinResult(new Money(BigDecimal.ZERO, currency), List.of(), false);
    }

    public boolean isWin() {
        return success && winAmount.amount().compareTo(BigDecimal.ZERO) > 0;
    }
}
