# LRU Cache

## What it is
A cache with fixed capacity that evicts the **Least Recently Used** entry when full. "Recently used" means any get or put on that key. Goal: O(1) get and O(1) put — achieved by combining a **HashMap** (O(1) lookup) with a **Doubly Linked List** (O(1) insert/remove/reorder).

Used everywhere: CPU caches, OS page replacement, Redis eviction policy (`allkeys-lru`), browser cache, CDN.

---

## Core Concepts

### Why doubly linked list + hashmap?
- HashMap alone: O(1) get/put, but can't track access order efficiently
- ArrayList alone: O(1) access by index, but O(N) to move an element to front
- Doubly linked list alone: O(1) insert/remove if you have a pointer, but O(N) lookup
- **Combined:** HashMap stores `key → Node pointer`, list maintains access order. Both O(1).

### Structure
```
HashMap:  key → Node reference (O(1) lookup)

Doubly Linked List:
  HEAD ←→ [most recent] ←→ ... ←→ [least recent] ←→ TAIL
  (sentinel nodes — never removed, simplify edge cases)
```

- On **get(key):** find node via map, move it to front (most recent), return value
- On **put(key, val):** if key exists → update + move to front. If new → add at front. If over capacity → remove node at TAIL (least recent), remove from map.

### Why sentinel HEAD and TAIL?
Avoid null checks. Without sentinels, removing the only node requires special-casing. With sentinels:
```
HEAD ←→ TAIL    (empty cache — always valid state)
```

---

## Code

```java
class LRUCache {

    private final int capacity;
    private final Map<Integer, Node> map;
    private final Node head, tail;  // sentinels

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        head = new Node(0, 0);
        tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        Node node = map.get(key);
        if (node == null) return -1;
        moveToFront(node);
        return node.val;
    }

    public void put(int key, int value) {
        Node node = map.get(key);
        if (node != null) {
            node.val = value;
            moveToFront(node);
        } else {
            Node newNode = new Node(key, value);
            map.put(key, newNode);
            addToFront(newNode);
            if (map.size() > capacity) {
                Node lru = tail.prev;  // least recently used
                remove(lru);
                map.remove(lru.key);
            }
        }
    }

    private void addToFront(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToFront(Node node) {
        remove(node);
        addToFront(node);
    }

    static class Node {
        int key, val;
        Node prev, next;
        Node(int key, int val) { this.key = key; this.val = val; }
    }
}
```

**Complexity:** Time O(1) get + put. Space O(capacity).

---

## Common Patterns

### Pattern 1: Java built-in (interview shortcut — mention then implement manually)
```java
// LinkedHashMap with accessOrder=true is a built-in LRU
Map<Integer, Integer> cache = new LinkedHashMap<>(capacity, 0.75f, true) {
    protected boolean removeEldestEntry(Map.Entry<Integer, Integer> e) {
        return size() > capacity;
    }
};
```
Know this exists — but interviewers expect you to implement the doubly linked list version.

### Pattern 2: Thread-safe LRU (distributed env)
Single-node: wrap with `Collections.synchronizedMap()` or use `ReentrantReadWriteLock`.
Multi-node (AWS): **don't use in-memory LRU** — use **ElastiCache Redis** with `maxmemory-policy allkeys-lru`. Each ECS task hitting the same Redis instance = consistent cache.

### Pattern 3: LRU with TTL (payment domain)
Add `expiryTime` to Node. On get: check if expired → evict if so. Needed for idempotency key stores (expire after 24h).

---

## Interview Angles

| Interviewer asks | You say |
|---|---|
| Why doubly linked not singly? | Need O(1) removal — singly requires traversal to find prev node |
| Why sentinel nodes? | Eliminate null checks on head/tail — every remove/insert works the same way |
| What if concurrent access? | Single node: `ReentrantReadWriteLock`; distributed: ElastiCache Redis |
| Java built-in? | `LinkedHashMap(capacity, 0.75f, true)` + `removeEldestEntry` — but implement from scratch in interview |
| LRU vs LFU? | LRU evicts by recency; LFU evicts by frequency. LFU better for uneven access patterns, harder to implement (O(1) needs bucket list per count) |
| Real-world use? | Redis `allkeys-lru`, CPU L1/L2 cache, CDN edge cache, OS page table |

---

## Key Numbers
- HashMap + DLL → O(1) get, O(1) put
- `LinkedHashMap` in Java IS an LRU if `accessOrder=true`
- Redis default eviction: `noeviction` (throws error) — change to `allkeys-lru` for cache workloads
- Payment domain: idempotency key cache = LRU with 24h TTL → use Redis, not in-memory
