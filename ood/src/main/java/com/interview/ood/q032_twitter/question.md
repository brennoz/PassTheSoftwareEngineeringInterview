**Lessons:** [0001 Tweet Model & Observer Feed](../../../../../topics/ood/twitter/0001-tweet-model-observer.html)

# q032 — Twitter (Simplified OOD)

**Category:** OOD · **Difficulty:** Medium · **Ideal time:** 25 min

---

## Problem

Design a simplified Twitter system supporting:

1. Users can **post** original tweets (max 280 characters), **retweet** existing tweets, and **reply** to tweets.
2. Users can **follow** and **unfollow** other users.
3. When a user posts a tweet, it appears in all their followers' feeds (**fan-out on write**).
4. Each user has a **feed** capped at the most recent 200 tweets.
5. A user can **like** and **unlike** any tweet.

---

## Requirements

- Use a **polymorphic Tweet hierarchy** — `OriginalTweet`, `Retweet`, `Reply` each extend abstract `Tweet`.
  - Each type implements `String displayContent()`.
- Use **Builder Pattern** for constructing `OriginalTweet` (optional hashtags, optional mediaUrl; validate 280-char limit).
- Use **Observer Pattern** for feed fan-out: `Feed` implements `FeedObserver`; `User.follow(target)` registers this user's feed as an observer on target.
- `Feed` timeline must use `Deque<Tweet>` (LinkedList) — O(1) prepend + bounded cap removal.
- Observer list on `User` must be thread-safe (`CopyOnWriteArrayList`).
- No Spring, no persistence, pure Java 21.

---

## Class skeleton guide

```
Tweet (abstract)
  OriginalTweet
  Retweet
  Reply

TweetBuilder → OriginalTweet

FeedObserver (interface)
  Feed implements FeedObserver

User
  follow(User) / unfollow(User)
  post(String content) → Tweet
  retweet(Tweet) → Retweet
  reply(Tweet, String content) → Reply
  getFeed() → Feed

Like (value object: user + tweet + createdAt)
```

---

## Constraints

- `Tweet.displayContent()` must be polymorphic — no `instanceof` or type checks anywhere
- `TweetBuilder` validates content at build time
- `Feed` capped at 200 tweets (drop oldest when exceeded)
- No `HashMap` for the observer list — `CopyOnWriteArrayList` required

---

## Skeleton

`MyAnswer.java` in this folder — implement all TODOs.

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
