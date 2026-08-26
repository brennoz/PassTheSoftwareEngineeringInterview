package com.interview.ood.q056_card_authorization.answer;

import java.math.BigDecimal;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MyAnswerTest {

    @Test
    void should_authorize_a_visa () {

        Card visa = new Card("1234567890654321", "Nick Webber", YearMonth.of(2027, 8));

        CardAuthorizationProcessor cardProcessor = new VisaAuthorizationProcessor();

        AuthorizationResult result = cardProcessor.authorize(visa, BigDecimal.valueOf(4999));

        assertEquals(Status.APPROVED, result.status());
    }

    @Test
    void should_authorize_a_mastercard () {

        Card visa = new Card("1234567890654321", "Nick Webber", YearMonth.of(2027, 8));

        CardAuthorizationProcessor cardProcessor = new MasterCardAuhtorizationProcessor();

        AuthorizationResult result = cardProcessor.authorize(visa, BigDecimal.valueOf(3000));

        assertEquals(Status.APPROVED, result.status());
    }    
}
