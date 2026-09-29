## Pairing Drill Category

For live pairing/mentoring-refactor interview rounds (e.g. a company's technical pairing
stage) — not a general-purpose category, explicit-invoke only (`next pairing-drill`).

**How it differs from every other category:**
- **Roleplay is always on.** Every `pairing-drill` question runs with two personas that Claude
  plays throughout — see "Pairing partner seniority" below for who they are. This is the
  entire point of the category; there is no non-roleplay variant. Treat them as people being
  mentored (or, for `pm`, briefed) through the change: validate good instincts, push back on
  premature abstraction or blunt corrections, ask clarifying questions in character rather than
  out of it.
- **Real, compiled, tested code, three-way split.** `pairing-drill` is a registered Maven
  module (`mvn test -pl pairing-drill`), same tier as `ood` — not markdown-only like
  `code-review`. The main-folder seeded file (real domain class name, e.g.
  `CustomerMembershipService.java`, not literally `MyAnswer.java`) is the original, read-only
  reference. `solution/` is the complete, heavily commented fix, pre-seeded from the start.
  `answer/` is where you actually work. See "Folder Structure Per Question" above for the full
  shape — this is forward-only from the next new question; q072–q074 predate it.
- **`review` vs `done`.** `review` is repeatable and non-terminal: Claude reads the current
  state of `answer/`, runs `mvn test -pl pairing-drill`, reacts in character, and appends the
  exchange plus a diff summary to `transcript.md`. `done` is terminal, same as everywhere else
  — writes `review.md` against the pairing rubric (technical communication, balanced
  engineering, prioritization, reading unfamiliar code, leading safely) and closes the session.
- **Difficulty is a scope selector, not a progression tracker.** `next easy/medium/hard
  pairing-drill` controls issue density and how tangled the planted issues are (easy ≈2–3,
  medium ≈4–6 *default*, hard ≈7+) — it does not feed a pass/fail auto-bump the way every other
  category's difficulty tracker does. "Finishing is not the goal" still doesn't map to a
  pass/fail bump; `review.md` stays qualitative-only regardless of the scope picked.
- **Not in the weighted `next` rotation** — see the note under Question Distribution.

**Pairing partner seniority.** `next pairing-drill {level}` — `junior` (default) / `mid` /
`senior` / `pm`. Combinable freely with difficulty and flavor in one line (Claude parses
intent, no strict positional grammar — same as `next devops {cluster}`).

| Level | What changes |
|---|---|
| `junior` (default) | Naive questions, needs reasoning explained, validates good instincts, pushes back gently — every example built so far |
| `mid` | Knows the basics, pushes back with concrete "why not X" alternatives, catches some mistakes unprompted, less hand-holding |
| `senior` | Near-peer — challenges design choices harder, may propose a competing approach you have to defend or adapt to; closer to real peer review than mentoring |
| `pm` | Not a technical peer — asks business/timeline/risk questions ("will this slip the release," "what's the customer impact if we ship half of this"). **Replaces one of the two technical personas for that session** (e.g. "Junior A" stays technical, "Junior B" becomes the PM) rather than adding a third participant or being a standalone flavor |

`pm` sessions add one sub-note to `review.md`'s "technical communication" axis specifically:
whether you translated the fix into jargon-free, business-impact/timeline terms rather than
pattern-naming and tradeoff articulation. Same five axes, same rubric structure, everywhere
else unchanged.

**Flavors — what varies between sessions.** `pairing-drill` is a delivery mechanism (roleplay +
real files + iterative `review`/`done`), not a single fixed content type. Each `next
pairing-drill` picks a flavor from the weighted rotation below, rotating within each cluster
rather than repeating the same shape.

**Flavor weighting: 70% general / 30% specific-topics.** When picking a flavor, draw ~70%
from the General cluster and ~30% from the Specific-topics cluster. Within each cluster, pick
the least-recently-used flavor — same rotation spirit as `next devops` picking clusters.

