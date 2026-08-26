# Review — Q011 Maximum Subarray

**Result: ✅ Pass**  
**Tests: 2/2**

## Correctness
Kadane's algorithm — correct. Extend or restart at each element. Seed with nums[0] handles all-negative.

## Complexity
Time: O(N). Space: O(1). Optimal.

## Issues
- Remove `main()` before interview — use MyAnswerTest instead.

## Follow-up to mention in interview
Can track start/end indices: reset `start = i` when restarting; update `end = i` when new max found.
