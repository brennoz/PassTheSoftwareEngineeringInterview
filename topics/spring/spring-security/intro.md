# Spring Security

## What it is
Spring Security is Spring's authentication and authorisation framework. It intercepts every HTTP request via a **filter chain** before it reaches your controller. You configure who can access what, how identity is verified, and what to do when it fails — the framework handles the plumbing. Used in payment systems for: JWT token validation, role-based access to endpoints (`/admin/**`, `/payments/**`), and service-to-service mTLS.

---

## Core Concepts

### 1. Authentication vs Authorisation
| | Authentication | Authorisation |
|---|---|---|
| Question | Who are you? | What are you allowed to do? |
| Spring object | `Authentication` (in `SecurityContext`) | `GrantedAuthority` / `@PreAuthorize` |
| When it fails | 401 Unauthorized | 403 Forbidden |

### 2. SecurityFilterChain
Every request passes through a chain of `Filter` instances **before** reaching the `DispatcherServlet`.

```
HTTP Request
    ↓
[UsernamePasswordAuthenticationFilter]   ← handles /login form
[BearerTokenAuthenticationFilter]        ← handles JWT in Authorization header
[ExceptionTranslationFilter]             ← catches 401/403, returns correct response
[FilterSecurityInterceptor]              ← checks authorisation rules
    ↓
DispatcherServlet → @RestController
```

You configure this chain via `SecurityFilterChain` bean (Spring Boot 3+):

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)          // disable for stateless REST APIs
            .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))  // no server-side session for JWT
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()          // login/register public
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/payments/**").hasRole("USER")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

### 3. UserDetailsService
Interface Spring calls to load a user by username during authentication:

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPasswordHash())   // BCrypt hash — never plaintext
            .roles(user.getRole().name())
            .build();
    }
}
```

### 4. SecurityContext
After authentication, Spring stores the identity in a `ThreadLocal`:
```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
```
**Distributed pitfall:** `ThreadLocal` is per-thread, per-JVM. Fine for sync request processing. For reactive (WebFlux) or async processing — use `ReactiveSecurityContextHolder` or propagate explicitly.

---

## JWT Flow (stateless — standard for payment APIs)

```
Client → POST /api/auth/login { email, password }
Server → validates credentials → issues JWT (signed with secret/RSA key)
Client → every request: Authorization: Bearer <token>
Server → JwtAuthFilter: validate signature + expiry → extract claims → set SecurityContext
```

### JWT Filter implementation:
```java
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {

        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        String token = header.substring(7);
        String username = jwtService.extractUsername(token);   // parse JWT claims

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails user = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(token, user)) {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chain.doFilter(req, res);
    }
}
```

### JwtService (key operations):
```java
@Service
public class JwtService {

    @Value("${jwt.secret}")          // injected from env/secrets — never hardcode
    private String secret;

    public String generateToken(UserDetails user) {
        return Jwts.builder()
            .setSubject(user.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))  // 24h
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean isTokenValid(String token, UserDetails user) {
        String username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

---

## Common Patterns

### Pattern 1: Method-level security
```java
@Configuration
@EnableMethodSecurity   // enables @PreAuthorize, @PostAuthorize, @Secured
public class MethodSecurityConfig {}

@RestController
public class PaymentController {

    @PreAuthorize("hasRole('USER') and #accountId == authentication.name")
    @GetMapping("/accounts/{accountId}/balance")
    public BalanceDto getBalance(@PathVariable String accountId) { ... }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/accounts/{accountId}")
    public void closeAccount(@PathVariable String accountId) { ... }
}
```

### Pattern 2: Password encoding (BCrypt — never skip this)
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);  // cost factor 12 = ~250ms hash time, brute-force resistant
}

// Registration:
user.setPasswordHash(passwordEncoder.encode(rawPassword));

// Login check (Spring calls this internally):
passwordEncoder.matches(rawPassword, storedHash);
```
**Never store plain text passwords.** BCrypt includes salt internally — no separate salt column needed.

### Pattern 3: CORS for REST APIs (payment frontend + backend split)
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("https://payments.yourapp.com"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Idempotency-Key"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", config);
    return source;
}
```

---

## AWS / Distributed Context

| Concern | Single-node | AWS / Multi-node |
|---|---|---|
| JWT secret | `application.properties` | **AWS Secrets Manager** — never hardcode; rotate without redeploy |
| Token validation | In-process | Each ECS pod validates independently (stateless ✅) — no shared state needed |
| Token revocation | Not possible with pure JWT | Redis token blacklist (ElastiCache) — check on each request if token revoked |
| Session state | `ThreadLocal` / HTTP session | Must be `STATELESS` — no HTTP sessions on multi-pod (sessions not shared) |
| Service-to-service | N/A | mTLS + IAM roles — JWT for user-facing, AWS IAM for service-to-service |

**Payment domain:** JWT expiry should be short (15–60 min) for payment APIs. Refresh token pattern: short-lived access token + long-lived refresh token stored in HttpOnly cookie (not localStorage — XSS safe).

---

## Interview Angles

| Question | Answer |
|---|---|
| 401 vs 403? | 401 = not authenticated (no/bad token); 403 = authenticated but not authorised |
| Why disable CSRF for REST? | CSRF needs session cookie; stateless JWT APIs have no session → CSRF not applicable |
| Why BCrypt not SHA-256? | SHA-256 is fast → brute-force easy; BCrypt has configurable cost factor → slow by design |
| Where store JWT? | `Authorization: Bearer` header. Not localStorage (XSS risk). Refresh token in HttpOnly cookie. |
| Token revocation? | JWT is stateless — can't revoke without a blacklist. Use Redis blacklist or short expiry. |
| JWT secret rotation? | Store in AWS Secrets Manager. Use asymmetric RSA: rotate private key, all pods validate with public key. |
| `OncePerRequestFilter` vs `Filter`? | `OncePerRequestFilter` guaranteed once per request even with forward/include. Standard for auth filters. |
| `@PreAuthorize` vs `SecurityFilterChain`? | FilterChain = URL-level rules (coarse). `@PreAuthorize` = method-level (fine-grained, can check params). Use both. |

---

## Key Numbers
- BCrypt cost 12 → ~250ms → acceptable login latency, unacceptable brute-force throughput
- JWT typical expiry: 15min (access token) + 7–30 days (refresh token)
- `HS256` = symmetric (shared secret) — simple, but secret must never leak
- `RS256` = asymmetric (private sign / public verify) — safer for multi-service (services only need public key)
