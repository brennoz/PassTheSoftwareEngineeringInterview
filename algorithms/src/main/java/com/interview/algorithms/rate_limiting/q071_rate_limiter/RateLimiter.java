package com.interview.algorithms.rate_limiting.q071_rate_limiter;

/**
 * A single-key rate limiter.
 *
 * <p>Each instance guards ONE logical key (one user, one API token, one IP). The limit
 * (capacity / window / refill rate) is fixed at construction — there is no key argument
 * because there is nothing to key on: the caller owns a map of {@code key -> RateLimiter}
 * if it needs per-key limiting.
 *
 * <h2>Return contract (the name is ambiguous, so this is nailed down here)</h2>
 * <pre>
 *   if (rateLimiter.limit()) {
 *       // ALLOWED — a token/slot was consumed, proceed with the request
 *   } else {
 *       // REJECTED — over budget, caller should return HTTP 429
 *   }
 * </pre>
 * {@code limit()} returns {@code true} when the call is <b>within</b> the limit (allowed)
 * and {@code false} when it is throttled. Calling {@code limit()} is not side-effect free:
 * a {@code true} result consumes one unit of budget.
 *
 * <h2>Thread safety</h2>
 * Implementations are safe to call from multiple threads. Each does a check-then-act on a
 * shared counter (token count / request count / timestamp log), which is a race without
 * synchronisation — two threads could both read "1 token left" and both consume it.
 *
 * <h2>In AWS / distributed environment</h2>
 * Every implementation here holds state in a single JVM's heap. On a multi-pod deployment
 * (ECS / EKS / k8s Deployment with replicas &gt; 1) each pod has its own limiter, so the
 * effective global limit is {@code perPodLimit * podCount} and it drifts as pods scale.
 * The fix is shared state in ElastiCache (Redis): {@code INCR + EXPIRE} for fixed window,
 * a sorted set for the sliding log, a Lua script or hash for the token bucket — each
 * mutation atomic on the Redis side so the check-then-act race moves off the app entirely.
 */
public interface RateLimiter {

    /**
     * @return {@code true} if this call is within the limit (and one unit of budget has
     *         been consumed); {@code false} if it is throttled.
     */
    boolean limit();
}
