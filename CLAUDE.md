# PassTheSoftwareEngineeringInterview

Exam simulation lab. Claude picks questions, you answer, Claude reviews.  
Different from SystemDesignLab (deep build) — this is breadth + time pressure + feedback loop.

---

## Command Legend

| Command | Action |
|---|---|
| `next` | Claude picks question (30% algo / 20% system-design / 15% code-review / 10% ood / 8% java / 7% devops / 10% rest) |
| `next algo` | Force algorithms category |
| `next system-design` | Force system design category |
| `next code-review` | Force "spot the issue" — Claude shows legacy Java code, you find what's wrong |
| `next ood` | Force Object-Oriented Design. Delivers a full solution-walkthrough lesson + complete commented `solution/` + empty `answer/` — see "OOD Delivery Convention" below |
| `next ood skeleton` | Same as `next ood`, but `answer/` ships pre-seeded with partial/buggy code (planted issues) instead of empty — see "OOD Delivery Convention" below |
| `next spring` | Force Spring/SpringBoot/Hibernate category |
| `next messaging` | Force Kafka/Redis/RabbitMQ/WebSockets category |
| `next nosql` | Force MongoDB/Elasticsearch/Neo4j category |
| `next db` | Force databases/SQL category |
| `next concepts` | Force DDD/EDA/Microservices/AWS/Docker/K8s category |
| `next java` | Force Java / Java 8 features category |
| `next devops` | Force DevOps — rotates clusters (containers/kubernetes/helm/ci-cd/observability/alerting/terraform/agent-infra/architecture); picks least-recently-seen cluster from PROGRESS.md |
| `next devops containers` | Force containers cluster: Dockerfile, multi-stage builds, docker-compose, .dockerignore |
| `next devops kubernetes` | Force kubernetes cluster: Pods, Deployments, Services, Ingress, HPA, ConfigMap, Secrets, probes |
| `next devops helm` | Force helm cluster: chart structure, values.yaml, templating, hooks, rollback, canary |
| `next devops ci-cd` | Force ci-cd cluster: GitLab CI/CD stages, caching, GitOps, ArgoCD, registry push |
| `next devops observability` | Force observability cluster: Datadog APM, metrics, logs, traces, SLOs, dashboards |
| `next devops alerting` | Force alerting cluster: PagerDuty, Slack webhooks, alert rules, runbooks, on-call |
| `next devops terraform` | Force terraform cluster: providers, resources, modules, state, remote backend, workspaces |
| `next devops agent-infra` | Force agent-infra cluster: deploying AI agents on K8s, rate-limit sidecars, LLM observability, model version pinning |
| `next devops architecture` | Force full end-to-end DevOps architecture question — always hard; bypasses difficulty tracker |
| `next payments` | Force payment/fintech domain: card auth, idempotency, PCI-DSS, fraud detection, transaction logging, settlement flows |
| `next banking` | Force banking domain: double-entry ledger, KYC/AML, loan origination, SWIFT/SEPA, interest calculation, regulatory reporting |
| `next betting` | Force sport betting domain: odds feed, bet placement, liability management, settlement saga, responsible gambling, in-play latency |
| `next easy` | Force easy difficulty |
| `next medium` | Force medium difficulty |
| `next hard` | Force hard difficulty |
| `next {category} {slug}` | Force category + topic hint — e.g. `next ood bank`, `next algo sliding-window`. Scopes lesson folder and question topic. |
| `next pairing-drill` | Force the pairing-drill category — live mentoring/refactor exercise with roleplay. **Explicit-invoke only, not in the weighted `next` rotation** — see "Pairing Drill Category" below |
| `next pairing-drill {level}` | Force pairing-partner seniority — `junior` (default) / `mid` / `senior` / `pm`. Combinable with difficulty and flavor in the same line, e.g. `next pairing-drill senior hard spring` — see "Pairing Drill Category" |
| `next pairing-drill skeleton` | Same as `next pairing-drill`, but `answer/` ships pre-seeded with a partial attempt already in progress instead of an exact copy of the seeded code — see "Pairing Drill Category" |
| `next {easy\|medium\|hard} pairing-drill` | Force issue-density scope for this session (easy ≈2–3 issues, medium ≈4–6, hard ≈7+ tangled) — medium is default. **Scope selector only — no pass/fail auto-progression for this category**, unlike every other `next easy/medium/hard` use |
| `hint` | Claude gives a hint — saved to `hints.md` in question folder |
| `solution` | Claude shows full solution — saved to `hints.md` |
| `review` | **`pairing-drill` only.** Repeatable, non-terminal check-in: Claude reads your current files, runs the tests, reacts in character, appends to `transcript.md`. Not the same as `done` |
| `done` | Submit answer — Claude reviews + writes `review.md` |
| `skip` | Skip question, log as skipped in PROGRESS.md |
| `pause` | Save state to SESSION.md |
| `resume` | Restore from SESSION.md |
| `progress` | Show PROGRESS.md summary |
| `retry q{NNN}` | Retry a specific past question |

