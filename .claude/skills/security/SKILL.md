---
name: security
description: >
  Perform security hardening, audit code for vulnerabilities, and generate secure implementations
  for Spring Boot + Angular projects following OWASP Top 10, secure-by-default patterns, and the
  HomeFlex security architecture (httpOnly cookies, CSRF, AES-256-GCM PII encryption, Redis rate
  limiting). Trigger this skill whenever the user asks to: "secure X", "harden X", "add auth to X",
  "check for vulnerabilities", "security review", "add CSRF protection", "encrypt X", "rate-limit X",
  "add input validation to X", "prevent XSS/SQLi/CSRF", or "make this production-ready". Always apply
  this — never freehand security-sensitive code.
---

# Security Skill — Spring Boot 4 + Angular 21

## Architecture Security Baseline (HomeFlex)

| Layer         | Mechanism                                                              |
| ------------- | ---------------------------------------------------------------------- |
| Auth tokens   | httpOnly + Secure + SameSite=Lax cookies (no localStorage)            |
| CSRF          | `CookieCsrfTokenRepository` + `SpaCsrfTokenRequestHandler`            |
| PII at rest   | AES-256-GCM with per-record random IVs (`PiiEncryptionService`)        |
| Rate limiting | Redis Lua INCR+EXPIRE — 100 req/min (auth), 20 req/min (public)        |
| Authorization | Spring Security `@PreAuthorize` + method-level RBAC                    |
| Passwords     | BCrypt (strength 12)                                                   |
| Secrets       | Environment variables only — never hardcoded defaults for sensitive values |
| CSP           | Strict Content-Security-Policy via Nginx `add_header ... always`       |
| IP resolution | Last `X-Forwarded-For` entry (Nginx-appended), never the first        |

---

## 1. Authentication & Authorization

### Secure JWT Cookie Setup (Spring Boot)

```java
// In AuthService — set tokens as httpOnly cookies, never in response body
private void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
    ResponseCookie access = ResponseCookie.from(jwtCookieName, accessToken)
        .httpOnly(true)
        .secure(jwtCookieSecure)          // true in prod (env var)
        .sameSite("Lax")
        .path("/")
        .maxAge(Duration.ofSeconds(900))  // 15 min
        .build();

    ResponseCookie refresh = ResponseCookie.from(refreshCookieName, refreshToken)
        .httpOnly(true)
        .secure(jwtCookieSecure)
        .sameSite("Lax")
        .path("/api/v1/auth/refresh")    // scope refresh token to refresh endpoint only
        .maxAge(Duration.ofDays(7))
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, access.toString());
    response.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());
}
```

### Controller-Level Authorization

Always use `@PreAuthorize` — never rely on client-side guards alone:

```java
// WRONG — relies only on URL pattern in SecurityConfig
@GetMapping("/admin/users")
public ResponseEntity<Page<UserDto>> listUsers(...) { ... }

// CORRECT — explicit method-level enforcement
@GetMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Page<UserDto>> listUsers(...) { ... }
```

### Ownership Enforcement Pattern

Every resource must be verified for ownership before returning or mutating:

```java
@Transactional(readOnly = true)
public BookingDto getBookingById(UUID bookingId, UUID requestingUserId) {
    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

    boolean isOwner   = booking.getTenantId().equals(requestingUserId);
    boolean isLandlord = booking.getProperty().getOwnerId().equals(requestingUserId);
    boolean isAdmin   = securityContext.hasRole("ADMIN");

    if (!isOwner && !isLandlord && !isAdmin) {
        throw new AccessDeniedException("Access denied to booking " + bookingId);
    }
    return bookingMapper.toDto(booking);
}
```

---

## 2. CSRF Protection (Spring Boot + Angular SPA)

### Backend Config

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Angular reads it
        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())           // SPA double-submit
    );
    return http.build();
}
```

### Angular Interceptor

```typescript
// Already wired in app.config.ts — every mutating request sends X-XSRF-TOKEN
const credentialsInterceptor: HttpInterceptorFn = (req, next) => {
  const mutating = ['POST','PUT','PATCH','DELETE'].includes(req.method);
  let headers = req.headers;
  const token = readCookie('XSRF-TOKEN');
  if (mutating && token) {
    headers = headers.set('X-XSRF-TOKEN', token);
  }
  return next(req.clone({ withCredentials: true, headers }));
};
```

---

## 3. Input Validation

### Bean Validation on DTOs (Spring Boot)

```java
public record CreatePropertyRequest(
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be under 200 characters")
    String title,

    @NotNull @Positive(message = "Price must be positive")
    BigDecimal pricePerNight,

    @NotBlank @Pattern(regexp = "^[a-zA-Z\\s-]{2,100}$", message = "Invalid city")
    String city,

    @Valid @NotNull
    AddressDto address   // nested validation
) {}
```

### Controller — Always Validate

```java
@PostMapping
public ResponseEntity<PropertyDto> create(
    @Valid @RequestBody CreatePropertyRequest request,  // @Valid triggers Bean Validation
    Authentication auth
) { ... }
```

### Angular — Reactive Form Validation

```typescript
form = this.fb.group({
  title: ['', [Validators.required, Validators.maxLength(200)]],
  price: [null, [Validators.required, Validators.min(0.01)]],
  city:  ['', [Validators.required, Validators.pattern(/^[a-zA-Z\s-]{2,100}$/)]],
});

