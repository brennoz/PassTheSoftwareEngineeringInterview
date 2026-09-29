# bettingco — Lesson Index

| File | Concepts covered |
|---|---|
| 0001-architecture-overview.html | System map, bounded contexts, Kafka vs RabbitMQ vs Redis WHY, AWS topology |
| 0002-feed-handlers.html | Feed Handler pattern, lib-feed-handlers FeedRaceBroadcaster + MessageBuilder, 18 integrations, Kafka bridge |
| 0003-event-service.html | PostgreSQL schema, EventStatus enum, Quartz scheduler, Kafka topic ownership, REST controllers, standard layered arch |
| 0004-bet-manager-service.html | Bet placement lifecycle, Spring Retry, Saga choreography, idempotency, multi-tenancy |
| 0005-pricing-service.html | Redis caching strategy, TTL design, odds calculation, consumer group partitioning |
| 0006-sportsbook-service.html | STOMP hub, RabbitMQ relay, Redis fan-out, Kafka 5-topic fan-in, WebSocket config |
| 0007-bet-lifecycle.html | End-to-end bet flow: placement → advisory → settlement → STOMP push |
| 0008-kafka-topics.html | Full topic table, WOODS vs BETTINGCO clusters, consumer group IDs, at-least-once delivery |
| 0009-websocket-stomp.html | STOMP protocol, RabbitMQ relay port 61613, SockJS fallback chain, frontend auth |
| 0010-infrastructure.html | EKS clusters, MSK, Redis cluster, Helm HPA, GitLab CI/CD stages, ECR, Terraform |
| 0011-ood-patterns.html | Standard layered arch, lib-feed-handlers composition, Saga, CQRS, TenantService + Redis cache, self-injection @Cacheable |
| 0012-sdlc-security-observability.html | Architecture decisions, coding standards, testing strategy, deployment, JWT filter (real code), Datadog→Slack, Danger→Slack |
| 0013-quiz-master.html | 25-question comprehensive quiz, all topics, click-to-reveal |
| 0014-frontend.html | cerberus-ui (Next.js 15, Jotai, TanStack Query, STOMP), sportsbook (React 16, Redux), optimistic cache updates, SockJS, JWT in cookie |
| 0015-diagram-platform.html | **SVG diagram** — full platform: 6 bounded contexts, all services, Kafka clusters, frontends, shared infra, design decision table |
| 0016-diagram-kafka.html | **SVG diagram** — Kafka data flow: WOODS vs BETTINGCO MSK, all 13 topics, producers, consumers, consumer group IDs, at-least-once |
| 0017-diagram-bet-lifecycle.html | **SVG diagram** — bet lifecycle sequence: 21-step flow from browser POST to STOMP push across 6 services, 4 phases |
| 0018-diagram-infrastructure.html | **SVG diagram** — K8s/AWS topology: 2 EKS clusters, HPA, GitLab CI stages, MSK, Redis cluster, RabbitMQ, Terraform IaC, production debugging |
| 0019-spring-boot-internals.html | **Gap topic** — Spring Boot autoconfiguration, @Conditional, bean lifecycle, @Cacheable self-injection, JWT filter, @Transactional propagation, Actuator |
| 0020-sql-performance.html | **Gap topic** — EXPLAIN ANALYZE, index types (B-tree, partial, composite, covering), N+1, HikariCP pool sizing, ARM actual DDL |
| 0021-transaction-isolation.html | **Gap topic** — 4 isolation levels, dirty/non-repeatable/phantom reads, @Transactional isolation, @Version optimistic lock, SELECT FOR UPDATE, idempotency_key strategy |
| 0022-k8s-delivery.html | **Gap topic** — Rolling update mechanics, liveness vs readiness vs startup probes, HPA, PodDisruptionBudget, kubectl debugging commands, Helm upgrade |
| 0023-api-evolution.html | **Gap topic** — REST versioning strategies, breaking vs safe changes, Deprecation/Sunset headers, Kafka tolerant reader, strangler fig pattern |
| 0024-production-debugging.html | **Gap topic** — 5 real incident scenarios: slow query, Kafka lag, 500 errors, Redis connection exhaustion, distributed trace; full command reference |
| 0025-agile-ownership.html | **Gap topic** — Senior vs junior ownership, Definition of Done (payment platform), tech debt classification, DORA metrics, PR review depth, incident ownership, ticket refinement |
| 0026-opayo-payment-framing.html | **Interview framing** — ARM→Opayo pattern transfer map, PCI-DSS requirements, card authorisation flow, exactly-once settlement, interview answer templates, 4 Opayo-domain quiz questions |
| 0027-behavioural.html | **Interview prep** — 10 behavioural questions: STAR skeleton + click-to-reveal model answer + key phrases; tell me about yourself, technical challenge, disagreement, influence, failure, deadline, ambiguity, process improvement, code quality |
| 0028-project-narrative.html | **Interview prep** — Timed 3-minute project pitch: 4 timed blocks (intro/architecture/coding+testing/deployment), key phrases per block, 6 follow-up probing questions with answers, timing checklist |
| 0029-security-deep-dive.html | **Interview prep** — Functional security (JWT filter, tenant isolation, RBAC, HTTPS, secrets) + Non-functional security (no PII in logs, pod hardening, network policies, rate limiting, dependency scanning); honest gap assessment |
| 0030-nfr-deep-dive.html | **Interview prep** — 5 NFR pillars with ARM numbers + Opayo equivalents: performance (sub-100ms), scalability (HPA + Kafka partitions), availability (multi-AZ + PDB), resilience (circuit breaker + DLQ), observability (Datadog APM + lag alerts) |
| 0031-informal-interview-story-bank.html | **Interview prep** — informal 30-min get-to-know-you conversation, 3 flagship stories grounded in real arm-backend source (composite local cache + Kafka invalidation; SlipService idempotency race vs. CustomerService's existing Redisson-lock fix pattern; 18-feed settlement saga with SettlementEngine BOG/deductions/each-way) mapped across 6 question areas incl. sports/betting/probability genuine-interest framing |
| 0032-low-latency-high-throughput-synthesis.html | **Interview prep** — synthesis across all services: event-ID Kafka keying for ordering (128-partition bet-builder outlier), acks=1 producer tradeoff, per-feed consumer batching, Betfair squashing listener, RxJava backpressure in pricing-service, Caffeine+Redis near-cache with Kafka invalidation, Redis liability hash, virtual threads vs fixed pools, container-aware JVM sizing + AppCDS, hot-path pinned to 1 replica in prod (verified), HikariCP/Hibernate batch tuning, domain-level circuit-breaker substitutes, honest gaps (no resilience4j, no schema registry, no read replicas) |
