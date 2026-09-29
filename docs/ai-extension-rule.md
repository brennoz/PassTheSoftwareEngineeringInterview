## AI Engineering Extension Rule

**Applies to lessons only** (not `question.md`). Classic implementation always comes first. AI section always last, after the quiz, visually separated.

### When to add it
Add an AI Engineering Extension section to a lesson when the topic matches **any** of these triggers:

| Trigger | Example domains |
|---|---|
| Content moderation / spam detection | Comment system, social feed, chat |
| Fraud / anomaly detection | Payments, banking, betting wallet |
| Search / recommendation / ranking | Feed ranking, product search, autocomplete |
| Classification (intent, sentiment, category) | Support tickets, transaction tagging, bet type |
| Content generation / summarisation | News feed, document processing, reports |
| Document processing / extraction | KYC, loan origination, invoice parsing |
| Chatbot / conversational interface | Customer support, banking assistant |
| Personalisation / user behaviour | Feed ranking, notification timing, odds display |

### Section template (add to lesson HTML after quiz)

```html
<!-- AI Engineering Extension -->
<h2 style="border-color: #7c3aed">🤖 AI Engineering Extension</h2>

<h3>Where AI fits</h3>
<!-- which component benefits from an LLM -->

<h3>Pattern</h3>
<!-- RAG / classifier / agent / embedding search / tool-use / summarisation / extraction -->

<h3>Architecture delta</h3>
<!-- new boxes: vector DB, embedding service, LLM API call, prompt cache, guardrails -->

<h3>Tradeoffs</h3>
<!-- latency cost · hallucination risk vs rule-based · cost per call · accuracy · cold start -->

<h3>AWS / managed services</h3>
<!-- Bedrock · OpenSearch vector index · SageMaker · Kendra · Comprehend -->

<!-- AI Quiz — one question, labelled AI-Q, same format as main quiz -->
<div class="question" id="ai-q1">
  <p>AI-Q: ...</p>
  <ul class="options" data-correct="N"> ... </ul>
  <div class="explanation"> ... </div>
</div>
```

### DevOps AI Angle section template (add to devops lessons after quiz)

```html
<!-- DevOps Agentic AI Angle -->
<h2 style="border-color: #7c3aed">🤖 Agentic AI Angle</h2>

<h3>AI as workload</h3>
<!-- How this tool/config changes when the service being deployed IS an AI agent:
     GPU node pools, long-running inference pods, token-rate sidecars, model version pinning,
     prompt config via ConfigMap, cost-per-call metrics in Datadog -->

<h3>AI as operator</h3>
<!-- How AI agents USE or AUTOMATE this tool in the DevOps pipeline:
     Claude in GitLab CI as automated code reviewer, AI-powered incident triage,
     LLM generating Terraform plans from natural language, AI reading Datadog anomalies
     and opening Jira tickets automatically -->

<h3>Key differences from a standard service</h3>
<!-- GPU requirements, non-deterministic outputs (can't assert exact response in tests),
     model version = dependency (pin like a library version), token cost = runtime cost metric -->

<h3>AWS managed services</h3>
<!-- Bedrock (managed inference), SageMaker (custom model hosting),
     SSM Parameter Store (prompt versioning), Cost Explorer (token cost tracking) -->
```

### Section styling
Use a **purple left border** (`border-color: #7c3aed`) on the `h2` to visually distinguish from the main lesson content.