// Show errors only after user interaction (touched)
get titleError(): string | null {
  const c = this.form.get('title');
  if (!c?.touched || !c.errors) return null;
  if (c.errors['required']) return 'Title is required';
  if (c.errors['maxlength']) return 'Title must be under 200 characters';
  return null;
}
```

---

## 4. PII Encryption (AES-256-GCM)

### Encrypting Sensitive Fields

Use `@Convert` + `PiiEncryptionService` — never store plaintext PII:

```java
// Entity
@Column(name = "phone_number")
@Convert(converter = PiiAttributeConverter.class)
private String phoneNumber;

// Converter
@Component
public class PiiAttributeConverter implements AttributeConverter<String, String> {
    @Autowired private PiiEncryptionService pii;

    @Override public String convertToDatabaseColumn(String plain) {
        return plain == null ? null : pii.encrypt(plain);
    }
    @Override public String convertToEntityAttribute(String cipher) {
        return cipher == null ? null : pii.decrypt(cipher);
    }
}
```

### PiiEncryptionService (AES-256-GCM)

```java
@Service
public class PiiEncryptionService {

    private final SecretKey key;

    public PiiEncryptionService(@Value("${app.security.pii-encryption-key}") String b64Key) {
        this.key = new SecretKeySpec(Base64.getDecoder().decode(b64Key), "AES");
    }

    public String encrypt(String plaintext) {
        try {
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] combined = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new EncryptionException("PII encryption failed", e);
        }
    }

    public String decrypt(String encoded) {
        try {
            byte[] combined = Base64.getDecoder().decode(encoded);
            byte[] iv         = Arrays.copyOfRange(combined, 0, 12);
            byte[] ciphertext = Arrays.copyOfRange(combined, 12, combined.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new EncryptionException("PII decryption failed", e);
        }
    }
}
```

---

## 5. Rate Limiting (Redis Lua)

### Rate Limit Filter Pattern

```java
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redis;

    private static final String RATE_LIMIT_SCRIPT = """
        local key = KEYS[1]
        local limit = tonumber(ARGV[1])
        local window = tonumber(ARGV[2])
        local current = redis.call('INCR', key)
        if current == 1 then
            redis.call('EXPIRE', key, window)
        end
        return current
        """;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String key  = buildKey(req);
        int limit   = isAuthenticated(req) ? 100 : 20;
        int window  = 60; // seconds

        Long count = redis.execute(
            RedisScript.of(RATE_LIMIT_SCRIPT, Long.class),
            List.of(key), String.valueOf(limit), String.valueOf(window));

        if (count != null && count > limit) {
            res.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            res.setHeader("Retry-After", String.valueOf(window));
            return;
        }
        chain.doFilter(req, res);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            // Use LAST entry — set by Nginx, not spoofable by the client
            String[] parts = xff.split(",");
            return parts[parts.length - 1].trim();
        }
        return request.getRemoteAddr();
    }
}
```

---

## 6. File Upload Security

### Secure Upload Pattern

```java
private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
    "image/jpeg", "image/png", "image/webp", "image/gif"
);
private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

public String uploadFile(MultipartFile file, String folder) {
    // 1. Size check
    if (file.getSize() > MAX_FILE_SIZE) {
        throw new ValidationException("File exceeds 10 MB limit");
    }

    // 2. MIME type whitelist (Content-Type header)
    String contentType = file.getContentType();
    if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
        throw new ValidationException("Unsupported file type: " + contentType);
    }

    // 3. Magic-number validation (TODO: Apache Tika — planned)
    // byte[] magic = file.getBytes(); Tika.detect(magic) must match contentType

    // 4. UUID filename — never use original filename on the server
    String ext = contentType.substring(contentType.lastIndexOf('/') + 1);
    String key = folder + "/" + UUID.randomUUID() + "." + ext;

    // 5. Upload
    return storageClient.upload(key, file.getInputStream(), contentType);
}
```

---

## 7. Secrets Management

### Rules

- **Never** use `${VAR:default}` syntax for secrets. No fallback → startup fails loud.
- Secrets required at startup: `JWT_SECRET`, `PII_ENCRYPTION_KEY`, `ADMIN_PASSWORD`, `MAIL_PASSWORD`, `ELASTIC_PASSWORD`.
- Dev secrets live in `rental-backend/.env` (git-ignored). Template at `rental-backend/.env.example`.
- Production: inject from AWS Secrets Manager / Docker secrets / CI environment.

```yaml
# WRONG
jwt:
  secret: ${JWT_SECRET:insecure-dev-default}

