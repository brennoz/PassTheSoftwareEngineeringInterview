package com.interview.algorithms.java_features.q055_top_spender_per_department;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MyAnswerTest {

    @Test
    void should_caculate_the_top_spender_per_department () {
        List<Purchase> purchases = List.of(
                                        buildPurchase("John Doe", "Biology", BigDecimal.valueOf(1000)),
                                        buildPurchase("John Doe", "Biology", BigDecimal.valueOf(500)),
                                        buildPurchase("John Doe", "Biology", BigDecimal.valueOf(300)),
                                        buildPurchase("Henrietta Bloom", "Biology", BigDecimal.valueOf(1800)),
                                        buildPurchase("Henrietta Bloom", "Biology", BigDecimal.valueOf(700)),
                                        buildPurchase("Albert Einstein", "Physic", BigDecimal.valueOf(5000)),
                                        buildPurchase("Albert Einstein", "Physic", BigDecimal.valueOf(1500)),
                                        buildPurchase("Gregor Samsa", "Physic", BigDecimal.valueOf(4500)),
                                        buildPurchase("Gregor Samsa", "Physic", BigDecimal.valueOf(400)),
                                        buildPurchase("Nicholas Von Der Malen", "Physic", BigDecimal.valueOf(3000))
                                    );

        Map<String, String> topSpenders = MyAnswer.topSpenderPerDepartment(purchases);

        assertEquals("Henrietta Bloom", topSpenders.get("Biology"));
        assertEquals("Albert Einstein", topSpenders.get("Physic"));
    }

    private Purchase buildPurchase(String employee, String department, BigDecimal amount) {
        return new Purchase(employee, department, amount);
    }
}
