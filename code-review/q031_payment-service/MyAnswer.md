# q031 — My Answer

## Issues Found

### 1. [Category] — [title]
Line: 
What's wrong: 
Consequence in production: 
Fix: 

### 2. ...
1. Exposing  log.info("Processing payment: card={},...) card info in the log is unsecure
2. values like amount should be treated as BigDecimal because double is not precise
3. ProcessPayment method should be transactional
4. IdempotencyKey shoudl be used to check if payment already exist to avoid duplication
5. The Map should be a ConcurrentHashMap tp avoid race conditions
6. calculateFee should return BigDecimal
