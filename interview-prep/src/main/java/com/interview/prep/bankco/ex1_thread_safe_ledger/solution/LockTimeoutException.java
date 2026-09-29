package com.interview.prep.bankco.ex1_thread_safe_ledger.solution;

// Thrown when tryLock() times out rather than blocking forever. A live-coding interviewer
// will often ask "what if the lock never becomes free?" — this is the answer: fail fast and
// let the caller retry, instead of a thread hanging indefinitely (which is how thread-pool
// starvation incidents happen in production).
public class LockTimeoutException extends RuntimeException {
    public LockTimeoutException(String accountId) {
        super("Could not acquire lock for account " + accountId + " within timeout");
    }
}
