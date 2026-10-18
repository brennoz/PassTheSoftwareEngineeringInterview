# q125 — Luhn Algorithm (Credit Card Validation)

**Category:** Algorithms | **Difficulty:** Easy | **Ideal time:** 15 min

---

## Problem

Implement the Luhn algorithm (Mod 10) to validate a credit card number.

**Steps:**
1. Start from the rightmost digit.
2. Double every second digit moving left.
3. If doubling gives > 9, subtract 9.
4. Sum all digits.
5. If `total % 10 == 0` → valid.

Non-digit characters (spaces, dashes) are ignored.

---

## Examples

```
Input:  "4539148803436467"   Output: true   (Visa test number)
Input:  "1234567812345678"   Output: false
Input:  "4539-1488-0343-6467"  Output: true  (dashes ignored)
```

---

## Constraints

- Input contains only digits and possibly spaces/dashes
- 1 <= effective digit count <= 19

---

## Your task

Edit `MyAnswer.java` — implement `static boolean isValidCreditCard(String cardNumber)`.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
