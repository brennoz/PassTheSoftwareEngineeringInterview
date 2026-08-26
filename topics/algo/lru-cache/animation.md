# LRU Cache — Step-by-Step Animation

## Input
```
capacity = 3
Operations: put(1,A), put(2,B), put(3,C), get(1), put(4,D), get(2), put(5,E)
```

Notation: `HEAD ←→ [key:val] ←→ ... ←→ TAIL`  
Most recent = left (near HEAD). Least recent = right (near TAIL).  
`map: {key→node}`

---

## Steps

### Step 1: `put(1, A)` — cache empty, just add
```
List:  HEAD ←→ [1:A] ←→ TAIL
Map:   {1→[1:A]}
Size:  1/3
```
New node added at front (most recent).

---

### Step 2: `put(2, B)` — add, not full
```
List:  HEAD ←→ [2:B] ←→ [1:A] ←→ TAIL
Map:   {1→[1:A], 2→[2:B]}
Size:  2/3
```
2 is most recent. 1 slides back.

---

### Step 3: `put(3, C)` — add, now full
```
List:  HEAD ←→ [3:C] ←→ [2:B] ←→ [1:A] ←→ TAIL
Map:   {1→[1:A], 2→[2:B], 3→[3:C]}
Size:  3/3  ← FULL
```

---

### Step 4: `get(1)` — key exists → move to front
```
Before: HEAD ←→ [3:C] ←→ [2:B] ←→ [1:A] ←→ TAIL
                                      ^
                               found via map O(1)
                               remove from current position
                               insert at front

After:  HEAD ←→ [1:A] ←→ [3:C] ←→ [2:B] ←→ TAIL
Map:   {1→[1:A], 2→[2:B], 3→[3:C]}  ← unchanged
Returns: A
```
Key insight: map still points to same node object — just relinked in list. O(1).

---

### Step 5: `put(4, D)` — new key, cache FULL → evict LRU
```
Current: HEAD ←→ [1:A] ←→ [3:C] ←→ [2:B] ←→ TAIL
                                      ^
                              tail.prev = [2:B] ← LEAST RECENTLY USED

Evict [2:B]:
  - remove [2:B] from list
  - remove key 2 from map

Add [4:D] at front:

After:  HEAD ←→ [4:D] ←→ [1:A] ←→ [3:C] ←→ TAIL
Map:   {1→[1:A], 3→[3:C], 4→[4:D]}
Size:  3/3
```
2 was LRU because `get(1)` moved 1 to front — 2 was never touched after step 2.

---

### Step 6: `get(2)` — key evicted → cache miss
```
List:  HEAD ←→ [4:D] ←→ [1:A] ←→ [3:C] ←→ TAIL
Map:   {1→[1:A], 3→[3:C], 4→[4:D]}   ← 2 not here

Returns: -1  (miss)
List unchanged.
```

---

### Step 7: `put(5, E)` — new key, cache FULL → evict LRU
```
Current: HEAD ←→ [4:D] ←→ [1:A] ←→ [3:C] ←→ TAIL
                                      ^
                              tail.prev = [3:C] ← LRU (never touched since step 3)

Evict [3:C]:
  - remove [3:C] from list
  - remove key 3 from map

Add [5:E] at front:

After:  HEAD ←→ [5:E] ←→ [4:D] ←→ [1:A] ←→ TAIL
Map:   {1→[1:A], 4→[4:D], 5→[5:E]}
```

---

## Final State
```
List (most → least recent):  [5:E] → [4:D] → [1:A]
Map:  {1, 4, 5}
Evicted:  2 (step 5), 3 (step 7)
```

---

## Key Insight
The doubly linked list gives you **O(1) reorder** — you already have the node pointer from the map, so remove + reinsert is just pointer surgery (4 pointer updates). No traversal ever needed.

```
remove(node):
    node.prev.next = node.next   // bypass node from left
    node.next.prev = node.prev   // bypass node from right

addToFront(node):
    node.next = head.next        // node points forward to old first
    node.prev = head             // node points back to sentinel
    head.next.prev = node        // old first points back to node
    head.next = node             // sentinel points forward to node
```

Sentinel HEAD and TAIL mean these 4 lines work even for empty cache or single element — no special cases.

---

## Complexity
Time: O(1) get | O(1) put  
Space: O(capacity)
