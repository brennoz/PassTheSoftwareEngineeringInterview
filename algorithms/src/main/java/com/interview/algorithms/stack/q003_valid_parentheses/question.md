# Q003 — Valid Parentheses

**Category:** Algorithms  
**Difficulty:** Easy  
**Ideal Time:** 15 min

---

## Problem

Given a string `s` containing only the characters `(`, `)`, `{`, `}`, `[`, `]`,
determine if the input string is valid.

A string is valid if:
1. Every open bracket has a corresponding close bracket of the same type.
2. Open brackets are closed in the correct order.
3. Every close bracket has a corresponding open bracket of the same type.

---

## Examples

```
Input:  s = "()"         → true
Input:  s = "()[]{}"     → true
Input:  s = "(]"         → false
Input:  s = "([)]"       → false
Input:  s = "{[]}"       → true
Input:  s = ""           → true
Input:  s = "["          → false
```

---

## Constraints

- `0 <= s.length <= 10^4`
- `s` consists of brackets only: `()[]{}` — no other characters

---

## What to Implement

```java
public boolean isValid(String s)
```

---

## Skeleton

See `Solution.java` for reference skeleton.  
Write your solution in `MyAnswer.java`.  
Write tests in `MyAnswerTest.java`.

---

## Hints (don't read yet)

Think about what data structure naturally handles "last opened must be first closed".
