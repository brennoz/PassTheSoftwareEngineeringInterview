# q041 — Full DevOps Pipeline: bet-settlement-service

**Lessons:** [0001 Full DevOps Pipeline End-to-End](../../topics/devops/architecture/0001-full-pipeline.html)

**Category:** devops · **Cluster:** architecture (always hard — bypasses difficulty tracker) · **Domain:** Sport Betting
**Ideal time:** 45 minutes

---

## Scenario

You own the deployment pipeline for `bet-settlement-service` — the service that marks bets won/lost and credits winnings the moment a market resolves. It runs on Amazon EKS. It is liability-critical: every minute of degraded capacity during a live match is real financial exposure (stale odds being honoured, delayed payouts, potential double-settlement on retried events).

The org's standard stack: **GitLab CI → Docker → ECR → Helm → ArgoCD (GitOps) → EKS → Datadog → Slack/PagerDuty**, with **Terraform** provisioning everything underneath (VPC, EKS cluster, ECR, IAM roles, and the ArgoCD/Datadog installs themselves).

## Question

Design and explain the full pipeline for a commit landing on `main` in `bet-settlement-service`, from `git push` to a running, observed pod. Your answer must cover all of the following:

1. **Full pipeline diagram** — every stage in order, from `git push` to a serving pod, naming the tool responsible for each hop. Be explicit about which tool triggers which — don't collapse "CI does everything."
2. **Push vs pull deploy model** — where exactly does the push-based part (GitLab CI) end and the pull-based part (ArgoCD/GitOps) begin? Why does that boundary matter for credential blast radius?
3. **Rollout safety for a liability-critical service** — a bad ConfigMap value causes the new version to crash-loop mid-rollout, during a live match. Walk through what Kubernetes readiness probes alone catch, what they miss, and what additional mechanism(s) prevent a silent partial-capacity outage. Name the specific Deployment/Rollout field(s) involved.
4. **Terraform's boundary** — what does Terraform own vs. what does ArgoCD own? What breaks if that boundary is violated (e.g. someone hand-edits a node group via the console)? What specifically prevents two engineers' concurrent `terraform apply` from corrupting shared state?
5. **Observability → alerting loop** — define one SLO for this service and describe how a breach reaches a human (Datadog → alert routing → on-call). What must the alert include to be actionable (link to the failure-mode discussion in item 3)?
6. **One end-to-end distributed failure mode** — pick a single failure that can only be understood by tracing across two or more of these tools (not a single-tool issue). Explain the failure and the cross-tool fix.

## Constraints

- No running code required — this is an architecture/design question, answered in prose + diagram (ASCII is fine)
- Every component must be named specifically (not "the CI tool" — say GitLab CI; not "the registry" — say ECR)
- Tie your rollout-safety answer (item 3) to the betting domain's liability sensitivity — don't answer generically
- Answer in `MyAnswer.md`

## Rubric — Pass requires

| Item | Must cover |
|---|---|
| Pipeline diagram | All 9 stages present and correctly ordered: test → build → docker build → push → manifest update → ArgoCD sync → EKS rollout → Datadog observe → alert |
| Push vs pull boundary | Correctly states GitLab CI stops at pushing an image + a Git commit; ArgoCD is the only thing with cluster credentials; explains blast-radius reasoning |
| Rollout safety | Names `progressDeadlineSeconds` (or equivalent) for stuck-rollout detection AND a canary/progressive-delivery mechanism (Argo Rollouts or Helm+manual traffic split) — readiness probes alone is not a sufficient answer |
| Terraform boundary | States Terraform owns cluster/infra, ArgoCD owns application manifests; names remote state (S3) + locking (DynamoDB) as the concurrency-safety mechanism |
| Observability loop | Defines a concrete SLO (latency or success-rate threshold with a number); alert routes to PagerDuty/Slack; alert content includes enough context to act (not just "error rate high") |
| Cross-tool failure | Failure genuinely spans ≥2 tools (e.g. ArgoCD sync + K8s readiness + Datadog lag combining into a silent partial outage) — a single-tool bug (e.g. "Dockerfile runs as root") does not satisfy this item |

## Tips

- This question rewards naming the *seam* between tools, not depth on any one tool — that's what "architecture" tests.
- "GitOps" means Git is the source of truth and a controller inside the cluster pulls — say this explicitly somewhere.
- For item 3, remember: readiness probes control traffic *to* a pod, they don't stop a rollout from getting stuck at 50% forever.
- For item 6, a good failure trace usually starts with "ArgoCD says Synced/Healthy, but..." — cluster-reported health and real user-facing health can diverge.

---

## Answer format

Write `MyAnswer.md` with a diagram/ASCII pipeline first, then answer each of the 6 numbered points in its own subsection. Keep prose tight — this is a 45-minute question, not an essay.
