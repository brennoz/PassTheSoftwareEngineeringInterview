package com.interview.ood.q027_bank_notifications;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyAnswerTest {

    // TODO: implement tests after filling in MyAnswer.java
    //
    // Suggested tests:
    //
    // @Test
    // void listener_receives_event_on_deposit() {
    //     List<AccountEvent> received = new ArrayList<>();
    //     SavingsAccount account = new SavingsAccount("ACC-1", new BigDecimal("1000.00"), new BigDecimal("100.00"));
    //     account.addListener(received::add);
    //     account.deposit(new BigDecimal("200.00"));
    //     assertEquals(1, received.size());
    //     assertEquals(AccountEvent.EventType.DEPOSIT, received.get(0).type());
    //     assertEquals(new BigDecimal("200.00"), received.get(0).amount());
    // }
    //
    // @Test
    // void fraud_listener_fires_alert_above_threshold() {
    //     List<AccountEvent> fraudEvents = new ArrayList<>();
    //     CheckingAccount account = new CheckingAccount("ACC-2", new BigDecimal("50000.00"), new BigDecimal("5000.00"));
    //     FraudDetectionListener fraud = new FraudDetectionListener(new BigDecimal("10000.00")) {
    //         @Override public void onEvent(AccountEvent event) {
    //             super.onEvent(event);
    //             if (event.type() == AccountEvent.EventType.WITHDRAWAL
    //                     && event.amount().compareTo(new BigDecimal("10000.00")) > 0)
    //                 fraudEvents.add(event);
    //         }
    //     };
    //     account.addListener(fraud);
    //     account.withdraw(new BigDecimal("15000.00"));
    //     assertEquals(1, fraudEvents.size());
    // }
    //
    // @Test
    // void fraud_listener_silent_for_normal_withdrawal() { ... }

    @Test
    void placeholder() {
        // remove once real tests are written
        assertTrue(true);
    }
}
