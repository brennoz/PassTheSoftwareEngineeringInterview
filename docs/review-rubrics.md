## Review Rubrics

### Algorithms
- Correctness: does it produce right output?
- Time complexity: optimal or acceptable?
- Space complexity: any obvious waste?
- Edge cases: null, empty, duplicates, overflow handled?

### System Design
- Component selection: right tools for the job?
- Scalability reasoning: bottlenecks identified?
- Trade-offs articulated: consistency vs availability, etc.?
- Data model: storage decisions justified?
- **AWS mapping:** every component mapped to AWS equivalent (e.g. Kafka → MSK, Redis → ElastiCache, Postgres → RDS Multi-AZ, API Gateway → API GW + Lambda or ALB)
- **Distributed failure modes:** what breaks at 10x load, in a multi-AZ deployment, or during a region failover?
- **If payment domain:** idempotency addressed? PCI-DSS data handling? Multi-region RTO/RPO stated? Race conditions on balance? Outbox pattern for event publishing?

### Spring / Coding Exercises
- Correct patterns used (e.g. @Transactional, @Service boundaries)?
- Testable design (dependencies injectable)?
- Idiomatic Java 21?
- Spring Boot conventions followed?

### Code Review / Spot the Issue

Issues are scored by category. For each issue found:
- **Spring anti-patterns:** mutable state in `@Controller`/`@Service`, `@Autowired` field injection (not constructor), missing `@Transactional`, wrong propagation, lazy-load outside transaction
- **Distributed system pitfalls:** shared mutable state assumes single instance, in-memory cache not invalidated across nodes, no idempotency, missing retry/timeout, assuming local file system
- **Concurrency bugs:** non-thread-safe collections (`HashMap` in shared context), missing `synchronized`/`volatile`, double-checked locking without `volatile`, `SimpleDateFormat` shared instance
- **Algorithm / complexity:** O(N²) where O(N log N) possible, unnecessary DB query in loop (N+1), sorting unsorted input assumptions
- **JPA / Hibernate:** N+1 select, `EAGER` fetch on large collections, entity exposed directly as DTO, missing `@Version` for optimistic lock, transaction boundary too wide or too narrow
- **Security:** SQL string concatenation (injection), secrets in code, missing input validation, `@PreAuthorize` missing
- **Memory / resource leaks:** streams not closed, connection not released, large collection held in static field
- **Java style:** raw types, unchecked casts, empty catch blocks swallowing exceptions, `System.out.println` instead of logger

Strong answer: finds ALL planted issues, names the category, explains the real-world consequence, proposes fix, and where relevant explains **AWS/distributed impact** (e.g. "on a 4-pod ECS cluster this cache is per-instance → use ElastiCache Redis instead").

**Domain-specific code review triggers (always flag these):**

Payment / Fintech:
- Card/PAN data in logs → PCI-DSS violation, instant fail in compliance audit
- No idempotency key on payment mutation → double charge on retry
- No optimistic lock (`@Version`) on account balance → overdraft race condition
- `synchronized` on instance method when service runs as multiple replicas → false sense of safety
- Hardcoded AWS region or endpoint → breaks multi-region deployment
- Secrets/API keys in code or properties files → use AWS Secrets Manager / KMS
- No `@Transactional` on multi-step payment operations → partial failure = data inconsistency

Banking:
- `double`/`float` for monetary amounts → precision loss, regulatory non-compliance → use `BigDecimal`
- Mutable/UPDATE on posted ledger entries → audit trail destroyed → append-only ledger rows
- Missing audit log on balance-changing operation → SOX/regulatory violation
- No timeout + circuit breaker on external credit bureau call → cascading failure
- Loan/account state machine with no guard → invalid state transitions (e.g. approved → pending)

Sport Betting:
- `double`/`float` for odds or payout → wrong payouts, regulatory liability → use `BigDecimal`
- No idempotency key on bet placement → duplicate bets on client retry → duplicate stake deduction
- Liability counter in application memory → wrong on multi-pod → use Redis INCR (atomic)
- Self-exclusion / deposit limit check missing or checked after wallet debit → regulatory violation
- No market suspension during live event update → bets accepted on stale odds → financial loss

### OO Design
- SOLID principles applied?
- Correct pattern identified and named (e.g. Strategy, Observer)?
- Classes have single responsibility, low coupling?
- Skeleton compiles, interfaces/abstractions make sense?
- Extensibility: adding new behaviour doesn't require modifying existing classes?

### Java / Java 8
- Java 8 APIs used correctly (streams, Optional, CompletableFuture)?
- No unnecessary boxing/unboxing, no raw types?
- Thread safety addressed where relevant?
- Idiomatic use of lambdas and method references?

### DevOps / Docker / Kubernetes / GitLab / Terraform / Observability

**Hard gate — must pass:**
- **Prod awareness:** failure modes identified, not just happy path. Examples that must be caught:
  - containers: no .dockerignore → secrets in image layer; `latest` tag → non-reproducible deploys; running as root → container escape risk
  - kubernetes: missing resource limits → OOMKilled / noisy neighbour; missing probes → traffic routed to crashed pod
  - helm: no rollback strategy → failed deploy is stuck; hardcoded values in templates → not environment-portable
  - ci-cd: secrets hardcoded in `.gitlab-ci.yml` → exposed in CI logs; no cache → rebuild from scratch every run
  - observability: no SLO defined → alert fires but breach not tracked; missing distributed trace → can't pinpoint slow service
  - alerting: no runbook linked from alert → on-call engineer blind; PagerDuty without escalation policy → alert silently drops
  - terraform: state not in remote backend → corruption on team usage; no state locking → concurrent apply race condition
  - agent-infra: no rate-limit sidecar → LLM API costs unbounded; model version not pinned → silent behaviour change on redeploy

**Required for pass:**
- Architecture justification: WHY this config/tool choice, not just what it is

**Bonus (not required to pass):**
- Correct YAML/HCL/Dockerfile syntax
- Agentic AI angle mentioned
- AWS equivalent called out (EKS, ECR, MSK, ElastiCache, Secrets Manager, Bedrock)
- Cost / operational tradeoff named

**Difficulty scope:**
- Easy: single tool in isolation (Dockerfile, one K8s resource, one Terraform resource)
- Medium: two tools wired together (CI pipeline + Docker push; K8s Deployment + HPA + ConfigMap)
- Hard: full subsystem (K8s prod setup with Ingress + TLS + Secrets + HPA + probes; full CI/CD pipeline)
- Architecture (always hard): complete DevOps pipeline — git push → GitLab CI → Docker → ECR → Helm → ArgoCD → EKS → Datadog → Slack → Terraform underneath

### Messaging / NoSQL / DB
- Technology chosen correctly for the use case?
- Configuration/schema correct?
- Failure scenarios considered?

### Concepts / DDD / EDA
- Domain language precise?
- Boundaries correctly identified?
- Patterns named and applied correctly?
