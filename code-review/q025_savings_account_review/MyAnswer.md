# My Answer — Q025

## Issues Found

### 1. [Category] — [short title]
1. line 13 should throw an exceptoin if accountId is not found
2. line 12 should wrap data in a DTO not replying directly with the db
 entity
3. line 21-23 should use BigDecimal instead of double
4. applyMonthlyInterest should be @Transactional
5. applyMonthlyInterest should return DTO instead of direct db entity
6. Should also check for indempotecy

### 2. ...
