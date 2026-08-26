# Q009 — Best Time to Buy and Sell Stock

**Category:** Algorithms  
**Difficulty:** Easy  
**Ideal Time:** 15 min

---

## Problem

You are given an array `prices` where `prices[i]` is the price of a stock on day `i`.

You want to maximise profit by choosing a **single day to buy** and a **different day in the future to sell**.

Return the maximum profit. If no profit is possible, return `0`.

---

## Examples

```
Input:  [7,1,5,3,6,4]  →  5   (buy day 1 at price 1, sell day 4 at price 6)
Input:  [7,6,4,3,1]    →  0   (prices only fall — no transaction = 0)
Input:  [1,2]          →  1
Input:  [2,1]          →  0
Input:  [1]            →  0
```

---

## Constraints

- `1 <= prices.length <= 10^5`
- `0 <= prices[i] <= 10^4`
- You may only complete **one** transaction (buy once, sell once)
- Must sell **after** you buy (no time travel)

---

## What to Implement

```java
public int maxProfit(int[] prices)
```

---

## Skeleton

See `Solution.java` for reference.  
Write your solution in `MyAnswer.java`.  
Write tests in `MyAnswerTest.java`.