---

## Question Distribution

| Category | Probability | Technologies |
|---|---|---|
| Algo Coding | 30% | Data structures, sorting, graphs, trees, DP, JUnit, EDA code exercises; **domain-relevant:** LRU cache, sliding window rate limiter, token bucket, interval scheduling, graph traversal (BFS/DFS), heap-based top-K, stream processing, odds/payout calculation, ledger balance algorithms |
| System Design | 20% | Distributed systems, scalability, component design; **domain-relevant:** card auth flow, fraud detection, real-time odds feed, bet settlement saga, loan origination pipeline, ledger system, idempotent payment API, multi-region failover, settlement reconciliation |
| Code Review / Spot the Issue | 15% | Legacy Java code snippets — find design flaws, Spring anti-patterns, concurrency bugs, distributed system pitfalls, bad algorithms, security holes, JPA N+1, memory leaks, etc. |
| OO Design (coding) | 10% | Class diagrams → Java skeletons: SOLID, design patterns (Strategy, Observer, Factory, Builder, Decorator, Command, etc.), UML to code, parking lot, elevator, chess, ATM, vending machine |
| Java / Java 8 | 8% | Core Java, Java 8 (streams, lambdas, Optional, CompletableFuture, default methods, method refs), Java 11/17/21 features, collections, concurrency, JVM internals |
| DevOps | 7% | 9 clusters — containers (Dockerfile, multi-stage, compose), kubernetes (Pods/Deployments/HPA/Ingress/probes), helm (charts/templating/rollback), ci-cd (GitLab/ArgoCD/GitOps), observability (Datadog APM/SLO/dashboards), alerting (PagerDuty/Slack/runbooks), terraform (providers/modules/state), agent-infra (AI agents on K8s, LLM observability, model versioning), architecture (full pipeline end-to-end) |
| Rest | 10% | Spring/Boot/Hibernate/JPA, Spring Security, Kafka, Redis, RabbitMQ, PostgreSQL, Oracle, MongoDB, Elasticsearch, Neo4j, AWS, DDD, EDA, Microservices patterns, gRPC, REST design |

`pairing-drill` is **not** in this table — it's explicit-invoke only (`next pairing-drill`). A
45–60 minute roleplay drill doesn't fit the breadth/time-pressure rhythm a random `next` is
built around; see "Pairing Drill Category" below.

---

@docs/domain-verticals.md

---

## AWS / Distributed Context Rule

**For every question where applicable**, Claude must add an **"In AWS / Distributed Environment"** section covering:
- What AWS service(s) map to this component (e.g. SQS, ElastiCache, RDS Multi-AZ, Kinesis)
- What breaks at scale / on a multi-node cluster (e.g. in-memory state, local file system, single-instance assumptions)
- What the failure mode is in a distributed deployment (e.g. split-brain, duplicate processing, cache inconsistency)
- How AWS mitigates it (e.g. ElastiCache for shared cache, SQS FIFO for ordering, DynamoDB conditional writes for idempotency)

