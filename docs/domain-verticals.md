## Industry Domain Focus

Questions should rotate across three verticals. When relevant, mention that a topic maps to real industry interview patterns (without naming specific companies). Pick the domain that best fits the question — rotate across all three over time.

---

### Vertical 1 — Payment / Fintech

| Topic | What they probe |
|---|---|
| High-throughput systems | Millions of card transactions/sec — sharding, partitioning, async processing |
| PCI-DSS compliance | Card data (PAN) never in logs, encrypted at rest (AWS KMS), tokenisation |
| Idempotency | Duplicate requests must not double-charge — idempotency keys, DB conditional writes |
| Race conditions on balance | Concurrent debit on same account → overdraft — `@Version` / optimistic lock / DynamoDB conditional write |
| Fraud detection | Real-time anomaly detection, sliding window counters, feature stores, rule engines |
| Card authorization flow | Issuer → Network → Acquirer, timeout handling, fallback, reversals |
| Multi-region failover | Active-active vs active-passive, RTO/RPO, Route 53 health checks, Aurora Global |
| Rate limiting | Token bucket / sliding window per merchant/card, Redis atomics (INCR + EXPIRE) |
| Outbox pattern | Atomic DB write + event publish — prevents ghost payments on SNS/Kafka failure |
| Exactly-once semantics | Kafka idempotent producer + transactions, consumer deduplication |
| Legacy system migration | Strangler fig pattern, shadow mode validation, event-driven decoupling |
| Observability | Prometheus metrics, Splunk/CloudWatch logs, OpenTelemetry distributed tracing |
| mTLS / security | Service-to-service auth, secrets via AWS Secrets Manager, JWT/OAuth2 |
| Schema evolution | Avro + Schema Registry, backward/forward compatibility, no breaking changes |
| Canary / blue-green deploy | Feature flags, gradual rollout, instant rollback for payment services |

---

### Vertical 2 — Banking

| Topic | What they probe |
|---|---|
| Double-entry ledger | Every debit has a matching credit — immutable append-only ledger, no UPDATE on posted entries |
| Interest calculation | Daily accrual vs compound, `BigDecimal` precision (never `double` for money), end-of-day batch jobs |
| Loan origination pipeline | Credit score → underwriting → approval → disbursement as a saga; rollback on rejection |
| KYC / AML compliance | Identity verification pipeline, watchlist screening (OFAC/PEP), suspicious activity reports (SARs) |
| SWIFT / SEPA / ACH | Message formats (ISO 20022), settlement windows (T+1/T+2), retry on NACK, correspondent banking |
| Regulatory reporting | End-of-day reconciliation, Basel III capital ratios, batch aggregation, audit trail immutability |
| Core banking migration | Strangler fig + dual-write to old and new system; shadow mode validation before cutover |
| Overdraft / credit limit | Optimistic lock (`@Version`) + compensating transaction if limit exceeded post-commit |
| Data retention / GDPR | Right to erasure vs immutable audit trail conflict — pseudonymisation strategy |
| SOX compliance | No production access without audit log, separation of duties, 4-eyes approval for config changes |

**Code review triggers (always flag):**
- `double` or `float` for monetary amounts → use `BigDecimal`
- Mutable state in posted ledger entries → ledger rows must be append-only
- Missing audit log on any balance-changing operation
- Synchronous call to external credit bureau without timeout + circuit breaker

---

### Vertical 3 — Sport Betting

| Topic | What they probe |
|---|---|
| Real-time odds feed | WebSocket fan-out to millions of clients; Redis pub/sub for odds updates; back-pressure handling |
| Bet placement idempotency | Network retry → duplicate bet → double stake deduction — idempotency key per bet slip |
| Liability management | Max exposure per outcome tracked in Redis; suspend market when threshold hit; atomic INCR check |
| In-play low latency | Sub-100ms bet acceptance during live events; in-memory state + async DB write (write-behind cache) |
| Bet settlement | Outcome event triggers settlement saga: mark bets won/lost → credit winnings → publish receipt |
| Odds calculation | Decimal/fractional/American formats; overround (margin) ensures book profit; `BigDecimal` for payout |
| Race conditions on wallet | Concurrent bet placements deplete same wallet → overdraft — optimistic lock or Redis DECR atomic |
| Responsible gambling | Deposit limits, loss limits, self-exclusion (GamStop UK), cooling-off — enforced pre-bet, not post |
| Anti-fraud / bonus abuse | Matched betting detection (correlated accounts, IP/device fingerprint), velocity checks |
| Market suspension | Event triggers (red card, goal) → suspend all markets atomically → recalculate odds → reopen |
| KYC / AML | Mandatory in regulated markets (UK GC, MGA); verify identity before first withdrawal |
| High-traffic spikes | Super Bowl / World Cup — 100× normal load in minutes; auto-scaling, pre-warmed Lambda/ECS |
| Regulatory audit trail | Every bet, odds change, settlement immutably logged — WORM storage (S3 Object Lock) |

**Code review triggers (always flag):**
- `double`/`float` for odds or payout calculation → `BigDecimal`
- No idempotency key on bet placement endpoint → duplicate bets on retry
- Liability counter in application memory → wrong on multi-pod deployment → use Redis INCR
- Self-exclusion check missing or checked after wallet debit → regulatory violation
- No market suspension lock → bets accepted on stale odds during live event
