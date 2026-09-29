package com.interview.pairingdrill.q074_bet_placement;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MyAnswerTest {

    private final BetRepository betRepository = Mockito.mock(BetRepository.class);
    private final BetPlacementService service = new BetPlacementService(betRepository);

    // Write your tests here.
}
