**Lessons:** [0001 Design Twitter](../../../topics/system-design/twitter/0001-twitter-system-design.html)

# q034 — Design Twitter

**Category:** System Design · **Difficulty:** Hard · **Ideal time:** 35 min

---

## Scenario

Design a simplified Twitter supporting:
- Users post tweets (text ≤ 280 chars, optional media)
- Users follow / unfollow other users
- Users see a personalised home timeline (tweets from people they follow)
- Users can search tweets by keyword
- Trending hashtags visible globally

Scale: **300M DAU**, **500M tweets/day**, read:write ~100:1.
One celebrity can have **100M followers**.

---

## Sections to cover

### 1. Scale estimation
- Tweets/sec (average + peak), storage/day, timeline reads/sec

### 2. Core data model
- Where do tweets live? Why that database?
- How is the social graph stored?
- What is a Snowflake ID and why use it instead of UUID or auto-increment?

### 3. Timeline / Feed generation — the most important section
- Explain fan-out on write: how it works, its weakness
- Explain fan-out on read: how it works, its weakness
- Explain the hybrid approach: who gets which strategy, how Feed Service merges at read time

### 4. Timeline storage
- What data structure in Redis stores a user's timeline?
- What is the score? What is the value? How is it capped?

### 5. Tweet posting flow (end-to-end)
- From client POST /tweets to followers seeing it — every hop

### 6. Media handling
- Upload flow, processing, serving

### 7. Search and trending
- Why Elasticsearch for search (not Cassandra)?
- How do you compute trending hashtags with time decay?

### 8. AWS mapping
- Map every component to a managed AWS service

---

## Write your answer

`MyAnswer.md` in this folder. Diagrams as ASCII are encouraged.

---

*Hint → `hint` | Submit → `done`*
