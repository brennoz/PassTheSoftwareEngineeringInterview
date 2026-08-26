# Q012 — My Answer: User Registration Service

Write each issue below using the format from question.md.

1. UserService must be injected via constructor in the controller and service
2. in the controller the search requires a pagination otherwise if there are huge number of users with that name it could cause slowness or delays in the workflow
3. response is always ok also if user registration falis in the controller
4. In UserService the password must not be printed in the logs for security reasons
5. Slef4j is never used in the controller
6. userRepo.save should be done once  only in the end of register method
7. is missing a check for email already existing
8. register needs to be done in a transaction

---

