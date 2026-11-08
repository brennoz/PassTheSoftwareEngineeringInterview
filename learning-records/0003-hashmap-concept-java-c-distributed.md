---
name: hashmap-concept-java-c-distributed
description: HashMap lesson — core concept, Java internals (Node[], treeify, hashCode/equals), C struct + djb2 implementation, consistent hashing for distributed KV stores
metadata:
  type: project
---

# HashMap — Concept, Java, C, Distributed KV

**Context:** Requested as a broad cross-cutting lesson (not tied to a specific interview question) — covers the data structure at all abstraction levels.

**Key concepts:**

**Core:**
- Hash function maps key → integer → bucket index (`hash(key) % capacity`)
- O(1) average lookup; no ordering guarantee
- Collision = two keys map to same bucket

**Java HashMap:**
- `Node<K,V>[]` table, default capacity 16 (power of 2), loadFactor 0.75
- Capacity always power of 2 → bucket = `hash & (capacity-1)` (bit-mask, not modulo)
- Resize doubles: rehashes all entries
- Collision: separate chaining (linked list); treeify at chain length > 8 → Red-Black Tree (Java 8+)
- Treeify threshold 8, untreeify 6, min treeify capacity 64
- Java's spread function: `h ^ (h >>> 16)` — mixes high 16 bits to prevent clustering in small tables

**hashCode / equals contract:**
- If `a.equals(b)` → `a.hashCode() == b.hashCode()` (required by Java spec)
- Same hash does NOT imply equal (just a collision)
- Forgetting to override `hashCode()` → bucket miss → `get()` returns null silently

**C struct:**
- `struct` = named block of fields, contiguous in memory
- `typedef struct X X` removes need to write `struct` prefix
- `*` dereference, `&` address-of, `->` arrow = `(*ptr).field`
- `malloc(sizeof(T))` — heap allocate; always check NULL; `free()` when done
- `calloc(n, size)` = `malloc(n*size)` + zero-fills
- `strdup(s)` = heap-copies string; caller must `free()`
- `strcmp(a, b) == 0` for string equality — `a == b` compares pointers

**C HashMap (djb2):**
- `Entry` struct with `char *key`, `char *value`, `Entry *next`
- `HashMap` struct with `Entry **buckets` (pointer-to-pointer), `capacity`
- djb2: `h = h*33 + c` per character, seed 5381
- `hm_put`: djb2 → index → walk chain (update if found, else head-insert)
- `hm_get`: djb2 → index → walk chain with `strcmp`

**Distributed KV:**
- Consistent hashing: ring 0→2³², key → first clockwise server
- Adding node remaps ~1/N keys; removing remaps successor's keys only
- Virtual nodes (vnodes): each server appears at K positions → even distribution
- N/W/R quorums: `W + R > N` → strong consistency (read set overlaps write set)
- Redis Cluster: 16384 slots, CRC16; DynamoDB: vnodes, R/W configurable; Cassandra: tunable per query

**Interview traps:**
- "HashMap is O(1)" — only average; constant `hashCode()` → O(n)
- Override `equals()` without `hashCode()` → silent bucket miss
- `a == b` in C compares pointers, not string content

**Next:** CompletableFuture pipelines (continuing Java concurrency track)
