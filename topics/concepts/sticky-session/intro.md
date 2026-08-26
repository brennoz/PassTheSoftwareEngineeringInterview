# Sticky Session

## What it is
A load balancer configuration that routes all requests from the same client to the **same backend server** for the duration of a session. Required when server-side state is stored **in-process** (HTTP session in memory, WebSocket connection, in-progress file upload). Default LB behaviour is round-robin or random → requests land on different servers → each server has no memory of the previous request.

---

## How it works

### Cookie-based (most common — AWS ALB default)
```
1. Client sends first request → ALB routes to Server A
2. ALB sets cookie:  AWSALB=abc123; Path=/; Max-Age=86400
3. Client sends all subsequent requests with that cookie
4. ALB reads AWSALB cookie → always routes to Server A
```

### IP Hash
LB hashes client IP → deterministic server selection. Simple, no cookie needed.
Problem: clients behind NAT (corporate proxy, mobile carrier) → same IP → all land on same server → uneven load.

### Consistent Hashing
Same ring-hash used for distributed caches. Client ID → hash → nearest server on ring. Survives server add/remove better than IP hash.

---

## The Core Problem

```
Without sticky session:         With sticky session:
                                
User → LB → Server A            User → LB → Server A  (always)
User → LB → Server B  ← no      User → LB → Server A  (always)
             session!            User → LB → Server A  (always)
```

Stateful data lives in Server A's memory. Server B knows nothing about this user → 401, lost cart, broken upload.

---

## Why Sticky Sessions are a Code Smell

Sticky sessions solve a symptom (state in memory) not the root cause. Problems they introduce:

| Problem | Explanation |
|---|---|
| **Defeats horizontal scaling** | 10 servers but all traffic for user X goes to Server A → Server A overloaded, others idle |
| **No failover** | Server A crashes → session lost → user logged out, lost cart, broken payment |
| **Uneven load** | "Hot" users generate more requests than average → their server is overloaded |
| **Stateful pods in K8s** | Pod restarts are normal → sticky breaks constantly |

**Correct fix: externalise state.**

---

## The Right Solution — Stateless Services

```
Before (sticky required):                After (no sticky needed):
                                         
User → LB → Server A                    User → LB → Server A
             ↓                                        ↓
         [session in memory]                    [Redis: session store]
                                         
User → LB → Server B  ← broken!         User → LB → Server B
             ↓                                        ↓
         [no session]                           [Redis: same session] ✓
```

### Three patterns to externalise state:

**1. Redis Session Store (HTTP sessions)**
```java
// Spring Boot: spring-session-data-redis
// application.properties:
spring.session.store-type=redis
spring.session.redis.namespace=spring:session

// That's it — @SessionAttribute now reads/writes Redis, not memory
```
All pods share the same Redis → any pod can serve any request → no sticky needed.

**2. JWT (stateless auth)**
No server-side session at all. Token carries all user identity + claims. Server validates signature, extracts claims — zero shared state.
```
Cookie / Authorization header → JWT → Server validates with secret → done
No Redis lookup, no DB call, no sticky
```

**3. Sticky only where truly unavoidable**
WebSocket connections ARE inherently sticky — the TCP connection is held by one server. Solutions:
- **NLB** (Layer 4) — flow-hash based, WebSocket-safe
- **Redis pub/sub** — messages routed between servers (chat system pattern)
- **API Gateway WebSocket** — AWS manages connection state, routes via connection ID in DynamoDB

---

## AWS

| Scenario | AWS config |
|---|---|
| HTTP sticky session | ALB target group → Stickiness → Duration-based (`AWSALB` cookie) |
| App-controlled sticky | ALB → Application-based stickiness (`AWSALBAPP` cookie set by app) |
| WebSocket | NLB (layer 4, flow hash) or API Gateway WebSocket |
| Stateless (correct) | ALB round-robin, no stickiness — Redis session store via ElastiCache |

**ALB sticky duration:** configurable 1s–7 days. Cookie expires → next request may land on different server → session lost if state not externalised.

**Multi-AZ with sticky:** ALB honours stickiness within an AZ. Cross-AZ failover breaks stickiness — always externalise state for multi-AZ resilience.

---

## Interview Angles

| Question | Answer |
|---|---|
| When do you need sticky sessions? | When state lives in server memory — HTTP sessions, in-progress uploads, WebSocket connections |
| Why are they a code smell? | Break horizontal scaling, defeat failover, cause uneven load — root fix is stateless services |
| How to eliminate them? | Spring Session + Redis (HTTP sessions); JWT (auth); Redis pub/sub (WebSocket routing) |
| WebSocket in K8s? | NLB (layer 4 flow hash) for connection affinity, or API Gateway WebSocket + DynamoDB for connection routing |
| AWS ALB sticky vs NLB? | ALB: cookie-based, layer 7, HTTP only. NLB: flow-hash (IP+port), layer 4, works for WebSocket/TCP |
| What happens when sticky server dies? | Session lost — this is why state must be externalised. Redis session store survives pod death. |

---

## Key Numbers
- ALB sticky: cookie-based, configurable 1s–7 days
- Redis session lookup: ~1ms — negligible vs removing sticky
- NLB flow hash: 5-tuple (src IP, src port, dest IP, dest port, protocol) — stable for long connections
- Spring Session: 1 dependency + 2 config lines → stateless pods

## Domain connections
- **Chat system:** WebSocket connections are sticky per-pod → use Redis pub/sub to route messages cross-pod
- **Sport betting:** in-play WebSocket odds feed → NLB for connection affinity, Redis fan-out for odds updates
- **Banking:** HTTP session auth → Spring Session + ElastiCache → stateless ECS pods, multi-AZ safe
