# External Questions — Payment Domain

Source: Visa technical screening (anonymised — treat as payment/banking domain questions)

---

## Q018 — Team Formation Efficiency
→ `algorithms/two_pointers/q018_team_formation_efficiency/`

Given `skill[]` of even length, form teams of 2 where every team has equal skill sum.
Return sum of efficiencies (`a * b` per team), or `-1` if impossible.

---

## Q019 — Max Array Correlation
→ `algorithms/greedy/q019_max_array_correlation/`

Given fixed array `a[]` and rearrangeable array `b[]` of same length.
Correlation = sum of `b[i]` where `b[i] > a[i]`.
Rearrange `b` to maximise correlation. Return max correlation.

---

## Q020 — Max OR Sum
→ `algorithms/bit_manipulation/q020_max_or_sum/`

Given `arr[]` and `k` operations. Each op: pick index `i`, replace `arr[i]` with `arr[i] * 2`.
Return maximum possible bitwise-OR of all elements after at most `k` operations.
