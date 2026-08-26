# Review — Q012 User Registration Service

**Result: ❌ Fail**  
**Score: 3 / 6 planted issues (+ 3 valid bonus observations)**

---

## Planted Issues — Found ✅

1. **Constructor injection** ✅ — correct. Both controller and service should use constructor injection.

2. **Password in logs** ✅ — correct. `log.info("... password={}", request.getPassword())` = security violation. Never log credentials.

3. **Missing `@Transactional` on `register()`** ✅ — correct. Two `save()` calls; if second fails, user exists in DB without a role → inconsistent state. Wrap entire method in `@Transactional`.

---

## Planted Issues — Missed ❌

4. **SQL INJECTION** — biggest miss in the whole snippet:
   ```java
   String sql = "SELECT * FROM users WHERE name = '" + name + "'";
   ```
   Input `' OR '1'='1` dumps entire users table. Input `'; DROP TABLE users; --` destroys data.
   Fix: parameterised query — `createNativeQuery("SELECT * FROM users WHERE name = :name", User.class).setParameter("name", name)`.
   This is OWASP #1 and should be spotted immediately.

5. **`Optional.get()` without check**:
   ```java
   return userRepository.findById(id).get();
   ```
   Throws `NoSuchElementException` if user not found → unhandled 500 to client.
   Fix: `.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))`.

6. **Plain text password**:
   ```java
   new User(request.getEmail(), request.getPassword())
   ```
   Password stored as-is in DB. Any DB read/breach exposes all passwords.
   Fix: `passwordEncoder.encode(request.getPassword())` (BCryptPasswordEncoder).
   You caught password in *logs* but missed password in *storage* — different issue.

7. **Returning entity directly from controller** (both endpoints):
   Exposes all JPA fields to API consumers: password hash, internal IDs, audit columns, role internals.
   Fix: map to a DTO before returning.

---

## Bonus Observations (valid, not planted)
- Pagination on search ✅ — good catch, unbounded result sets are a real prod issue
- Double `save()` inefficiency ✅ — set role before first save, one save is enough
- Email uniqueness check ✅ — good defensive thinking
- Unused `@Slf4j` on controller ✅ — minor but shows attention to detail

---

## Study Priority
- **SQL injection via string concatenation** — must be a reflex. Any `"... '" + input + "'"` = instant flag.
- **`Optional.get()` without check** — always use `orElseThrow` or `orElse`
- **Password encoding** — BCrypt before any persistence, non-negotiable