**Cross-cutting rules (apply to any session regardless of cluster):**
- **Functional idioms:** any General session can additionally plant one functional-idioms issue
  (imperative loop → stream, manual null-check → `Optional`) as one of its N planted issues
  when it fits naturally. Not the main event — one planted issue, not the focus.
- **Records:** appear as a refactoring target in General sessions when a mutable POJO value
  object is present (refactor to an immutable record). Appear as the natural type choice in
  Message parsing sessions for event payloads and DTOs.
- **Messaging / EDA correctness:** not a standalone flavor. When EDA issues arise naturally
  (duplicate processing, missing outbox, offset committed before processing), mention them in
  `review.md` under AWS/distributed impact — not as planted issues in their own session.
- **Generic methods (`<T>`):** any session can plant one raw-type → bounded-generic issue.
  Issue shape: seeded code uses `Object`, a raw `List`, or an unchecked cast; fix is a generic
  method (`public <T extends X> ...`) where the caller doesn't supply the type — the compiler
  infers it. One planted issue, not the focus. Appears when the seeded code has a utility or
  helper method that is artificially concrete.
- **ArgumentCaptor:** approximately 1-in-3 sessions, a test uses `verify(mock).save(any())`
  (too loose to catch a wrong payload) as a planted gap. Fix: replace with
  `ArgumentCaptor<X>` — capture the argument, assert on its fields. Applies whenever
  something is saved to a repository or published to a bus and the current test doesn't verify
  what was actually passed.
- **Chain repetition (window=2):** when generating a new session, read the last 2 pairing-drill
  exercises from PROGRESS.md and extract their planted-issue technique types (e.g. `BigDecimal`,
  `@Transactional` boundary, enum `==` vs `.equals()`, `Optional` misuse, idempotency key,
  record vs mutable POJO, generic raw type). Pick 1–2 of those techniques and plant them as
  **secondary** issues in the new session — the primary flavor still drives 3–4 issues, the echo
  techniques add 1–2 more. Goal: recently-drilled material reappears before it fades, without
  making the session feel like a repeat. Do not echo a technique that is already the primary
  focus of the new flavor.

---

### General cluster (70%)

| Flavor | What it drills | Example so far |
|---|---|---|
| Service smells | Duplication, primitive obsession, magic numbers, encapsulation leaks — the default, method-level refactor | q072 (membership pricing) |
| Correctness-in-disguise | A vague reported symptom hiding a real bug (partial-failure ordering, N+1, swallowed exception) rather than a pure structural ask | q073 (order fulfillment) |
| OOD structural | A SOLID violation needing an actual pattern extraction (State/Strategy over an if/else chain, LSP violation, God class via inheritance misuse) — draws on the same pattern vocabulary as `ood/`; also the natural home for a caching bug (stale/unbounded cache, no invalidation) when the fix is a structural extraction rather than a one-line change | q074 (bet placement) |
| Concurrency / synchronization | Race conditions, non-atomic check-then-act, missing `synchronized`/`volatile`/`ConcurrentHashMap`, missing idempotency keys on a mutating operation — often paired with OOD structural | q074 (bet placement) |
| Type safety / primitive obsession | String-based status/type → enum; mutable POJO value object → record; `double`/`float` for money/odds → `BigDecimal`; `boolean` flag params → named enum; raw `String` IDs → typed value objects. Issue pool scales with difficulty: easy = 2–3 issues, medium = 4–5, hard = all of the above tangled. **Always bundles one OOD/logic-layer issue** showing the production consequence: missing `@Transactional` on the operation the type change exposed, missing idempotency key that String-based lookup was masking, race condition on a counter tied to string comparison, or an invalid state transition that typed enums would have made a compile error. The type fix is primary; the logic issue is the "so what breaks in prod" companion. | q077 (market settlement) |
| Spring-specific | Field injection vs constructor injection, `@Transactional` boundary/propagation, bean scope, JPA lazy-loading N+1. **Two modes per scenario:** plain-Java and Spring-microservices-context version of the same underlying bug | q076 (loan disbursement) |
| Code-review hybrid | Classic spot-the-issue findings (security, swallowed exceptions, JPA anti-patterns) combined with a refactor task — not just structural smells | not yet generated |
| Algorithmic-complexity-in-disguise | An O(n²)-or-worse operation buried inside otherwise-ordinary domain code, discovered mid-refactor | not yet generated |
| Search / filtering | Seeded code has a naive O(n) linear scan or a God-method `if (name != null && status != null && ...)` filter chain hardcoded in the service. Task: extract a composable `Specification<T>` / `Predicate<T>` design — same territory as `ood/q042` (library search). Issue pool: no-criteria call returns full unbounded scan, OR/AND composition not possible (adding a new filter requires editing the service), each filter path duplicated across service methods. Draws on the Specification pattern, `Predicate.and`/`or`/`negate`, and the Open/Closed principle. Seeded domain deliberately has 3+ filterable fields so composability is non-trivial. | not yet generated |

