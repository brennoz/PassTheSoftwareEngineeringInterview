# PassTheSoftwareEngineeringInterview

Exam simulation lab for senior Java / backend engineers. Claude picks questions across categories, you answer, Claude reviews with a rubric and writes `review.md`.

Different from a deep-build lab — this is **breadth + time pressure + feedback loop**.

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
