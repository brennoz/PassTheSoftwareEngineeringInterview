---
name: feedback-ood-prod-leaning
description: From q033 onward, OOD exercises use production-leaning design over pure textbook OOD
metadata:
  type: feedback
---

From q033 onward, OOD questions use a production-leaning structure — not pure textbook OOD patterns.

**Rule:** Domain objects are thin data containers. Services hold logic. Relationships are explicit entities.

**Structure:**
- `User`, `Tweet`, `Book` etc. = data containers only (no graph logic, no embedded observer lists)
- Relationship entities = `Follow`, `Loan`, `Like`, `Reservation` — map to real DB rows
- In-memory repos = `Map<String, T>` simulating persistence (no Spring/JPA but same shape)
- Service classes = `SocialGraph`, `FeedService`, `LibraryService` own queries and business logic
- Always queryable: "who follows whom?", "show active loans", "get feed for user"

**Why:** User asked after q032 Twitter — Observer-in-User is textbook OOD but can't answer "who am I following?" without scanning all users. Production systems separate the social graph from feed delivery.

**How to apply:** Still use design patterns where appropriate (Strategy, Builder, State, etc.) but wire them through services, not by embedding logic in domain objects.
