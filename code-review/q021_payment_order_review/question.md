# Q021 — Payment Order Controller Review

**Category:** Code Review  
**Difficulty:** Easy  
**Ideal time:** 12 min

---

## Question

Spot every issue in this payment order controller. For each issue state:
- Category
- What is wrong (line reference)
- Real-world consequence in prod / at scale
- Fix

---

```java
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class PaymentOrderController {

    @Autowired
    private PaymentOrderRepository orderRepository;

    @Autowired
    private PaymentService paymentService;

    // POST /api/orders
    @PostMapping
    public PaymentOrder createOrder(@RequestBody CreateOrderRequest request) {   // line 17

        PaymentOrder order = new PaymentOrder();
        order.setMerchantId(request.getMerchantId());
        order.setAmount(request.getAmount());
        order.setCardToken(request.getCardToken());

        PaymentOrder saved = orderRepository.save(order);
        paymentService.charge(saved);

        return saved;   // line 26
    }

    // GET /api/orders/{id}
    @GetMapping("/{id}")
    public PaymentOrder getOrder(@PathVariable Long id) {   // line 31
        return orderRepository.findById(id).get();          // line 32
    }
}
```

---

Write your answer in `MyAnswer.md`.
