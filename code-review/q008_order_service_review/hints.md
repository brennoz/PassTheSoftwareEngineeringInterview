# Hints — Q008 Order Service

## Hint 1

Look at these 4 areas — each has at least one issue:

1. **How dependencies are injected** — is this the recommended Spring way?
2. **The fields declared at the top of `OrderService`** — what happens when 2 threads hit `buildSummary()` and `placeOrder()` simultaneously?
3. **The loop inside `buildSummary()`** — how many SQL queries does this generate?
4. **The `catch` block** — what could go wrong silently in production?
