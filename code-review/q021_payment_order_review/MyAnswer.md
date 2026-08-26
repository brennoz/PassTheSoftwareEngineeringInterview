# My Answer — Q021

## Issues Found

### 1. [Category] — [short title]
Line X: [what is wrong]
Consequence: [what breaks in prod / at scale]
Fix: [what to change]

1. Constructor with injected attributes is missing
2. Move all the logic in createOrder probably in PaymentService
3. createOrder hava to be transactional
4. request order have to be idempotent, probaly add a header with idempotency id to check if that order has been already processed
5. line 32 get only in case the value isPresent otherwise throw an execption
6. in both methods PaymentOrder entity needs to be re-wrapped in a PaymentOrderDTO to expose only non-sensitive secure data

### 2. ...
