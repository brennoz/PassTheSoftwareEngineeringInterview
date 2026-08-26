# Review — Q009 Best Time to Buy and Sell Stock

**Result: ✅ Pass**  
**Tests: 2/2**

---

## Correctness
Two-pointer approach correct. `l` tracks cheapest buy day seen, `r` scans forward for best sell.

## Complexity
- Time: O(N) — single pass. ✅ Optimal.
- Space: O(1). ✅

## Subtle Bug (tests don't expose it)
```java
if (prices[r] < prices[l]) {
    l++;   // wrong — should be l = r
```
`l++` moves one step right, which may land on a price higher than `prices[r]`. Correct intent: jump left pointer to the new minimum → `l = r`. Tests pass because given inputs don't hit this divergence. Mention it in interview.

## Style
1. `profit` variable only used inside loop — inline it: `maxProfit = Math.max(prices[r] - prices[l], maxProfit)`
2. Remove `main()` before submitting — use `MyAnswerTest` instead.
