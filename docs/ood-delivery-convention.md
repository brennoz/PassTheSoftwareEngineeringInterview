## OOD Delivery Convention

Applies to the `ood` category only — no other category changes delivery shape.

**Plain `next ood` (and `next ood {slug}`):**
1. Teach-first lesson generated before the question (existing `topics/ood/{slug}/INDEX.md`
   check applies as usual) — but the lesson itself is now a full solution-walkthrough doc:
   design reasoning, TDD narrative (red → green shown, not just described), and complete
   code for the primary recommended approach. Not a lighter pre-teaching primer.
2. `solution/` — complete, heavily commented (WHY, not what), TDD-ordered reference
   implementation matching what the lesson walks through. Always includes a runnable
   `{Entity}Main.java` demonstrating the solution end-to-end (construct the core objects,
   drive them through a realistic sequence of calls, print/assert observable outcomes) —
   same precedent as `q030_library_system/solution/LibraryMain.java`.
3. `answer/` — empty (a `.gitkeep` placeholder only). Write from scratch, or study
   `solution/` and the lesson first — the choice is yours, not enforced by withholding
   content.

**`next ood skeleton` (and `next ood skeleton {slug}`):**
1. Identical lesson + `solution/` as plain `next ood`.
2. `answer/` — pre-seeded with partial/buggy code (planted issues), same spirit as
   `q030_library_system/answer/attempt1`'s historical planted bugs (State pattern miswired,
   BigDecimal `==` reference-equality bug). Task: complete it and find what's wrong before
   `done`.

**Folder mirroring (both modes):** `solution/` and `answer/` are created under BOTH
`src/main/java/.../q{NNN}_{slug}/` AND `src/test/java/.../q{NNN}_{slug}/` — not just
`src/main`. Test code (the reference test suite in `solution/`, and wherever the learner's
own tests eventually land in `answer/`) mirrors the same `solution/`/`answer/` split as the
implementation code, exactly matching each package's directory layout.

**Both modes:** TDD is mandatory. JUnit 5 + Mockito only — no AssertJ (standing project-wide
rule, restated here since it's load-bearing for OOD specifically).

**Comment density (both modes, both `solution/` and its tests):** heavier than this
project's general default. Every non-trivial class or method in `solution/` explains, inline:
1. The design decision made (which pattern, which structure) and why it fits this specific
   requirement — not a restatement of what the code does.
2. At least one alternative that was considered and rejected, with the concrete reason
   (e.g. "enum + switch was rejected because adding a state means editing every switch
   block").
3. What NOT to do — the specific mistake this design guards against, ideally naming the
   actual bug shape (silent state corruption, a strategy holding elevator-specific state and
   losing reusability, a public setter bypassing a guard).

Test files get the same treatment, not just production code: comment on WHY a test is
structured the way it is, what it actually verifies vs. what a superficially-similar-but-
wrong version of the same test would miss. If a test was rewritten after an initial version
turned out not to test what it claimed, say so and explain why the fixed version is correct
— this project already caught real examples of that mid-session and it's worth keeping the
lesson attached to the code, not just in chat history.

This does not apply retroactively to already-built OOD questions (q013, q026, q027, q030,
q032, q035, q036, q037, q040) — forward-only from q042. The `{Entity}Main.java`
requirement specifically is forward-only from q044 — q042 and q043 predate it and were not
retrofitted.
