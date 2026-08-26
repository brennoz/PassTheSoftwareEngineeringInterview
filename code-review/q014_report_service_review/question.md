# Q014 — Spot the Issues: Report Service

**Category:** Code Review  
**Difficulty:** Medium  
**Ideal Time:** 15 min

---

## Task

This reporting service runs in production on a 4-node cluster. Find everything wrong.

---

## Code

```java
// ReportController.java
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public List<Report> getReports(@RequestParam String status,
                                   @RequestParam String customerId) {
        return reportService.findReports(status, customerId);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Report> approve(@PathVariable Long id) {
        Optional<Report> report = reportService.findById(id);
        Report r = report.get();
        r.setStatus("APPROVED");
        reportService.save(r);
        return ResponseEntity.ok(r);
    }
}

// ReportService.java
@Service
@Slf4j
public class ReportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private NotificationClient notificationClient;

    private Map<String, List<Report>> cache = new HashMap<>();

    public List<Report> findReports(String status, String customerId) {
        String cacheKey = status + "_" + customerId;
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }
        String sql = "SELECT * FROM reports WHERE status = '" + status
                   + "' AND customer_id = '" + customerId + "'";
        List<Report> results = jdbcTemplate.query(sql, new ReportRowMapper());
        cache.put(cacheKey, results);
        return results;
    }

    public void processAll() {
        List<Report> reports = reportRepository.findAll();
        for (Report report : reports) {
            if (report.getStatus() == "PENDING") {
                report.setStatus("PROCESSING");
                reportRepository.save(report);
                notificationClient.notify(report);
            }
        }
    }

    private void sendNotification(Report report) {
        try {
            notificationClient.notify(report);
        } catch (Exception e) {
            log.error("Notification failed: " + e.getMessage());
        }
    }
}
```

---

## Write your answer in `MyAnswer.md`

For each issue:
```
### N. [Category] — short title
Line(s): where
What is wrong: ...
Consequence in prod: ...
Fix: ...
```

Categories: Security | Spring anti-pattern | Java bug | Distributed pitfall | Exception handling | Data integrity
