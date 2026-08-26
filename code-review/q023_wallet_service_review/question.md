# Q023 — Wallet Service Review

**Category:** Code Review  
**Difficulty:** Medium  
**Ideal time:** 15 min

---

## Question

Spot every issue in this wallet service. For each issue state:
- Category
- What is wrong (line reference)
- Real-world consequence in prod / at scale
- Fix

---

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private final SimpleDateFormat formatter =                          // line 16
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Wallet getWallet(Long walletId) {                           // line 19
        return walletRepository.findById(walletId).get();              // line 20
    }

    public void deposit(Long walletId, BigDecimal amount) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(amount));            // line 27

        walletRepository.save(wallet);

        String timestamp = formatter.format(new Date());               // line 31
        Transaction tx = new Transaction(walletId, amount, "DEPOSIT", timestamp);
        transactionRepository.save(tx);                                // line 33
    }

    public List<WalletSummary> getHistory(Long userId) {
        List<Wallet> wallets = walletRepository.findByUserId(userId);
        List<WalletSummary> summaries = new ArrayList<>();
        for (Wallet w : wallets) {                                     // line 39
            int count = transactionRepository
                .findByWalletId(w.getId()).size();                     // line 41
            summaries.add(new WalletSummary(w.getId(), w.getBalance(), count));
        }
        return summaries;
    }
}
```

---

Write your answer in `MyAnswer.md`.
