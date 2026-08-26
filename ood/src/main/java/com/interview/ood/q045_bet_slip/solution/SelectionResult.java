package com.interview.ood.q045_bet_slip.solution;

// Why a 3-state enum, not boolean won: PENDING must stay distinguishable from LOST — an
// unsettled selection has zero payout today, same as a confirmed loss, but they are not the
// same fact. AccumulatorBet.isWon() below depends on telling "not decided yet" apart from
// "already lost" — collapsing them to a boolean would make a still-pending accumulator leg
// look identical to a settled loser.
public enum SelectionResult {
    PENDING, WON, LOST
}
