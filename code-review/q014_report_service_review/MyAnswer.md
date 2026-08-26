# Q014 — My Answer: Report Service

Write each issue below using the format from question.md.

0. constructor injectionof attributes is missing in controller and service
1. Report r = report.get(); id report is null report is set to APPROVED in any case
2. private Map<String, List<Report>> cache = new HashMap<>(); it should be Concurrent
3. should be using ?         String sql = "SELECT * FROM reports WHERE status = '" + status
                   + "' AND customer_id = '" + customerId + "'"; to avoid query injections
5. notificationClient.notify(report); should use sendNotification               


---

