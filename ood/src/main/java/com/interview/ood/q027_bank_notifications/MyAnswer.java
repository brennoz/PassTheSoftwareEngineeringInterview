package com.interview.ood.q027_bank_notifications;

// TODO 1: Define AccountEvent record with EventType enum (DEPOSIT, WITHDRAWAL, CLOSE)
//         and three factory methods: deposit(), withdrawal(), close()

// TODO 2: Define AccountEventListener interface with onEvent(AccountEvent) method

// TODO 3: Extend BankAccount (abstract) — add:
//         - listeners list (thread-safe)
//         - addListener() / removeListener()
//         - fireEvent() called AFTER state change in deposit(), withdraw(), close()

// TODO 4: Implement SavingsAccount (minimum balance rule)

// TODO 5: Implement CheckingAccount (overdraft limit rule)

// TODO 6: Implement EmailNotificationListener

// TODO 7: Implement FraudDetectionListener — configurable threshold, default 10,000

public class MyAnswer {
    public static void main(String[] args) {
        // smoke test — replace with your implementation
    }
}
