# PassTheSoftwareEngineeringInterview

A personal AI-powered study lab for passing **senior Java / backend software engineering interviews** — built on top of [Claude Code](https://claude.ai/code) and driven by a custom teaching system.

> **Goal:** arrive at any senior Java interview — OOD, system design, algorithms, code review, live pairing — and perform confidently under time pressure. Domain focus: fintech, trading OMS, banking, sport betting.

---

## What this is

Most interview prep is passive — read a book, watch a video. This lab is **active and adaptive**:

- **Claude picks questions** from a weighted category distribution (30% algo, 20% system design, 15% code review, 10% OOD, …)
- **You answer** in real Java code or Markdown, in your IDE, under realistic time pressure
- **Claude reviews** against a rubric and writes `review.md` — what you got right, what you missed, what breaks in production or at AWS scale
- **Difficulty auto-adjusts** — 2 consecutive passes bumps you up; 2 fails drops you down
- **Lessons generate on demand** — if a topic hasn't been taught yet, Claude generates a full HTML lesson + whiteboard before the question

It is not a framework or a library. It is a conversation with Claude Code where you type commands like `next`, `hint`, `done`, `review`.

---

## How it works with Claude Code

This project is designed to be opened in [Claude Code](https://claude.ai/code) (CLI or IDE extension). The `CLAUDE.md` file contains the full instruction set — question distribution, delivery conventions, rubrics, pairing-drill rules, lesson pipeline. Claude reads it at session start and operates as your exam coach.

```
# Start a session
claude                        # open Claude Code in this directory

next                          # Claude picks a question
next algo hard                # force category + difficulty
next pairing-drill senior     # live refactor roleplay with a senior peer persona
hint                          # get a nudge
done                          # submit — Claude writes review.md
progress                      # show your scoreboard
```

No setup beyond `claude` CLI and Java 21 / Maven for the coding modules.

---

## Teaching system — `/teach`

Before the first question on any new topic, Claude auto-generates a **self-contained HTML lesson** saved under `topics/{category}/{slug}/`. Lessons are:

- Linked to the question via `question.md`
- Stored permanently in git — build up a personal reference library over time
- Paired with **step-through whiteboard exports** (via [agent-whiteboard](https://github.com/bobpuley/agent-whiteboard) MCP) for visual concepts

You can also trigger lessons directly:

```
/teach explain CompletableFuture pipelines with thenApply vs thenCompose
/teach how does consistent hashing work in a distributed KV store
```

---

## Examples

### Example 1 — Algorithm question (`next algo medium`)

Claude picks a question, you implement in `MyAnswer.java`, then `done`:

```
> next algo medium

Q082 — Sliding Window Rate Limiter (Medium)
Implement a per-user rate limiter: allow at most N requests in a rolling T-second window.
Return true if the request is allowed, false otherwise.

Ideal time: 20 min
File: algorithms/src/main/java/.../q082_sliding_window_rate_limiter/MyAnswer.java
```

You implement it. Then:

```
> done

✅ Correctness: sliding window deque logic correct — evicts stale timestamps before counting.
✅ Time complexity: O(1) amortised per call.
⚠️  Missing: no thread safety — HashMap + ArrayDeque are not safe under concurrent access.
    Fix: ConcurrentHashMap + synchronized block per userId, or a per-key ReentrantLock.
⚠️  AWS/Distributed: in-memory per-instance state breaks on multi-pod ECS/EKS deployment.
    Fix: replace local map with Redis ZRANGEBYSCORE + ZADD + EXPIRE (atomic via Lua script).

Score: 3/5 — good core logic, missed concurrency + distributed failure mode.
```

---

### Example 2 — Code Review (`next code-review banking hard`)

Claude shows legacy Java with planted bugs. You find them in `MyAnswer.md`:

```java
// Planted snippet (excerpt) — find what's wrong

@Service
public class LoanDisbursementService {

    @Autowired
    private LoanRepository loanRepository;      // field injection

    public void disburse(String loanId, double amount) {   // double for money
        Loan loan = loanRepository.findById(loanId).get(); // .get() with no check
        loan.setStatus("DISBURSED");
        loan.setAmount(loan.getAmount() + amount);         // mutable posted entry
        loanRepository.save(loan);
        externalBankClient.transfer(amount);               // no @Transactional
    }
}
```

Your `MyAnswer.md` lists issues. Claude scores:

```
> done

Issues found: 4/6

✅ [Banking] double for monetary amount → BigDecimal (line 6) — precision loss, regulatory non-compliance
✅ [Spring]  field injection → constructor injection (line 3) — not testable, hides dependencies  
✅ [Spring]  missing @Transactional — DB save + external call not atomic; partial failure = inconsistent state
✅ [JPA]     .get() without isPresent() check → NoSuchElementException in production
❌ Missed: no audit log on balance-changing operation → SOX violation
❌ Missed: no timeout/circuit breaker on externalBankClient.transfer() → cascading failure on bank API slowdown
```

---

### Example 3 — Pairing Drill (`next pairing-drill junior`)

Roleplay: you mentor two junior devs refactoring live production code.

```
> next pairing-drill junior

[Emma 👩‍💻] Hey! So we've been asked to fix this BetPlacementService before the World Cup launch.
           I think the issue is in validateAndPlace()... but I'm not sure where to start?

[Noah 👨‍💻] I ran the tests — they pass! So maybe it's fine?

// You open answer/BetPlacementService.java and start refactoring...

> review

[Emma] Oh nice, you extracted the liability check into its own method! 
       But wait — shouldn't that counter be thread-safe? We get like 50k bets/sec during live matches.

[Noah] I mean... it's just an int, it'll be fine right?

// transcript.md updated — Claude reacts in character, notes what you caught vs missed

> done

review.md written:
✅ Technical communication: explained BigDecimal fix clearly without jargon
✅ Leading safely: didn't rewrite everything — guided Emma/Noah to the solution
⚠️  Missed: liability counter as int → non-atomic on concurrent requests → overselling exposure
⚠️  Missed: no idempotency key on bet placement → duplicate bet on client retry
```

---

## Domain focus — three industry verticals

Questions rotate across three real-world domains so every answer is grounded in production consequences, not toy examples:

| Vertical | What it probes |
|---|---|
| **Fintech / Payments** | Card auth flow, idempotency keys, PCI-DSS, race conditions on balance (`@Version`), fraud detection, outbox pattern |
| **Banking** | Double-entry ledger, `BigDecimal` for money, loan origination saga, KYC/AML, SWIFT/SEPA, SOX audit trail |
| **Sport Betting** | Real-time odds feed, liability management, bet settlement saga, in-play latency, responsible gambling compliance |

Code review questions in these domains always flag domain-specific landmines (e.g. `double` for odds → wrong payouts, missing idempotency key → duplicate bet on retry).

---

## Command Legend

| Command | Action |
|---|---|
| `next` | Claude picks a question (weighted random across all categories) |
| `next algo` | Force algorithms |
| `next system-design` | Force system design |
| `next code-review` | Force spot-the-issue — find bugs in legacy Java code |
| `next ood` | Force OO Design — full lesson + solution/ + empty answer/ |
| `next ood skeleton` | Same as `next ood` but answer/ pre-seeded with planted bugs |
| `next spring` | Force Spring / Spring Boot / Hibernate |
| `next messaging` | Force Kafka / Redis / RabbitMQ / WebSockets |
| `next nosql` | Force MongoDB / Elasticsearch / Neo4j |
| `next db` | Force databases / SQL |
| `next concepts` | Force DDD / EDA / Microservices / AWS / Docker / K8s |
| `next java` | Force Java / Java 8+ features |
| `next devops` | Force DevOps — rotates across 9 clusters |
| `next devops containers` | Dockerfile, multi-stage, docker-compose |
| `next devops kubernetes` | Pods, Deployments, Services, Ingress, HPA, probes |
| `next devops helm` | Chart structure, templating, rollback, canary |
| `next devops ci-cd` | GitLab CI/CD, ArgoCD, GitOps |
| `next devops observability` | Datadog APM, SLOs, dashboards, distributed tracing |
| `next devops alerting` | PagerDuty, Slack webhooks, runbooks |
| `next devops terraform` | Providers, modules, state, remote backend |
| `next devops agent-infra` | AI agents on K8s, LLM observability, model versioning |
| `next devops architecture` | Full end-to-end DevOps pipeline (always hard) |
| `next payments` | Payment/fintech domain: card auth, idempotency, PCI-DSS |
| `next banking` | Banking domain: double-entry ledger, KYC/AML, SWIFT/SEPA |
| `next betting` | Sport betting domain: odds feed, bet placement, settlement |
| `next easy` | Force easy difficulty |
| `next medium` | Force medium difficulty |
| `next hard` | Force hard difficulty |
| `next {category} {slug}` | Force category + topic hint, e.g. `next ood bank` |
| `next pairing-drill` | Live mentoring/refactor roleplay — explicit-invoke only |
| `next pairing-drill {level}` | `junior` / `mid` / `senior` / `pm` pairing partner |
| `hint` | Get a hint — saved to hints.md |
| `solution` | Show full solution — saved to hints.md |
| `review` | Pairing-drill only: repeatable check-in, appends to transcript.md |
| `done` | Submit answer — Claude reviews and writes review.md |
| `skip` | Skip question, logged in PROGRESS.md |
| `pause` | Save state to SESSION.md |
| `resume` | Restore from SESSION.md |
| `progress` | Show PROGRESS.md summary |
| `retry q{NNN}` | Retry a specific past question |

---

## Question Distribution

| Category | Weight | Technologies |
|---|---|---|
| Algo Coding | 30% | Data structures, graphs, DP, JUnit, EDA code exercises |
| System Design | 20% | Distributed systems, scalability, component design |
| Code Review / Spot the Issue | 15% | Legacy Java — find design flaws, concurrency bugs, anti-patterns |
| OO Design | 10% | SOLID, Strategy, Observer, Factory, Builder, State, Command |
| Java / Java 8+ | 8% | Streams, lambdas, Optional, CompletableFuture, Java 21 features |
| DevOps | 7% | 9 clusters — containers, K8s, Helm, CI-CD, observability, alerting, Terraform, agent-infra, architecture |
| Rest | 10% | Spring, Kafka, Redis, PostgreSQL, MongoDB, AWS, DDD, EDA, gRPC |

`pairing-drill` is explicit-invoke only — not in the weighted rotation.

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

## Folder Structure

```
algorithms/src/main/java/com/interview/algorithms/q{NNN}_{slug}/
├── question.md
├── Solution.java       ← reference only
└── MyAnswer.java       ← you write here

algorithms/src/test/java/com/interview/algorithms/q{NNN}_{slug}/
└── MyAnswerTest.java

ood/src/main/java/com/interview/ood/q{NNN}_{slug}/
├── solution/           ← complete reference implementation
└── answer/             ← you write here

pairing-drill/src/main/java/com/interview/pairingdrill/q{NNN}_{slug}/
├── {DomainClass}.java  ← seeded messy code (read-only reference)
├── solution/           ← complete reference fix
├── answer/             ← you work here
└── transcript.md       ← appended on every `review`

system-design/q{NNN}_{slug}/
├── question.md
├── hints.md
└── MyAnswer.md

code-review/q{NNN}_{slug}/
├── question.md
├── hints.md
└── MyAnswer.md
```

---

## Pairing Drill Questions

Live mentoring/refactor roleplay — real compiled Java, two junior personas, iterative `review` / `done` cycle.  
Invoke with `next pairing-drill` (never in the weighted `next` rotation).

| # | Slug | Flavor | Domain |
|---|---|---|---|
| q072 | Customer Membership Pricing | Service smells — duplication, magic numbers, encapsulation leaks | General SaaS |
| q073 | Order Fulfillment | Correctness-in-disguise — partial-failure ordering, swallowed exception | E-commerce |
| q074 | Bet Placement | OOD structural + Concurrency — State pattern, missing synchronization | Sport Betting |
| q075 | Transaction Reconciliation | Java SDK / functional idioms — streams, `Optional`, `CompletableFuture` | Fintech |
| q076 | Loan Disbursement | Spring-specific — field injection, `@Transactional` boundary, missing circuit breaker | Banking |
| q077 | Market Settlement | Type safety / primitive obsession — `double` → `BigDecimal`, String status → enum | Sport Betting |
| q078 | Payment Search | Search / filtering — `Specification<T>`, `Predicate` composition, unbounded scan | Fintech |
| q079 | Account Statement | Code-review hybrid — spot bugs + structural refactor | Banking |
| q080 | Live Odds Service | Concurrency / synchronization — race conditions, non-atomic check-then-act | Sport Betting |
| q081 | Invoice Billing Service | Type safety + Service smells — primitive obsession, missing `@Transactional` | Billing |

Each question ships with:
- Seeded messy `{Domain}.java` (read-only reference)
- `solution/` — complete heavily-commented fix (pre-seeded from day one)
- `answer/` — where you actually work
- `transcript.md` — appended on every `review`

---

## MCP Integration — agent-whiteboard

This repo ships with `.mcp.json` wiring Claude Code to [agent-whiteboard](https://github.com/bobpuley/agent-whiteboard) — a local MCP server that renders step-through whiteboards live in your browser.

Used to generate all visual lessons under `topics/` (whiteboard HTML exports committed alongside each lesson). To use:

```bash
# Clone and build agent-whiteboard
git clone https://github.com/bobpuley/agent-whiteboard.git
cd agent-whiteboard && npm install && npm run build && npm link

# Start the server (port 3000)
agent-whiteboard
```

Then open Claude Code in this repo — `.mcp.json` auto-connects the `agent-whiteboard` MCP server.

---

## Stack

Java 21 · Spring Boot 3 · Kafka · Redis · PostgreSQL · MongoDB · AWS · Docker · JUnit 5 · Mockito

---

## Testing Convention

JUnit 5 + Mockito only — no AssertJ for new tests. `BigDecimal` comparisons via `compareTo`, not `assertEquals`.
