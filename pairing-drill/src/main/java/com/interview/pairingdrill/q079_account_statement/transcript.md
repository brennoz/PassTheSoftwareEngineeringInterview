# Pairing Session Transcript — q079: Account Statement Service

**Flavor:** Code-review hybrid  
**Domain:** Banking  
**Difficulty:** Medium (4–6 planted issues)  
**Seniority:** Junior

---

## Session Open

**Emma:** Hey, really glad you joined us! So I wrote this statement service last week and it
passed my tests, but the PM says the overnight batch job is sometimes returning zero statements
with no error. I genuinely can't reproduce it locally — everything works fine on my machine.

**Liam:** Yeah, and I noticed on a couple of accounts the credit/debit totals in the statement
don't quite match what's in the ledger view. It's only off by a tiny amount but the compliance
team spotted it and now everyone's worried. Could be a rounding thing?

**Emma:** Oh, and Sasha from infra mentioned the batch job is running super slow — like 4× longer
than the previous one. She asked if we were doing something unusual with the database.

**[Your turn — open `answer/AccountStatementService.java` and start reviewing with Emma and Liam.]**

---
*(subsequent exchanges appended on each `review` call)*
