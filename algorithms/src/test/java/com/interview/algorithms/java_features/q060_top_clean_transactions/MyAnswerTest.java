package com.interview.algorithms.java_features.q060_top_clean_transactions;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MyAnswerTest {

    @Test
    void should_return_3_flagged_transactions() {

        List<Transaction> transactions = List.of(
            tx("1", BigDecimal.valueOf(250), false),
            tx("2", BigDecimal.valueOf(345), true),
            tx("3", BigDecimal.valueOf(1234), true),
            tx("4", BigDecimal.valueOf(660), true),
            tx("5", BigDecimal.valueOf(9876), false),
            tx("6", BigDecimal.valueOf(345), true),
            tx("7", BigDecimal.valueOf(123), false)
        );

        List<Transaction> topNTxs = MyAnswer.topCleanTransactions(transactions, 3);

        assertEquals(3, topNTxs.size());
        assertEquals("3", topNTxs.get(0).id());
        assertEquals("4", topNTxs.get(1).id());
        assertEquals("2", topNTxs.get(2).id());
    }

    @Test
    void should_return_4_flagged_transactions() {

        List<Transaction> transactions = List.of(
            tx("1", BigDecimal.valueOf(250), false),
            tx("2", BigDecimal.valueOf(345), true),
            tx("3", BigDecimal.valueOf(1234), true),
            tx("4", BigDecimal.valueOf(660), true),
            tx("5", BigDecimal.valueOf(9876), false),
            tx("6", BigDecimal.valueOf(345), true),
            tx("7", BigDecimal.valueOf(123), false)
        );

        List<Transaction> topNTxs = MyAnswer.topCleanTransactions(transactions, 4);

        assertEquals(4, topNTxs.size());
        assertEquals("3", topNTxs.get(0).id());
        assertEquals("4", topNTxs.get(1).id());
        assertEquals("2", topNTxs.get(2).id());
        assertEquals("6", topNTxs.get(3).id());
    }    

    private Transaction tx(String id, BigDecimal amount, boolean flaggedForReview) {
        return new Transaction(id, amount, flaggedForReview);
    }
}