# CORRECT — fails fast if not set
jwt:
  secret: ${JWT_SECRET}
```

### Test Users Are Dev-Only

```java
// CORRECT — DataInitializer only runs in non-prod profiles
@Component
@Profile("!prod")
public class DataInitializer implements CommandLineRunner { ... }
```

---

## 8. Timing-Safe Comparisons

Use `MessageDigest.isEqual()` for all secret/token comparisons — never `String.equals()`:

```java
// WRONG — vulnerable to timing attacks
if (token.equals(expectedToken)) { ... }

// CORRECT — constant-time
if (MessageDigest.isEqual(token.getBytes(), expectedToken.getBytes())) { ... }
```

---

## 9. User Enumeration Prevention

Always return the same response whether the user exists or not:

```java
// WRONG — leaks user existence
public void sendPasswordResetEmail(String email) {
    User user = repo.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found")); // different response!
    ...
}

// CORRECT — silent no-op for unknown emails
public void sendPasswordResetEmail(String email) {
    User user = repo.findByEmail(email).orElse(null);
    if (user == null) return; // same 200 response as success
    ...
}
```

---

## 10. Nginx Security Headers

All responses (including error pages) must include security headers. Use `always`:

```nginx
# Security Headers
add_header X-Frame-Options "SAMEORIGIN" always;
add_header X-Content-Type-Options "nosniff" always;
add_header X-XSS-Protection "1; mode=block" always;
add_header Referrer-Policy "strict-origin-when-cross-origin" always;
add_header Content-Security-Policy "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; font-src 'self' https://fonts.gstatic.com data:; img-src 'self' data: https:; connect-src 'self'; frame-ancestors 'none'; base-uri 'self'; form-action 'self';" always;
add_header Permissions-Policy "geolocation=(), microphone=(), camera=()" always;
```

---

## OWASP Top 10 Checklist

| # | Risk                               | Mitigation in this stack                                      | Status |
|---|-----------------------------------|---------------------------------------------------------------|--------|
| A01 | Broken Access Control           | `@PreAuthorize` + ownership checks on every resource          | 🟢     |
| A02 | Cryptographic Failures          | AES-256-GCM PII, BCrypt passwords, TLS (prod ALB)            | 🟢     |
| A03 | Injection                       | JPA parameterized queries; Jakarta validation on DTOs         | 🟢     |
| A04 | Insecure Design                 | Layered architecture, no controller→repository, RBAC          | 🟢     |
| A05 | Security Misconfiguration       | No hardcoded defaults; dev profile gated; Swagger prod-off   | 🟢     |
| A06 | Vulnerable Components           | Dependabot planned (SEC-10)                                   | 🔴     |
| A07 | Auth & Session Failures         | httpOnly cookies, refresh rotation, logout clears DB token   | 🟢     |
| A08 | Software & Data Integrity       | Stripe webhook idempotency; outbox relay; Flyway migrations   | 🟢     |
| A09 | Security Logging & Monitoring   | ELK + Prometheus; security event logging planned (SEC-06)    | 🟡     |
| A10 | SSRF                            | No user-controlled URL fetching; S3 presigned via UUID key   | 🟢     |

---

## Security Code Review Checklist

Before marking any PR as ready, verify:

- [ ] No hardcoded secrets, passwords, or API keys (use env vars with no fallback for sensitive values)
- [ ] All controller endpoints have `@PreAuthorize` or explicit security config
- [ ] Ownership verified in service layer before returning or mutating resources
- [ ] Password reset / OTP flows do not leak user existence (same response for hit/miss)
- [ ] File uploads validate MIME type whitelist + size limit
- [ ] Token/secret comparisons use `MessageDigest.isEqual()` (constant-time)
- [ ] Rate-limit IP resolution uses last `X-Forwarded-For` entry, not first
- [ ] `DataInitializer` or any seed class annotated `@Profile("!prod")`
- [ ] No test/dummy OAuth bypass code paths in production
- [ ] Swagger/OpenAPI disabled by default; enabled only in `dev` profile
- [ ] PII fields use `@Convert(converter = PiiAttributeConverter.class)`
- [ ] Angular forms use `withCredentials: true` + `X-XSRF-TOKEN` interceptor
- [ ] Nginx includes all security headers with `always` flag
- [ ] `.env` is git-ignored; `.env.example` template is committed
