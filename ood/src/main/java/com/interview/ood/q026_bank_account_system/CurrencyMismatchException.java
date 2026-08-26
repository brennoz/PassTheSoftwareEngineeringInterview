package com.interview.ood.q026_bank_account_system;

import java.util.Currency;

public class CurrencyMismatchException extends RuntimeException {
    public CurrencyMismatchException(Currency expected, Currency actual) {
        super("Currency mismatch: expected " + expected + " but got " + actual);
    }
}
