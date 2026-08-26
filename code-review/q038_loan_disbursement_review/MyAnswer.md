## Issues Found

### 1. [Category] — [title]
Line ~X: 
Consequence: 
Fix: 

### 2. ...

- loan.getStatus() == "PENDING") loan.getStatus() == "APPROVED" they should be equals! Objects equality.
- approve method should return LoanApplicationDTo not directly the entity
- approvedAmount should be BigDecimal with relattive operations
- List<LoanApplication> method should decouple from entity getting backa list of DTOs
- disburse and approve should be transactional (missing transactional annotation)
- auditLogger is not used maybe a ComplatableFuture which triggers run method
- both methods miss an idempotency check
- maybe some caching woult be better