---

### Specific-topics cluster (30%)

| Flavor | What it drills | Example so far |
|---|---|---|
| Java SDK / functional idioms | Streams, `Optional`, `Collectors`, method references — same territory as the `java` category, but delivered through the pairing-drill mechanic. The full focus, not a cross-cutting planted issue | not yet generated |
| Resilience / fault tolerance | Missing timeout, no circuit breaker, retry without backoff (thundering herd), synchronous call chain with no fallback — draws on this project's `resilience4j` stack | not yet generated |
| CompletableFuture pipelines | Seeded code has sequential blocking calls (independent calls made one-by-one). Fix: compose with `CompletableFuture.supplyAsync` + `allOf`/`thenCombine`/`thenCompose`/`exceptionally`. Mid-migration surfaces CF-specific bugs: wrong combinator (`thenApply` wrapping a CF-returning method → nested CF), exception swallowed inside `thenApply`, `join()` on main thread defeating async | not yet generated |
| Virtual threads | Seeded code uses `Executors.newFixedThreadPool(N)` for I/O-bound tasks — platform thread pool is the bottleneck. Fix: migrate to `Executors.newVirtualThreadPerTaskExecutor()`. Surfaces the `synchronized`-pinning caveat: any `synchronized` block in the hot path negates the benefit, replace with `ReentrantLock` | not yet generated |
| Message parsing / deserialisation | JSON (Jackson) and XML parsing in a domain context (e.g. parsing a Kafka `BetSettledEvent` JSON, an ACH payment XML). Planted issue pool: `ObjectMapper` instantiated per call (expensive — should be a shared bean); missing `@JsonProperty` on record/POJO fields → silent null on deserialisation; `JsonProcessingException` swallowed → malformed message silently dropped, no DLQ; raw `String` where a typed enum or record belongs; no null-check after deserialisation → NPE downstream. Records used as the natural type for event payloads and DTOs | not yet generated |

---

Domain rotates across this project's three verticals (payment/fintech, banking, sport betting)
plus general SaaS/e-commerce. When a flavor is rotated into one of the three verticals, its
planted issues should draw from that vertical's "always flag" trigger list rather than
inventing new compliance content — e.g. a betting-domain Resilience session plants a missing
market-suspension lock, not a generic issue. When generating a new `pairing-drill` question,
pick whichever flavor + domain combination hasn't been used recently.

**Out of scope, on purpose:** infra/config review (Dockerfile, K8s manifests, Terraform, CI
pipelines) stays entirely in the `devops` category. `pairing-drill`'s mechanic — real compiled
and tested Java — doesn't fit YAML/HCL review.

**Company-specific prep content** (real employer names, predicted interview format/rubric,
domain-flavored practice snippets) belongs in `topics/projects/{company}/`, not here. `pairing-drill/`
question folders stay domain-generic (`Customer`/`Order`/`Shipment`) since this folder **is**
pushed to git, unlike `topics/projects/`.
