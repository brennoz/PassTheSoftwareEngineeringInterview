# q032 Twitter OOD — Review

**Result: PASS — 4/4 tests green. Three code quality issues worth knowing.**

---

## What was correct

| Area | Assessment |
|------|-----------|
| Tweet hierarchy — polymorphism | ✅ OriginalTweet/Retweet/Reply each override displayContent() correctly |
| Observer wiring | ✅ follow() registers feed as observer; post/retweet/reply all call notifyObservers |
| TweetBuilder — validation | ✅ blank check + length check both present |
| TweetBuilder — fluent API | ✅ hashtag/media return `this` |
| Feed — LinkedList/Deque | ✅ correct data structure chosen |
| CopyOnWriteArrayList | ✅ used for observers |
| Retweet.displayContent | ✅ attribution format correct |
| Reply.displayContent | ✅ @username prefix correct |

---

## Issue 1 — Feed cap is 201, not 200 (off-by-one)

```java
// Your code:
if (!timeline.isEmpty() && timeline.size() > 200) {
    timeline.removeLast();
}
timeline.addFirst(tweet);
```

**Trace when feed reaches capacity:**

| State | timeline.size() | condition | action |
|-------|-----------------|-----------|--------|
| Before 201st post | 200 | 200 > 200 = **false** | no removal |
| After addFirst | 201 | — | feed now has **201 items** |
| Before 202nd post | 201 | 201 > 200 = **true** | removeLast → 200 |
| After addFirst | 201 | — | still 201 |

Feed oscillates and always settles at 201 — one over the cap.

Fix: check **after** adding, or change condition to `>=`:
```java
// Option A — add first, then trim:
timeline.addFirst(tweet);
if (timeline.size() > MAX_FEED_SIZE) timeline.removeLast();

// Option B — trim before if already at limit:
if (timeline.size() >= MAX_FEED_SIZE) timeline.removeLast();
timeline.addFirst(tweet);
```

Tests didn't catch this because they only add 1 tweet. In a real interview, the interviewer will ask: "How does your feed handle 201 posts from Alice?"

---

## Issue 2 — `MAX_CONTENT_SIZE` is not a constant

```java
private int MAX_CONTENT_SIZE = 280;
```

Uppercase names by Java convention signal `static final` constants.
This is an instance field, mutable, and takes memory per `TweetBuilder` instance.

Fix:
```java
private static final int MAX_CONTENT_SIZE = 280;
```

Minor, but interviewers notice naming convention violations — especially on a value that clearly never changes.

---

## Issue 3 — Redundant condition + misleading variable names (style)

**a) Redundant `!content.isEmpty()` in the length check:**
```java
if (content.isBlank()) { throw ...; }                    // if we reach line 2:
if (!content.isEmpty() && content.length() > MAX_CONTENT_SIZE) { ... }  // isEmpty is already false
```
After the blank check passes, `content` is guaranteed non-empty. The `!content.isEmpty()` guard is dead code.

**b) Variables named `tweet` for specific subtypes:**
```java
Retweet tweet = new Retweet(this, source);  // confusing — type says Retweet, name says tweet
Reply   tweet = new Reply(this, content, parent);
```
Name the variable after its type: `Retweet rt = ...` / `Reply reply = ...`.

---

## Issue 4 — Minor: missing space after colon in Retweet

```java
return "RT @" + sourceTweet.getAuthor().getUsername() + ":" + sourceTweet.displayContent();
//                                                           ^^ missing space
```

Standard format is `"RT @alice: content"`. Tests passed because they only checked `.contains("@alice")` and `.contains("Great idea!")` — a stricter test would fail. Fix: `": "`.

---

## Summary

| Category | Result |
|----------|--------|
| Patterns applied correctly | ✅ Observer, Builder, polymorphism |
| Tests | ✅ 4/4 |
| Feed cap (off-by-one) | ⚠️ fix: addFirst then trim |
| Static final constant | ⚠️ minor naming convention |
| Style | ⚠️ redundant condition, variable names |

Strong pass. The only real behavioural bug is the feed off-by-one — everything else is style. In an interview, correct the feed cap and you'd get full marks.
