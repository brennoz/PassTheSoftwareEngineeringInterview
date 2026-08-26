# Q008 — Spot the Issues: Order Service

**Category:** Code Review  
**Difficulty:** Medium  
**Ideal Time:** 15 min

---

## Task

The following code was written by a junior developer and deployed to a 3-instance Kubernetes cluster. Find everything that is wrong — design flaws, Spring anti-patterns, distributed system pitfalls, concurrency bugs, JPA issues, anything.

---

## Code

```java
// OrderController.java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{id}/summary")
    public ResponseEntity<OrderSummaryDto> getSummary(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.buildSummary(id));
    }

    @PostMapping
    public ResponseEntity<Void> place(@RequestBody OrderRequest request) {
        orderService.placeOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

// OrderService.java
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private NotificationClient notificationClient;

    private int totalOrdersProcessed = 0;

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private final Map<Long, OrderSummaryDto> summaryCache = new HashMap<>();

    public OrderSummaryDto buildSummary(Long orderId) {
        if (summaryCache.containsKey(orderId)) {
            return summaryCache.get(orderId);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("not found"));

        List<String> lines = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            lines.add(item.getProduct().getName() + " x" + item.getQuantity());
        }

        String date = "";
        try {
            date = FORMATTER.format(order.getCreatedAt());
        } catch (Exception e) {
            // ignore
        }

        OrderSummaryDto dto = new OrderSummaryDto(order.getId(), lines, date);
        summaryCache.put(orderId, dto);
        return dto;
    }

    public void placeOrder(OrderRequest request) {
        totalOrdersProcessed++;
        Order order = new Order(request.getUserId(), request.getItems(), new Date());
        orderRepository.save(order);
        notificationClient.sendConfirmation(order);
    }
}
```

---

## Write your answer in `MyAnswer.md`

For each issue:
```
### N. [Category] — short title
Line(s): X
What is wrong: ...
Consequence in prod: ...
Fix: ...
```

Categories: Spring anti-pattern | Concurrency | Distributed pitfall | JPA/Hibernate | Exception handling | Java style
