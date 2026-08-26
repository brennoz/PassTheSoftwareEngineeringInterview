package com.interview.ood.q045_bet_slip.solution;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class SolutionTest {

    @Test
    void singleBetWonPaysStakeTimesOdds() {
        Selection selection = new Selection("Team A win", new BigDecimal("2.50"));
        SingleBet bet = new SingleBet(selection, new BigDecimal("10.00"));
        selection.settle(SelectionResult.WON);

        // Why compareTo, not assertEquals directly on BigDecimal: BigDecimal.equals() is
        // scale-sensitive ("25.00" != "25.0"), a standing rule in this project.
        assertEquals(0, bet.calculatePayout().compareTo(new BigDecimal("25.00")));
    }

    @Test
    void singleBetLostPaysZero() {
        Selection selection = new Selection("Team A win", new BigDecimal("2.50"));
        SingleBet bet = new SingleBet(selection, new BigDecimal("10.00"));
        selection.settle(SelectionResult.LOST);

        assertEquals(0, bet.calculatePayout().compareTo(BigDecimal.ZERO));
    }

    // What this verifies: payout only materializes once EVERY leg is settled WON, not the
    // moment the last one happens to be checked. A weaker test that only ever settled all
    // legs WON before asserting would miss AccumulatorBet quietly paying out on a
    // partially-settled slip — this test checks the zero-payout state mid-settlement too.
    @Test
    void accumulatorPaysOnlyWhenAllLegsWon() {
        AccumulatorBet acca = new AccumulatorBet(new BigDecimal("5.00"));
        Selection leg1 = new Selection("Leg 1", new BigDecimal("2.00"));
        Selection leg2 = new Selection("Leg 2", new BigDecimal("1.50"));
        acca.addLeg(leg1);
        acca.addLeg(leg2);

        leg1.settle(SelectionResult.WON);
        assertEquals(0, acca.calculatePayout().compareTo(BigDecimal.ZERO));

        leg2.settle(SelectionResult.WON);
        assertEquals(0, acca.calculatePayout().compareTo(new BigDecimal("15.00")));
    }

    @Test
    void accumulatorWithOneLostLegPaysZeroEvenIfOthersWon() {
        AccumulatorBet acca = new AccumulatorBet(new BigDecimal("5.00"));
        Selection leg1 = new Selection("Leg 1", new BigDecimal("2.00"));
        Selection leg2 = new Selection("Leg 2", new BigDecimal("1.50"));
        acca.addLeg(leg1);
        acca.addLeg(leg2);
        leg1.settle(SelectionResult.WON);
        leg2.settle(SelectionResult.LOST);

        assertEquals(0, acca.calculatePayout().compareTo(BigDecimal.ZERO));
        assertFalse(acca.isWon());
    }

    // What this verifies: BetSlip treats SingleBet and AccumulatorBet uniformly through the
    // Wager interface — the entire point of Composite here. It sums stake and payout across
    // heterogeneous children with no instanceof/switch on concrete wager type anywhere in
    // BetSlip's implementation.
    @Test
    void betSlipSumsStakeAndPayoutAcrossHeterogeneousWagers() {
        Selection singleSelection = new Selection("Single leg", new BigDecimal("2.00"));
        SingleBet single = new SingleBet(singleSelection, new BigDecimal("10.00"));
        singleSelection.settle(SelectionResult.WON);

        AccumulatorBet acca = new AccumulatorBet(new BigDecimal("5.00"));
        Selection accaLeg = new Selection("Acca leg", new BigDecimal("3.00"));
        acca.addLeg(accaLeg);
        accaLeg.settle(SelectionResult.WON);

        BetSlip slip = new BetSlip();
        slip.addWager(single);
        slip.addWager(acca);

        assertEquals(0, slip.getStake().compareTo(new BigDecimal("15.00")));
        assertEquals(0, slip.calculatePayout().compareTo(new BigDecimal("35.00")));
        assertTrue(slip.isWon());
    }

    // What this verifies: the recursive case that justifies BetSlip implementing Wager
    // itself rather than just holding a List<SingleBet | AccumulatorBet>. A BetSlip nested
    // inside another BetSlip must have its stake and payout summed transparently by the
    // parent, with no special-casing for "this child happens to be a container too." This
    // is the test that would fail first if BetSlip were ever refactored back to a
    // non-recursive "leaf bets only" design.
    @Test
    void nestedBetSlipPayoutIsSummedByParentSlip() {
        Selection wonSelection = new Selection("Nested single", new BigDecimal("2.00"));
        SingleBet nestedSingle = new SingleBet(wonSelection, new BigDecimal("10.00"));
        wonSelection.settle(SelectionResult.WON);

        BetSlip innerSlip = new BetSlip();
        innerSlip.addWager(nestedSingle);

        BetSlip outerSlip = new BetSlip();
        outerSlip.addWager(innerSlip);

        assertEquals(0, outerSlip.getStake().compareTo(new BigDecimal("10.00")));
        assertEquals(0, outerSlip.calculatePayout().compareTo(new BigDecimal("20.00")));
    }
}
