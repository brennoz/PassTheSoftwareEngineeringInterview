# q080 — Transcript

---

## Session start

**Emma:** Okay so last night was a mess. On-call got woken up at 11pm — bets were being accepted on
`mkt_arsenal_to_win` after the market was supposed to be suspended. The red card came in, the market
consumer called `suspendMarket()`, but the odds-feed thread was mid-update at the same time. Some bets
got through on the old odds. Compliance is asking questions.

**Noah:** I pulled the code. I mean... it looks fine to me? It checks `suspendedMarkets.contains(marketId)`
before doing anything. If the market's suspended it returns early. What am I missing?

**Emma:** *(to you)* We think there's a threading issue but neither of us has done much concurrent Java.
Can you walk us through what you see? I'm also curious — is there anything else in here that could cause
problems, not just the suspension thing?

---

*(append `review` exchanges below as the session progresses)*
