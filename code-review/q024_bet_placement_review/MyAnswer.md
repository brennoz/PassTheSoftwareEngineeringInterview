# My Answer — Q024

## Issues Found

### 1. [Category] — [short title]
Line X: [what is wrong]
Consequence: [what breaks in prod / at scale]
Fix: [what to change]
1. line 22 needs to throw exception if Wallet is not found
2. placeBet needs to be transactional
3. LiabilityCount needs to store in a shared in-memory cache like Redis
4. Wallet needs to consider @Version
5. attribures in constructr as input params

### 2. ...
