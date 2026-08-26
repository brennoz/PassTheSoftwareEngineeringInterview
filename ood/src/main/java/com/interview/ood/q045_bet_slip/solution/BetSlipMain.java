package com.interview.ood.q045_bet_slip.solution;

import java.math.BigDecimal;

// Runnable demo — not a test. Walks the same "talk it through" journey an interviewer would
// want: a single bet settled won, an accumulator settled leg-by-leg (payout only appears once
// every leg is won), an accumulator with one lost leg (payout stays zero forever), a slip
// mixing both bet types summed uniformly, and finally a BetSlip nested inside another
// BetSlip — the recursive case that is the actual point of using Composite here.
public class BetSlipMain {

    public static void main(String[] args) {
        System.out.println("=== Single bet, settled WON ===");
        Selection matchWinner = new Selection("Arsenal vs Chelsea - Arsenal to win", new BigDecimal("2.50"));
        SingleBet single = new SingleBet(matchWinner, new BigDecimal("10.00"));
        matchWinner.settle(SelectionResult.WON);
        System.out.println("Single bet payout: " + single.calculatePayout());

        System.out.println();
        System.out.println("=== Accumulator, legs settled one at a time ===");
        AccumulatorBet acca = new AccumulatorBet(new BigDecimal("5.00"));
        Selection leg1 = new Selection("Leg 1", new BigDecimal("1.80"));
        Selection leg2 = new Selection("Leg 2", new BigDecimal("2.00"));
        Selection leg3 = new Selection("Leg 3", new BigDecimal("1.50"));
        acca.addLeg(leg1);
        acca.addLeg(leg2);
        acca.addLeg(leg3);
        leg1.settle(SelectionResult.WON);
        System.out.println("Payout with 1/3 legs settled WON: " + acca.calculatePayout());
        leg2.settle(SelectionResult.WON);
        leg3.settle(SelectionResult.WON);
        System.out.println("Payout with 3/3 legs settled WON (1.80 x 2.00 x 1.50 x 5.00 stake): "
                + acca.calculatePayout());

        System.out.println();
        System.out.println("=== Accumulator, one leg LOST -> payout stays zero forever ===");
        AccumulatorBet losingAcca = new AccumulatorBet(new BigDecimal("5.00"));
        Selection winLeg = new Selection("Winning leg", new BigDecimal("1.80"));
        Selection loseLeg = new Selection("Losing leg", new BigDecimal("2.00"));
        losingAcca.addLeg(winLeg);
        losingAcca.addLeg(loseLeg);
        winLeg.settle(SelectionResult.WON);
        loseLeg.settle(SelectionResult.LOST);
        System.out.println("Accumulator payout (one leg lost): " + losingAcca.calculatePayout());

        System.out.println();
        System.out.println("=== BetSlip mixing a SingleBet + AccumulatorBet, summed uniformly ===");
        BetSlip slip = new BetSlip();
        slip.addWager(single);
        slip.addWager(acca);
        System.out.println("Slip total stake: " + slip.getStake());
        System.out.println("Slip total payout: " + slip.calculatePayout());
        System.out.println("Slip fully won: " + slip.isWon());

        System.out.println();
        System.out.println("=== Nested BetSlip: session slip inside an account-level slip ===");
        BetSlip sessionSlip = new BetSlip();
        sessionSlip.addWager(single);
        sessionSlip.addWager(acca);

        BetSlip accountSlip = new BetSlip();
        accountSlip.addWager(sessionSlip);
        accountSlip.addWager(losingAcca);
        System.out.println("Account slip total stake: " + accountSlip.getStake());
        System.out.println("Account slip total payout: " + accountSlip.calculatePayout());
        System.out.println("Account slip fully won (false — losingAcca lost): " + accountSlip.isWon());
    }
}