This applies to:
- Code review issues (add AWS impact to each issue's consequence)
- System design questions (AWS component mapping required)
- Concept questions (e.g. Kafka → MSK, Redis → ElastiCache, Postgres → RDS Multi-AZ)
- Algorithm questions where the algorithm solves a distributed problem (rate limiter, LRU cache)

---

@docs/ai-extension-rule.md

---

## Code Review Question Format

Claude writes a realistic Java snippet (20–60 lines) with **N planted issues** (N depends on difficulty: easy=2–3, medium=4–6, hard=7+). Issues span multiple categories intentionally — not all in one area.

Claude does **not** reveal N upfront. After `done`, Claude scores: issues found / total planted, per category.

Answer format in `MyAnswer.md`:
```
## Issues Found

### 1. [Category] — [short title]
Line X: [what is wrong]
Consequence: [what breaks in prod / at scale]
Fix: [what to change]

### 2. ...
```

---

## Difficulty Progression

Claude starts at **easy**. After 2 consecutive passes → bump to next level. On fail → hold level (or drop one if 2 consecutive fails). User can override with `next easy/medium/hard` at any time.

---

@docs/ood-delivery-convention.md

---

## Question Workflow

1. User says `next` (or category/difficulty/slug variant)
2. Claude determines category + slug (e.g. `ood/bank`)
3. **Lesson check:** read `topics/{category}/{slug}/INDEX.md`
   - Concepts needed for the question **already listed** → skip to step 5
   - INDEX missing or concept **not listed** → announce *"No lesson for {slug} yet — generating before the question"*, then:
     a. Determine question spec (concept + difficulty) first
     b. Invoke `/teach` to generate exactly the lesson(s) needed
     c. Save to `topics/{category}/{slug}/0NNN-*.html`; update `INDEX.md`
4. Open lesson in browser
5. Generate full question file set on disk, post question in chat
6. Ideal time shown (informational — no hard cutoff)
7. User opens IDE, edits skeleton file
8. `hint` → Claude gives hint, appends to `hints.md`
9. `done` → Claude reads answer file, reviews against rubric, writes `review.md`
10. PROGRESS.md updated with result row

---

## Folder Structure Per Question

```
{module}/src/main/java/com/interview/{module}/q{NNN}_{slug}/
├── question.md      ← problem statement + constraints + skeleton description
├── Solution.java    ← skeleton (DO NOT EDIT — reference only)
└── MyAnswer.java    ← YOU write here

{module}/src/test/java/com/interview/{module}/q{NNN}_{slug}/
└── MyAnswerTest.java ← write tests here (TDD encouraged)

Concept/design questions:
system-design/q{NNN}_{slug}/
├── question.md
├── hints.md         ← populated on hint/solution requests
└── MyAnswer.md      ← you write here

concepts/q{NNN}_{slug}/
├── question.md
├── hints.md
└── MyAnswer.md

OO Design (coding skeletons):
ood/src/main/java/com/interview/ood/q{NNN}_{slug}/
├── question.md      ← problem + class diagram description
├── Solution.java    ← reference skeleton (interfaces + stubs)
└── MyAnswer.java    ← YOU implement here

devops/q{NNN}_{slug}/
├── question.md
├── hints.md
└── MyAnswer.md      ← or MyAnswer.yml / MyDockerfile as appropriate

Code review / spot the issue:
code-review/q{NNN}_{slug}/
├── question.md      ← contains the broken Java code snippet + "what is wrong?"
├── hints.md
└── MyAnswer.md      ← list every issue you find, categorised

Pairing drill (see "Pairing Drill Category" below — **forward-only from the next new
`pairing-drill` question; q072–q074 predate this shape and are not retrofitted**, same
precedent as the OOD Delivery Convention):
pairing-drill/src/main/java/com/interview/pairingdrill/q{NNN}_{slug}/
├── question.md
├── {DomainClass}.java  ← seeded messy code — REFERENCE ONLY, not edited directly. Real class
│                          name, not "MyAnswer". This is "what we started with."
├── {supporting entity/interface files as the scenario needs}
├── hints.md            ← populated on hint/solution requests
├── transcript.md       ← appended on every `review`
├── solution/           ← self-contained reference (own package, redeclares any entity/
│                          interface it needs to change) — pre-seeded from the start, same as
│                          `ood`'s `solution/`. Nothing stops you opening it early; the
│                          discipline of not peeking is on you, not enforced by withholding it.
└── answer/             ← where you actually work. Starts as an exact copy of the seeded
                           messy code (default) — or, under `next pairing-drill skeleton`, a
                           partial attempt already in progress (planted mistakes mid-refactor,
                           same spirit as `ood skeleton`'s attempt1)

pairing-drill/src/test/java/com/interview/pairingdrill/q{NNN}_{slug}/
├── solution/
│   └── SolutionTest.java
└── answer/
    └── MyAnswerTest.java   ← starts empty, mocks pre-wired (default) — or, per question,
                               pre-seeded with tests that pass but don't actually exercise the
                               real bug, same pattern as q056/q060 in `algorithms`
```

`review`/`done` operate on `answer/`, not the main-folder file directly.

**After `done`:** Claude creates `review.md` in the same folder.

---

@docs/review-rubrics.md

---

## Lesson Pipeline (`/teach` skill)

`/teach` skill is the **only** lesson system. `teach me` is retired.

### Output path override
When `/teach` runs in this project, save lessons to `topics/{category}/{slug}/` — **not** `./lessons/`. Numbering is per-topic: `0001-*.html` within each slug folder restarts at 0001, not global.

### Topic folder structure
```
topics/
  ood/{slug}/          e.g. bank, parking-lot, elevator, atm
  algo/{slug}/         e.g. two-sum, sliding-window, bfs
  system-design/{slug}/
  java/{slug}/
  spring/{slug}/
  messaging/{slug}/
  nosql/{slug}/
  db/{slug}/
  devops/{slug}/
  concepts/{slug}/
  projects/{company}/  e.g. pairingco — real, named-employer interview-day prep kits
```

### `projects/{company}/` — company-specific interview prep

Distinct from the categories above: not a concept being taught, but a prep kit for a real,
named employer's actual interview process (format, rubric, drills, scripts). Differences from
the standard flow:

- **No teach-first gate.** Skip the `INDEX.md` "concept already listed" check — these are
  always written fresh for the specific engagement, not reused across companies.
- **One slug per company, flat.** All docs for that employer's process live directly under
  `topics/projects/{company}/`, numbered `0NNN-*.html` same as any other topic folder — not
  nested per interview round. A later round for the same company just continues the numbering.
- **Local-only, never pushed.** The entire `topics/projects/` tree is excluded via
  `.git/info/exclude` (not `.gitignore` — no trace of the exclusion rule in repo history
  either). Confirm this exclusion is still in place before ever running a broad `git add`.

### INDEX.md — concept registry per topic
Every topic folder contains `INDEX.md`. Format:

```markdown
# {slug} — Lesson Index

| File | Concepts covered |
|---|---|
| 0001-*.html | Template Method, BigDecimal, synchronized |
| 0002-*.html | Strategy Pattern, AccountStatus enum |
```

Claude reads this before deciding whether to generate a new lesson. If the concept needed for the next question is already listed, skip lesson generation.

### Teach-first decision tree
```
next {category} {slug}
  │
  ├─ topics/{category}/{slug}/INDEX.md exists
  │   AND concepts for this question already listed?
  │     YES → skip lesson generation
  │     NO  → announce "No lesson for {slug} yet — generating before the question"
  │           → determine question spec first (concept + difficulty)
  │           → invoke /teach for exactly the lesson(s) needed
  │           → save to topics/{category}/{slug}/0NNN-*.html
  │           → append entry to INDEX.md
  │
  └─ generate full question file set
```

### question.md lesson link
Every generated `question.md` includes a `Lessons:` line at the top linking to the HTML lesson(s) that cover its concepts:
```
**Lessons:** [0001 Domain Model](../../../../../topics/ood/bank/0001-domain-model.html)
```

### Aid distinction

| Aid | When to use |
|---|---|
| `/teach` (auto) | Fires before first question on a new topic — zero knowledge assumed |
| `hint` | Stuck mid-exercise — some knowledge, need nudge |
| `solution` | Gave up |

---

## Study Flow (before retry)

Before retrying a question, read:
- `hints.md` — every hint/solution given during original attempt
- `review.md` — rubric feedback from Claude

---

@docs/pairing-drill-spec.md

---

## Maven Modules

| Module | Run tests |
|---|---|
| algorithms | `mvn test -pl algorithms` |
| spring-questions | `mvn test -pl spring-questions` |
| messaging | `mvn test -pl messaging` |
| nosql | `mvn test -pl nosql` |
| databases | `mvn test -pl databases` |
| ood | `mvn test -pl ood` |
| interview-prep | `mvn test -pl interview-prep` |
| pairing-drill | `mvn test -pl pairing-drill` |
| All | `mvn test` |

---

## Testing Stack

**New tests: JUnit 5 + Mockito only — no AssertJ.** `assertj-core` stays as a pom dependency for existing tests that already use it (not retroactively rewritten), but every test written from here on uses plain JUnit 5 (`org.junit.jupiter.api.Assertions`) and Mockito, not AssertJ's fluent `assertThat`.

- `BigDecimal` comparisons: `assertEquals(0, actual.compareTo(expected))` — never `assertEquals(expected, actual)` directly, since `BigDecimal.equals()` is scale-sensitive (`100.00` != `100.0`).
- Exceptions: `assertThrows(SomeException.class, () -> ...)`.
- Timeouts/deadlock checks: `assertTimeoutPreemptively(Duration.ofSeconds(N), () -> ...)`.
