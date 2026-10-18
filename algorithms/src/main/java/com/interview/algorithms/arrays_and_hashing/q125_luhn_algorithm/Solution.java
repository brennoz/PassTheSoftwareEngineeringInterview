package com.interview.algorithms.arrays_and_hashing.q125_luhn_algorithm;

public class Solution {

    // Algorithm: traverse right-to-left; double every second digit; subtract 9 if doubled > 9; sum % 10 == 0
    public static boolean isValidCreditCard(String cardNumber) {
        int sum = 0;
        boolean doubleDigit = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            char c = cardNumber.charAt(i);
            if (!Character.isDigit(c)) continue;
            int digit = c - '0';
            if (doubleDigit) {
                digit *= 2;
                if (digit > 9) digit -= 9;
            }
            sum += digit;
            doubleDigit = !doubleDigit;
        }
        return sum % 10 == 0;
    }
}
