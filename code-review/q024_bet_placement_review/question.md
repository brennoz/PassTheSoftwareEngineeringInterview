# Q024 — Bet Placement Service Review

**Category:** Code Review — Sport Betting Domain
**Difficulty:** Medium  
**Ideal time:** 15 min

---

## Question

Spot every issue in this bet placement service. For each issue state:
- Category
- What is wrong (line reference)
- Real-world consequence in prod / at scale
- Fix

---

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BetPlacementService {

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private OddsService oddsService;

    private static int liabilityCount = 0;                               // line 17

    public BetReceipt placeBet(PlaceBetRequest request) {

        Wallet wallet = walletRepository
            .findByUserId(request.getUserId()).get();                     // line 22

        if (wallet.getBalance() < request.getStake()) {                  // line 24
            throw new IllegalStateException("Insufficient balance");
        }

        double odds   = oddsService.getOdds(request.getMarketId(),
                                            request.getOutcome());       // line 29
        double payout = request.getStake() * odds;                       // line 30

        wallet.setBalance(wallet.getBalance() - request.getStake());     // line 32
        walletRepository.save(wallet);

        Bet bet = new Bet(
            request.getUserId(),
            request.getMarketId(),
            request.getOutcome(),
            request.getStake(),
            payout
        );
        betRepository.save(bet);                                         // line 42

        liabilityCount++;                                                // line 44

        return new BetReceipt(bet.getId(), payout);
    }
}
```

---

Write your answer in `MyAnswer.md`.
