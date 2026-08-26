# URL Shortener

## What it is
A service that maps a short opaque code (e.g. `short.ly/aB3xKz`) to an original long URL and redirects visitors to it. Read-heavy (100:1 read/write typical), latency-critical on redirect, and requires globally unique code generation at scale without coordination overhead.

---

## Core Concepts

- **Base62 encoding** — 62 chars (a-z A-Z 0-9). 6 chars = 62^6 ≈ 56 billion codes. Enough for 100M URLs with huge headroom.
- **Code generation strategies** — two main approaches: hash-then-truncate vs. counter-then-encode (see patterns below).
- **301 vs 302 redirect** — the single most-asked interview detail (see below).
- **Cache-aside on read path** — redirects hit Redis first; DB only on miss.
- **Write path is rare** — only shorten needs the DB write; optimize for reads.
- **Data model is simple** — one table: `(code PK, long_url, created_at, user_id?)`.

---

## 301 vs 302 — Why it matters

| | 301 Permanent | 302 Temporary |
|---|---|---|
| Browser caches? | Yes — next visit skips your server | No — every visit hits your server |
| Click analytics possible? | No — browser goes direct | Yes — every redirect logged |
| Server load | Lower (cache absorbs repeats) | Higher |
| Use when | CDN/static shortener, no analytics | Analytics, A/B, expiry needed |

**Interview answer:** Default to **302** if you need analytics or URL expiry. Use **301** only if load reduction is the priority and analytics are irrelevant.

---

## Code Sample

### Option A — Hash + Truncate (stateless, collision-prone)
```java
// MD5 of long URL → take first 6 Base62 chars
public String shorten(String longUrl) {
    byte[] hash = MessageDigest.getInstance("MD5").digest(longUrl.getBytes());
    long num = ByteBuffer.wrap(hash).getLong(); // first 8 bytes → long
    return toBase62(Math.abs(num)).substring(0, 6);
}

private String toBase62(long n) {
    String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder sb = new StringBuilder();
    while (n > 0) { sb.append(chars.charAt((int)(n % 62))); n /= 62; }
    return sb.reverse().toString();
}
```
Problem: two different URLs can hash to same 6 chars → must check DB + retry.

### Option B — Counter + Base62 (recommended at scale)
```java
// Global counter (Redis INCR or DB sequence) → encode to Base62
public String shorten(String longUrl) {
    long id = redisClient.incr("url:counter"); // atomic, no collision
    String code = toBase62(id);               // always unique
    db.insert(code, longUrl);
    return "short.ly/" + code;
}
```
Why better: no collision by design. Counter = unique ID. Base62 encode it.
Tradeoff: counter is a single point of contention → shard with range partitions or use Snowflake ID.

---

## Common Patterns

### 1. Read path with Cache-Aside
```
GET /aB3xKz
  → Redis GET aB3xKz
      HIT  → 302 redirect (< 1ms)
      MISS → DB SELECT WHERE code='aB3xKz'
           → Redis SET aB3xKz <long_url> EX 86400
           → 302 redirect
```
Cache TTL: 24h is common. Hot URLs (top 20%) absorb ~80% of traffic via cache.

### 2. Write path
```
POST /shorten { longUrl }
  → generate code (counter or hash)
  → DB INSERT (code, longUrl)
  → return short URL
  (no cache write on shorten — lazy load on first redirect)
```

### 3. Custom aliases
User picks slug → check uniqueness in DB → insert if available → 409 Conflict if taken.

---

## Data Model

```sql
CREATE TABLE urls (
    code       VARCHAR(8)   PRIMARY KEY,   -- the short code
    long_url   TEXT         NOT NULL,
    created_at TIMESTAMPTZ  DEFAULT now(),
    user_id    BIGINT                       -- nullable, for owned links
);
CREATE INDEX ON urls(long_url);  -- optional: detect duplicate submissions
```

**DB choice:** PostgreSQL or DynamoDB. PostgreSQL: ACID, simple. DynamoDB: key-value at scale, O(1) lookup on `code` PK, no joins needed.

---

## Scale & Bottlenecks

| Bottleneck | Solution |
|---|---|
| Redirect latency | Redis cache-aside (< 1ms hit rate) |
| Write throughput (counter) | Shard counter: each app node owns a range (e.g. node 1 uses 1–1000, node 2 uses 1001–2000) |
| DB read throughput | Read replicas + cache |
| DB storage | 100M rows × ~500 bytes ≈ 50GB — fits one Postgres instance |
| Global distribution | CDN edge caching 301 redirects; or multi-region DB replica |

---

## Interview Angles

- **"How do you generate unique codes?"** → Counter + Base62 vs Hash + truncate. Know tradeoffs.
- **"301 or 302?"** → Always explain analytics vs. caching tradeoff. Default: 302.
- **"How do you handle hash collisions?"** → Retry with salt, or avoid by using counter.
- **"How do you scale writes?"** → Shard the counter; or use Snowflake ID (no coordination).
- **"What database?"** → Simple key-value access pattern → DynamoDB or Redis. If you need analytics queries → PostgreSQL.
- **"How do you prevent abuse (spam URLs)?"** → Rate limit by IP on POST /shorten; URL blocklist.
- **"Custom aliases?"** → Unique constraint on code; 409 on conflict.
