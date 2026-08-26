# Q002 — Design a URL Shortener

**Category:** System Design  
**Difficulty:** Easy  
**Ideal Time:** 20 min

---

## Problem

Design a service like bit.ly or TinyURL.

Users submit a long URL → system returns a short code (e.g. `short.ly/aB3xKz`).  
Anyone with the short link gets redirected to the original URL.

---

## Functional Requirements

- `POST /shorten` — accepts a long URL, returns a short URL
- `GET /{code}` — redirects to the original URL
- Short codes are globally unique
- (Optional) custom aliases: user picks their own slug

## Non-Functional Requirements

- **100M URLs stored**
- **Read-heavy**: 100:1 read-to-write ratio
- Short codes: 6–8 characters
- Redirects must be fast (< 10 ms p99)
- URLs do not expire (for now)

---

## What to Cover in Your Answer

1. **Short code generation** — how do you create unique 6-char codes at scale?
2. **Data model** — what do you store, in which database?
3. **Redirect flow** — HTTP 301 vs 302, and why it matters
4. **Caching** — where and what do you cache?
5. **Scale** — where are the bottlenecks? How do you handle them?

---

## Answer File

Write your answer in `MyAnswer.md` (this folder).  
Diagrams as ASCII or description — no tool required.
