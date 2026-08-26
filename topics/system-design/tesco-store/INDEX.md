# tesco-store — Lesson Index

Prep for Tesco's System Design round: 90 minutes, live, using Draw.io, given an unknown
problem statement, with a guaranteed CI/CD follow-up. Two tiers — Tier 1 is the transferable
method (works for whatever prompt actually comes up), Tier 2 is a worked example using the
real hints from the 2nd interview (see the recall callout in file 0003) as flavor for a
hypothetical Tesco store system, not a claim about Tesco's actual architecture.

| File | Tier | Concepts covered |
|---|---|---|
| [0001-90min-drawio-method.html](0001-90min-drawio-method.html) | 1 — Method | Verbatim interview-invite quote, 90-min time budget, clarifying-question framework (4 categories), draw.io shape vocabulary, generic starting skeleton, what "good" looks like vs common failure modes |
| [0002-cicd-template.html](0002-cicd-template.html) | 1 — Method | Generic CI/CD pipeline skeleton, Azure service mapping (ACR, AKS, Key Vault, Azure Monitor), environments/promotion, fleet-rollout problem (push vs pull/GitOps at thousands-of-clusters scale), rollback strategy, 90-second verbal answer template |
| [0003-system-overview.html](0003-system-overview.html) | 2 — Worked example | Recalled-hints callout, "why local compute" 3-part argument, full bounded-context SVG diagram (store-edge + central + external gateway), service responsibility table, the WAN-independence boundary rule |
| [0004-store-edge-architecture.html](0004-store-edge-architecture.html) | 2 | K3s vs Azure IoT Edge vs full AKS-at-edge comparison, Azure Arc fleet management, edge stack SVG diagram (hardware→OS→K3s→pods→storage), single-node vs redundant-node trade-off, resource discipline |
| [0005-websockets-deep-dive.html](0005-websockets-deep-dive.html) | 2 | WS vs REST-polling vs SSE justification, why this ISN'T the millions-of-connections scaling problem, connection lifecycle (auth, heartbeat, reconnect, session resume), Spring STOMP-over-WebSocket, message design, server-side basket-state rationale |
| [0006-microservice-boundaries.html](0006-microservice-boundaries.html) | 2 | Bounded-context boundary test, layered Spring Boot service shape, pragmatic single-Postgres-instance/per-service-schema data ownership at edge scale, inter-service REST calls, why not split further/merge, central vs edge service differences |
| [0007-store-central-sync.html](0007-store-central-sync.html) | 2 | Single-writer-per-direction consistency model, outbox pattern (with diagram), idempotency keys, Azure Service Bus vs Event Hubs, local-vs-central stock-accuracy nuance |
| [0008-k8s-azure-topology.html](0008-k8s-azure-topology.html) | 2 | Central AKS topology table, fleet-topology SVG diagram (Arc control plane + 3-ring GitOps rollout), store connectivity options (ExpressRoute vs SD-WAN/VPN) |
| [0009-payment-pci-dss.html](0009-payment-pci-dss.html) | 2 | PCI-DSS scope-minimisation principle, tokenisation/P2PE payment flow diagram, network isolation (NetworkPolicy, VLAN), logging discipline, honest limitation on offline card authorization |
| [0010-failure-modes-resilience.html](0010-failure-modes-resilience.html) | 2 | Full blast-radius table across 6 failure types, WAN-reconnect sequence diagram, zero-touch edge-box replacement via GitOps, circuit breakers on intra-cluster calls |
| [0011-scale-capacity-estimation.html](0011-scale-capacity-estimation.html) | 2 | Stated assumptions (stores, terminals, tx/day, peak multiplier), worked back-of-envelope maths, table connecting each derived number to a specific earlier design decision |
| [0012-mock-walkthrough.html](0012-mock-walkthrough.html) | Rehearsal | Full timed 90-minute script (invented practice prompt) applying file 0001's method end-to-end: clarifying Q&A, spoken estimation, narrated diagram build (+ SVG), two interviewer-driven deep dives, CI/CD answer, wrap-up, "if the real prompt differs" transfer table, final self-check |

## Design notes

- **Confidence convention**: any file using a real recalled interview hint opens with a
  purple-bordered "What Tesco actually said" callout, kept separate from the surrounding
  generic system-design content — see file 0003 for the full statement of what was actually
  recalled (in-store machine near cashier terminals, heavy WebSocket use, Docker/K8s/Azure,
  Spring Boot microservices). Everything else is a plausible design consistent with those
  hints, not a claim about Tesco's real system.
- No quizzes — this is methodology + rehearsal content, not retrieval-practice content.
- SVG diagrams (not ASCII) in files 0003, 0004, 0008, 0012 — the real interview tool is
  Draw.io, so these give an actual visual reference, not just prose, for what a clean
  box-and-arrow diagram for this domain looks like.
