# Q012 — Spot the Issues: User Registration Service

**Category:** Code Review  
**Difficulty:** Medium  
**Ideal Time:** 15 min

---

## Task

This user management service is being deployed to production. Find all issues — security holes, Spring problems, Java bugs, data integrity risks, anything that would get flagged in a real code review.

---

## Code

```java
// UserController.java
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/search")
    public List<User> search(@RequestParam String name) {
        return userService.searchByName(name);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }
}

// UserService.java
@Service
@Slf4j
public class UserService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public List<User> searchByName(String name) {
        String sql = "SELECT * FROM users WHERE name = '" + name + "'";
        return entityManager.createNativeQuery(sql, User.class).getResultList();
    }

    public User findById(Long id) {
        return userRepository.findById(id).get();
    }

    public User register(UserRequest request) {
        log.info("New registration: email={}, password={}", request.getEmail(), request.getPassword());

        User user = new User(request.getEmail(), request.getPassword());
        userRepository.save(user);

        Role role = roleRepository.findByName("USER");
        user.setRole(role);
        userRepository.save(user);

        return user;
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

Categories: Security | Spring anti-pattern | Java bug | Data integrity | API design
