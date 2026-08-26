# Hints — Q005 Design Google Docs

## Hint 1 (conflict resolution)

Most candidates say "last write wins" — that's wrong and corrupts the doc.

The key: two users type at the **same position** at the same time. You need to **transform** one op against the other before applying.

**Operational Transformation (OT):**
- Every edit is an operation: `insert(pos, char)` or `delete(pos)`
- When op B arrives after op A was already applied, shift B's position by A's effect
- Google Docs uses **server-authoritative OT**: all ops go through one server that serializes them → no N² transform matrix problem

Start there. Mention CRDT as an alternative (Figma uses it) and one tradeoff.
