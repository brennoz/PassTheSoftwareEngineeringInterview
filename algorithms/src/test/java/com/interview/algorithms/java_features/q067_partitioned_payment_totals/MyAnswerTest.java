package com.interview.algorithms.java_features.q067_partitioned_payment_totals;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.interview.algorithms.java_features.q067_partitioned_payment_totals.MyAnswer.Transaction;

public class MyAnswerTest {

    @Test
    void should_generate_a_map_with_successfull_and_unsuccessful_transactions() {
        //Given
        List<Transaction> transactions = List.of(
            tx("1",BigDecimal.valueOf(100),false),
            tx("2",BigDecimal.valueOf(200),true),
            tx("3",BigDecimal.valueOf(300),true),
            tx("4",BigDecimal.valueOf(1000),false),
            tx("5",BigDecimal.valueOf(500),true),
            tx("6",BigDecimal.valueOf(400),false)
        );
        //when
        Map<Boolean, BigDecimal> result = MyAnswer.totalByOutcome(transactions);
        //Then
        assertEquals(0, BigDecimal.valueOf(1000).compareTo(result.get(Boolean.TRUE)));
        assertEquals(0, BigDecimal.valueOf(1500).compareTo(result.get(Boolean.FALSE)));
    }

    @Test
    void should_generate_a_map_with_zero_amount_for_successfull_txs() {
        //Given
        List<Transaction> transactions = List.of(
            tx("1",BigDecimal.valueOf(100),false),
            tx("4",BigDecimal.valueOf(1000),false),
            tx("6",BigDecimal.valueOf(400),false)
        );
        //when
        Map<Boolean, BigDecimal> result = MyAnswer.totalByOutcome(transactions);
        //Then
        assertEquals(0, BigDecimal.ZERO.compareTo(result.get(Boolean.TRUE)));
        assertEquals(0, BigDecimal.valueOf(1500).compareTo(result.get(Boolean.FALSE)));
    }    

    private MyAnswer.Transaction tx(String id, BigDecimal amount, boolean successfull){
        return new MyAnswer.Transaction(id, amount, successfull);
    }
}
