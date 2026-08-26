# My Answer — Q017

## Issues Found

### 1. [Category] — [short title]
Line X: [what is wrong]
Consequence: [what breaks in prod / at scale]
Fix: [what to change]

1. Constructor injecting services is missing
2. I would move transfer logic into another service maybe PaymentService transfer method and I would make the method transactional
3. all the .get method should be executed only if result isPresent otherwise throw an Exception line 20 23 32
4. Transfer method should probably be also synchronized or manage some sort of idempotency with headers accountId? But I would do all of this in PaymentService


### 2. ...
