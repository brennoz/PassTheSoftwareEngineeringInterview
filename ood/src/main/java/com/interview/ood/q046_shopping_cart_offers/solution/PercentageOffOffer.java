package com.interview.ood.q046_shopping_cart_offers.solution;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Pattern: Decorator (Concrete Decorator) — applies a percentage discount to whatever total
// the layer beneath it produced, not to some separately-tracked "original" total. This is
// the class that makes wrapping order matter: PercentageOffOffer wrapped around a
// BuyOneGetOneFreeOffer discounts the post-BOGOF total; wrapped the other way round, BOGOF
// would discount the post-percentage total instead. Neither order is "wrong" in the
// abstract — which one is correct depends on the actual voucher terms, which is exactly why
// this is the caller's choice to make via construction order, not a policy baked into either
// decorator.
// Why divide(..., 4, RoundingMode.HALF_UP) rather than a plain divide(): 100 doesn't divide
// every possible percentage cleanly (e.g. 33.33...%); an unscaled divide() throws
// ArithmeticException the first time a caller passes a non-terminating percentage, which
// is a production outage waiting to happen, not a hypothetical.
public class PercentageOffOffer extends CartOfferDecorator {

    private final BigDecimal percentage;

    public PercentageOffOffer(PricedCart wrapped, BigDecimal percentage) {
        super(wrapped);
        this.percentage = percentage;
    }

    @Override
    public BigDecimal getTotal() {
        BigDecimal total = wrapped.getTotal();
        BigDecimal discountFraction = percentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal retainedFraction = BigDecimal.ONE.subtract(discountFraction);
        return total.multiply(retainedFraction);
    }
}
