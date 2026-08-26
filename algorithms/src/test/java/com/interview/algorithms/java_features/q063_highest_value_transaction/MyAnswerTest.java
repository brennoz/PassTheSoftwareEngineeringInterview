package com.interview.algorithms.java_features.q063_highest_value_transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MyAnswerTest {

    @Test
    void should_fetch_the_highest_transaction () {
        List<Transaction> transactions = List.of(
            tx("1", 1500),
            tx("2", 2500),
            tx("3", 500)
        );

        Optional<Transaction> highestTx = MyAnswer.highestValueTransaction(transactions);

        assertEquals(0, highestTx.get().amount().compareTo(BigDecimal.valueOf(2500)));
    }

    @Test
    

    private Transaction tx(String id, double amount) {
        return new Transaction(id, BigDecimal.valueOf(amount));
    }
}
