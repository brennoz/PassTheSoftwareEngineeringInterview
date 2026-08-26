# q050 Review — Sealed Interfaces + Pattern Matching for Switch

**Result: PASS — 3/3 tests, all hard constraints met**

---

## What You Got Right

- Single `switch` **expression** with record deconstruction patterns for all three cases —
  `Circle(double radius)`, `Rectangle(double width, double height)`, `Triangle(double base,
  double height)` — components used directly, no `.radius()`/`.width()` etc. accessor calls.
- **No `default` branch** — relied on the compiler proving exhaustiveness from `Shape`'s
  `permits` list, exactly the point of the exercise.
- No `instanceof`, no `if`/`else` anywhere.
- Correct formulas: `π × r²`, `width × height`, `(base × height) / 2` — the division-by-2
  placement (`(base * height) / 2` vs the reference's `0.5 * base * height`) is a harmless
  equivalent, not an issue.
- Tests: one per shape, matches the reference suite's shape and delta-tolerance handling for
  the circle case (`1e-9`) — correctly anticipated that a `Math.PI` comparison needs a delta,
  not exact `assertEquals`.

## Minor Notes (not deductions)

- `switch(shape)` — no space before the parenthesis. Purely a formatting nit, several style
  guides (including this project's general Java style elsewhere) put a space there
  (`switch (shape)`), but it has zero functional effect.

## Summary

Second clean pass in a row at `java` category — correct use of sealed-type exhaustiveness,
record patterns, and appropriately-sized tests, right after picking the concepts up from the
lesson generated on the spot. No gaps to flag.

---

**Difficulty progression:** 2nd consecutive pass at `java` (easy) → bumping to **medium** for
the next `next java`.
