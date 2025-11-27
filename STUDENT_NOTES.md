# 📚 LGVT User Service - Comprehensive Student Learning Notes

## Table of Contents
1. [Project Overview](#1-project-overview)
2. [Technology Stack](#2-technology-stack)
3. [Project Architecture](#3-project-architecture)
4. [Package Structure](#4-package-structure)
5. [Entity Layer](#5-entity-layer)
6. [Data Access Layer (DAO)](#6-data-access-layer-dao)
7. [Service Layer](#7-service-layer)
8. [REST Controllers](#8-rest-controllers)
9. [Security Configuration](#9-security-configuration)
10. [JWT Authentication](#10-jwt-authentication)
11. [Email Service](#11-email-service)
12. [File Upload with Cloudinary](#12-file-upload-with-cloudinary)
13. [Microservice Communication with OpenFeign](#13-microservice-communication-with-openfeign)
14. [Exception Handling](#14-exception-handling)
15. [Learning Path](#15-learning-path)

---

## 1. Project Overview

This microservice handles **user authentication, registration, and role management** with Multi-Factor Authentication (MFA) support. It's part of a larger microservices architecture for a voting system.

### Key Features:
- User Registration and Authentication
- Multi-Factor Authentication (MFA) via Email OTP
- Role-Based Access Control (VOTER, ADMIN, SUPER_ADMIN)
- Password Reset Functionality
- JWT Token-based Security
- File Upload to Cloudinary
- Email Notifications
- Admin Invitation System

---

## 2. Technology Stack

| Technology | Purpose |
|------------|---------|
| **Spring Boot 3.3.5** | Main framework for building the application |
| **Spring Security** | Authentication and authorization |
| **Spring Data JPA** | Database operations |
| **PostgreSQL** | Database |
| **JWT (jjwt)** | Token-based authentication |
| **Lombok** | Reducing boilerplate code |
| **Cloudinary** | Cloud-based image storage |
| **JavaMailSender** | Email notifications |
| **Spring Cloud OpenFeign** | Microservice communication |
| **Eureka Client** | Service discovery |

### Maven Dependencies Explained

```xml
<!-- Spring Boot Starters -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- Provides: REST API capabilities, embedded Tomcat server -->

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<!-- Provides: Authentication, Authorization, Security filters -->

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<!-- Provides: JPA/Hibernate for database operations -->

<!-- JWT Libraries -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<!-- Provides: JWT token creation and validation -->
```

---

## 3. Project Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT (Frontend)                         │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     REST Controllers Layer                       │
│  (VoterRestController, UserRestController, InvitationController) │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        Service Layer                             │
│    (VoterService, UserService, JwtService, EmailService, etc.)   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Data Access Layer (DAO)                       │
│         (VoterDAO, UserDAO, SecureTokenDAO, InvitationDAO)       │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                         Database                                 │
│                       (PostgreSQL)                               │
└─────────────────────────────────────────────────────────────────┘
```

---

## 4. Package Structure

```
com.lgvt.user_service/
├── UserServiceApplication.java    # Main entry point
├── config/                        # Configuration classes
│   └── CloudinaryConfig.java
├── dao/                           # Data Access Objects
│   ├── CidDocument.java
│   ├── InvitationDAO.java
│   ├── SecureTokenDAO.java
│   ├── UserDAO.java
│   ├── UserDAOImpl.java
│   ├── VoterDAO.java
│   └── VoterDAOImpl.java
├── dto/                           # Data Transfer Objects
│   └── AuditDto.java
├── entity/                        # JPA Entities
│   ├── Gender.java
│   ├── GeneralUser.java
│   ├── Invitation.java
│   ├── InvitationStatus.java
│   ├── Role.java
│   ├── SecureToken.java
│   ├── User.java
│   ├── UserStatus.java
│   └── Voter.java
├── exception/                     # Custom Exceptions
│   ├── CustomizedResponseEntityExceptionHandler.java
│   ├── ErrorDetails.java
│   └── UserAlreadyExistException.java
├── feign/                         # Feign Clients
│   └── AuditFeign.java
├── rest/                          # REST Controllers
│   ├── HelloController.java
│   ├── HomeController.java
│   ├── InvitationController.java
│   ├── UserRestcontroller.java
│   └── VoterRestController.java
├── Response/                      # Response DTOs
│   ├── ForgotPasswordResponse.java
│   ├── LoginResponse.java
│   ├── LoginUserInfo.java
│   └── VerifyForgotPasswordResponse.java
├── security/                      # Security Configuration
│   ├── CustomDetailsService.java
│   ├── CustomUserDetails.java
│   ├── JwtFilter.java
│   └── SecurityConfig.java
├── service/                       # Business Logic
│   ├── CloudinaryService.java
│   ├── EmailService.java
│   ├── EmailServiceImplementation.java
│   ├── InvitationService.java
│   ├── InvitationServiceImpl.java
│   ├── JwtService.java
│   ├── SecureTokenService.java
│   ├── SecureTokenServiceImplementation.java
│   ├── UserService.java
│   ├── UserServiceImpl.java
│   ├── VoterService.java
│   └── VoterServiceImpl.java
└── utils/                         # Utility Classes
    ├── AbstractEmailContext.java
    ├── AccountEmailContext.java
    ├── FileUploadUtil.java
    ├── ForgotPasswordContext.java
    ├── InvitationEmailContext.java
    └── MFAEmailContext.java
```

---

## 5. Entity Layer

### 5.1 Understanding JPA Entities

Entities are Java classes that map to database tables. They use annotations to define the mapping.

### 5.2 GeneralUser (Abstract Base Class)

```java
package com.lgvt.user_service.entity;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass  // This class won't have its own table, but its fields will be inherited
public abstract class GeneralUser {
    // Abstract methods that child classes must implement
    public abstract String getPassword();
    public abstract String getEmail();
    public abstract Role getRole();
    public abstract int getId();
    public abstract Object getName();
}
```

**Key Concepts:**
- `@MappedSuperclass`: Indicates this class is a parent class whose mapping information applies to its child entities
- Abstract methods define a contract for all user types

### 5.3 User Entity

```java
@Entity                          // Marks this class as a JPA entity
@Table(name = "users")           // Specifies the database table name
@Data                            // Lombok: generates getters, setters, toString, equals, hashCode
public class User extends GeneralUser {
    @Id                          // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment
    private int id;

    @Column(name = "name", nullable = true)
    @Size(min = 3, message = "Name must be at least 3 characters long")
    @NotBlank(message = "Name is required")
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Column(name = "phone", nullable = false)
    @Pattern(regexp = "^(77|17)\\d{6}$", message = "Phone number must have exactly 8 digits")
    @NotNull(message = "Phone number is required")
    private String phone;

    @Column(name = "password", nullable = false)
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @NotBlank(message = "Password is required")
    private String password;

    @Enumerated(EnumType.STRING)  // Store enum as string in database
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.ACTIVE;
}
```

**Key Annotations Explained:**
| Annotation | Purpose |
|------------|---------|
| `@Entity` | Marks class as database entity |
| `@Table` | Customizes table name |
| `@Id` | Marks primary key field |
| `@GeneratedValue` | Auto-generates ID values |
| `@Column` | Customizes column properties |
| `@Enumerated` | Maps enum to database |
| `@Size`, `@Email`, `@Pattern` | Validation constraints |

### 5.4 Voter Entity

```java
@Entity
@Table(name = "voter")
@Data
public class Voter extends GeneralUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // ... other fields similar to User ...

    @Column(name = "cid", nullable = false, unique = true)
    @Digits(integer = 11, fraction = 0, message = "CID must be exactly 11 digits")
    @NotNull(message = "CID is required")
    private String cid;

    @Embedded  // Embeds another class's fields into this table
    private CidDocument cid_document;

    @Column(name = "dob", nullable = false)
    @NotNull(message = "Date of birth is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    @Column(name = "gender", nullable = false)
    @NotNull(message = "Gender is required")
    private Gender gender;

    @Column(name = "is_verified", nullable = false)
    private boolean verified = false;

    @Column(name = "is_logged_in", nullable = false)
    private boolean logged_in = false;

    // Custom validation method
    @AssertTrue(message = "Voter must be at least 18 years old")
    public boolean isAdult() {
        return dob != null && Period.between(dob, LocalDate.now()).getYears() >= 18;
    }
}
```

**New Concepts:**
- `@Embedded`: Includes fields from another class (CidDocument) in this entity's table
- `@AssertTrue`: Custom validation that runs a method to check a condition

### 5.5 Embeddable Class (CidDocument)

```java
@Embeddable  // Can be embedded in other entities
@Data
@Builder     // Lombok: provides builder pattern
@NoArgsConstructor
@AllArgsConstructor
public class CidDocument {
    private String document_cloudinary_id;
    private String document_cloudinary_url;
}
```

### 5.6 Enums

```java
// Role.java - Defines user roles
public enum Role {
    VOTER,
    ADMIN,
    SUPER_ADMIN
}

// Gender.java
public enum Gender {
    MALE,
    FEMALE
}

// UserStatus.java
public enum UserStatus {
    ACTIVE,
    DISABLED
}

// InvitationStatus.java
public enum InvitationStatus {
    PENDING,
    ACCEPTED,
    ARCHIVED
}
```

### 5.7 SecureToken Entity (For OTP Verification)

```java
@Data
@Entity
@Table(name = "secureTokens")
public class SecureToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String token;

    @Column(updatable = false)
    @Basic(optional = false)
    private LocalDateTime expireAt;

    @ManyToOne  // Many tokens can belong to one voter
    @JoinColumn(name = "voter_id", referencedColumnName = "id")
    private Voter voter;

    @ManyToOne  // Many tokens can belong to one user
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false, length = 6)
    private int otp;  // One-Time Password for verification
}
```

**Key Concept - `@ManyToOne` Relationship:**
- One Voter/User can have many SecureTokens
- The `@JoinColumn` specifies the foreign key column

---

## 6. Data Access Layer (DAO)

The DAO layer handles all database operations. This project uses a custom DAO pattern with EntityManager instead of Spring Data JPA repositories.

### 6.1 DAO Interface (VoterDAO)

```java
public interface VoterDAO {
    Voter saveVoter(Voter voter);
    boolean checkIfUserExists(String email);
    boolean checkIfUserExistsById(int id);
    String sendRegistrationConfirmationEmail(Voter voter);
    String sendLoginMFAEmail(GeneralUser user);
    Voter changeVoterStatus(int id);
    Voter getVoterByEmail(String email);
    boolean checkIfPasswordMatches(String password, String oldPassword);
    void logoutVoter(Voter voter);
    void passwordReset(String password, Voter voter);
    long getTotalVoterCount();
    List<Voter> findAll();
    List<Voter> findByDzongkhagAndGewog(String dzongkhag, String gewog);
    void delete(Voter voter);
    Voter findById(int id);
}
```

### 6.2 DAO Implementation (VoterDAOImpl)

```java
@Repository  // Marks this as a Spring-managed repository bean
public class VoterDAOImpl implements VoterDAO {
    private EntityManager entityManager;  // JPA EntityManager for database operations
    private final BCryptPasswordEncoder passwordEncoder;  // For password hashing
    private EmailService emailService;
    private SecureTokenService secureTokenService;

    @Value("${app.base.url}")  // Injects value from application.properties
    private String baseUrl;

    @Autowired
    @Lazy  // Lazy initialization to avoid circular dependencies
    public VoterDAOImpl(EntityManager entityManager, BCryptPasswordEncoder passwordEncoder,
            SecureTokenService secureTokenService, EmailService emailService) {
        this.secureTokenService = secureTokenService;
        this.emailService = emailService;
        this.entityManager = entityManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Voter saveVoter(Voter voter) {
        // Encrypt password before saving
        voter.setPassword(passwordEncoder.encode(voter.getPassword()));
        return entityManager.merge(voter);  // Save or update entity
    }

    @Override
    public boolean checkIfUserExists(String email) {
        try {
            // JPQL Query - Java Persistence Query Language
            TypedQuery<Voter> query = entityManager.createQuery(
                "SELECT u FROM Voter u WHERE u.email = :email", Voter.class);
            query.setParameter("email", email);
            query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public Voter getVoterByEmail(String email) {
        try {
            TypedQuery<Voter> query = entityManager.createQuery(
                "SELECT v FROM Voter v WHERE v.email = :email", Voter.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @Transactional  // Ensures database operations are atomic
    public Voter changeVoterStatus(int id) {
        Voter existingVoter = entityManager.find(Voter.class, id);
        if (existingVoter != null) {
            existingVoter.setVerified(true);
            existingVoter.setLogged_in(true);
            return entityManager.merge(existingVoter);
        }
        return null;
    }

    @Override
    public boolean checkIfPasswordMatches(String password, String oldPassword) {
        // BCrypt comparison
        return passwordEncoder.matches(password, oldPassword);
    }

    @Override
    @Transactional
    public void passwordReset(String password, Voter voter) {
        String encryptedPassword = passwordEncoder.encode(password);
        voter.setPassword(encryptedPassword);
        entityManager.merge(voter);
    }

    @Override
    public long getTotalVoterCount() {
        String query = "SELECT COUNT(v) FROM Voter v";
        return entityManager.createQuery(query, Long.class).getSingleResult();
    }

    @Override
    public List<Voter> findByDzongkhagAndGewog(String dzongkhag, String gewog) {
        String query = "SELECT v FROM Voter v WHERE v.dzongkhag = :dzongkhag " +
                       "AND v.gewog = :gewog AND v.verified = true";
        return entityManager.createQuery(query, Voter.class)
                .setParameter("dzongkhag", dzongkhag)
                .setParameter("gewog", gewog)
                .getResultList();
    }
}
```

**Key Concepts:**
- **EntityManager**: JPA's primary interface for database operations
- **JPQL**: Java Persistence Query Language - SQL-like but uses entity names
- **@Transactional**: Ensures all operations complete or rollback together
- **BCryptPasswordEncoder**: Secure password hashing

### 6.3 Spring Data JPA Repository (SecureTokenDAO)

```java
// This uses Spring Data JPA's repository pattern
public interface SecureTokenDAO extends JpaRepository<SecureToken, Long> {
    SecureToken findByToken(final String token);  // Auto-generated query
    Long deleteByToken(final String token);

    @Modifying  // Indicates this query modifies data
    @Query("DELETE FROM SecureToken st WHERE st.voter.id = :voterId")
    void deleteTokensByVoterId(@Param("voterId") int voterId);
}
```

**Spring Data JPA Benefits:**
- Auto-generates queries from method names
- `findByToken` → `SELECT * FROM secure_tokens WHERE token = ?`
- Custom queries with `@Query` annotation

---

## 7. Service Layer

The service layer contains business logic and orchestrates between controllers and DAOs.

### 7.1 Service Interface (VoterService)

```java
public interface VoterService {
    String saveVoter(Voter voter, MultipartFile imageFile);
    boolean checkIfUserExistsById(int id);
    ResponseEntity<LoginResponse> loginVoter(Voter voter, HttpServletResponse response);
    ResponseEntity<String> logout(String email, HttpServletResponse response);
    ResponseEntity<ForgotPasswordResponse> forgotPassword(String email);
    ResponseEntity<String> resetPassword(String password, String token);
    ResponseEntity<String> resentOTP(String token, String type);
    ResponseEntity<Map<String, Object>> getVoterInfoByEmail(String email);
    ResponseEntity<String> updatePassword(String password, String email);
    String createSession(String email, HttpServletResponse response);
    void updateVoterInfoByEmail(String email, Map<String, String> updates);
}
```

### 7.2 Service Implementation (VoterServiceImpl)

```java
@Service  // Marks this as a service component
@Data
public class VoterServiceImpl implements VoterService {
    @Autowired
    private VoterDAO voterDAO;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private SecureTokenService secureTokenService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CustomDetailsService customUserDetailsService;
    @Autowired
    private AuditFeign auditFeign;

    @Override
    @Transactional
    public String saveVoter(@Valid Voter voter, MultipartFile imageFile) {
        // Step 1: Upload image to Cloudinary if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            final CidDocument cidDocument = uploadImage(voter.getId(), imageFile);
            voter.setCid_document(cidDocument);
        }

        // Step 2: Check if user already exists
        if (voterDAO.checkIfUserExists(voter.getEmail())) {
            throw new UserAlreadyExistException("This Voter already exists");
        }

        // Step 3: Save voter to database
        Voter voter_res = voterDAO.saveVoter(voter);

        // Step 4: Create audit log (using Feign client)
        AuditDto audit = new AuditDto(
                voter.getEmail(),
                "VOTER_CREATE",
                "Voter registered successfully",
                null,
                "SUCCESS"
        );
        auditFeign.createAudit(audit);

        // Step 5: Send verification email
        String token = voterDAO.sendRegistrationConfirmationEmail(voter_res);
        return token;
    }

    public ResponseEntity<LoginResponse> loginVoter(Voter voter, HttpServletResponse response) {
        Voter existingVoter = voterDAO.getVoterByEmail(voter.getEmail());

        if (existingVoter != null) {
            try {
                // Authenticate using Spring Security
                Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                        voter.getEmail(), 
                        voter.getPassword()
                    ));

                if (authentication.isAuthenticated()) {
                    if (existingVoter.isVerified()) {
                        LoginUserInfo userInfo = new LoginUserInfo(
                            existingVoter.getId(),
                            existingVoter.getEmail(),
                            existingVoter.getName(),
                            existingVoter.getRole().toString()
                        );

                        // Check if MFA is already completed
                        if (existingVoter.isLogged_in()) {
                            // Generate JWT token
                            UserDetails userDetails = customUserDetailsService
                                .loadUserByUsername(voter.getEmail());
                            String token = jwtService.generateToken(userDetails);

                            // Log successful authentication
                            AuditDto audit = new AuditDto(
                                voter.getEmail(),
                                "AUTH_SUCCESS",
                                "Voter Logged In successfully",
                                null,
                                "SUCCESS"
                            );
                            auditFeign.createAudit(audit);

                            return ResponseEntity.ok(new LoginResponse(
                                "Login successful",
                                token,
                                true,
                                "proceed",
                                userInfo
                            ));
                        } else {
                            // Send MFA email
                            String token = voterDAO.sendLoginMFAEmail(existingVoter);
                            return ResponseEntity.ok(new LoginResponse(
                                "Multifactor Authentication needed",
                                token,
                                false,
                                "redirect_to_mfa",
                                userInfo
                            ));
                        }
                    } else {
                        // User not verified
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new LoginResponse(
                                "User is not verified",
                                null,
                                false,
                                "verify_user",
                                null
                            ));
                    }
                }
            } catch (Exception ex) {
                // Authentication failed
                AuditDto audit = new AuditDto(
                    voter.getEmail(),
                    "AUTH_FAILURE",
                    "Incorrect password",
                    null,
                    "ERROR"
                );
                auditFeign.createAudit(audit);
                
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(
                        "Incorrect password",
                        null,
                        false,
                        "retry_login",
                        null
                    ));
            }
        }
        
        // User doesn't exist
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new LoginResponse(
                "User does not exist",
                null,
                false,
                "register_user",
                null
            ));
    }

    @Override
    public String createSession(String email, HttpServletResponse response) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        String token = jwtService.generateToken(userDetails);
        return token;
    }
}
```

**Key Concepts:**
- **@Service**: Marks class as a service layer component
- **@Transactional**: Database operations are atomic
- **AuthenticationManager**: Spring Security's authentication handler
- **ResponseEntity**: HTTP response wrapper with status codes

---

## 8. REST Controllers

Controllers handle HTTP requests and route them to appropriate services.

### 8.1 VoterRestController

```java
@RestController  // Combines @Controller and @ResponseBody
@RequestMapping("/api/auth")  // Base path for all endpoints
public class VoterRestController {
    private VoterService voterService;
    private SecureTokenService secureTokenService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    public VoterRestController(VoterService voterService, 
                               SecureTokenService secureTokenService,
                               UserService userService) {
        this.secureTokenService = secureTokenService;
        this.voterService = voterService;
    }

    // Register new voter with file upload
    @PostMapping(value = "/voter/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveVoter(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("cid") String cid,
            @RequestParam("phone") String phone,
            @RequestParam("dzongkhag") String dzongkhag,
            @RequestParam("gewog") String gewog,
            @RequestParam("village") String village,
            @RequestParam("dob") @JsonFormat(pattern = "yyyy-MM-dd") LocalDate dob,
            @RequestParam("gender") Gender gender,
            @RequestParam("occupation") String occupation,
            @RequestPart("file") MultipartFile file) {
        
        // Build voter object from request parameters
        Voter voter = new Voter();
        voter.setName(name);
        voter.setEmail(email);
        voter.setPassword(password);
        voter.setCid(cid);
        voter.setPhone(phone);
        voter.setDzongkhag(dzongkhag);
        voter.setGewog(gewog);
        voter.setVillage(village);
        voter.setDob(dob);
        voter.setGender(gender);
        voter.setOccupation(occupation);

        String token = voterService.saveVoter(voter, file);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Voter with CID " + voter.getCid() + " created.");
        response.put("token", token);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Verify OTP for registration
    @PostMapping("/voter/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOTP(
            @RequestParam("otp") int otp,
            @RequestParam("token") String token) {
        try {
            boolean isVerified = secureTokenService.verifyOtp(otp, token);
            if (isVerified) {
                secureTokenService.changeVoterStatus(token);
                secureTokenService.removeToken(token);
                return ResponseEntity.ok(Map.of("message", "OTP is verified"));
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid OTP"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }

    // Login endpoint
    @PostMapping("/voter/login")
    public ResponseEntity<?> login(@RequestBody Voter voter, 
                                   HttpServletResponse response) {
        return voterService.loginVoter(voter, response);
    }

    // Verify MFA OTP
    @PostMapping("/verify-login-otp")
    public ResponseEntity<LoginResponse> verifyLoginOTP(
            @RequestParam("otp") int otp,
            @RequestParam("token") String token,
            HttpServletResponse response) {
        try {
            boolean isVerified = secureTokenService.verifyOtp(otp, token);
            if (isVerified) {
                SecureToken secureToken = secureTokenDAO.findByToken(token);

                if (secureToken.getVoter() != null) {
                    secureTokenService.changeVoterLoginStatus(token);
                }

                String email = secureTokenService.getEmailFromToken(token);
                String sessionToken = voterService.createSession(email, response);
                secureTokenService.removeToken(sessionToken);

                LoginUserInfo userInfo;
                if (secureToken.getVoter() != null) {
                    userInfo = new LoginUserInfo(
                        secureToken.getVoter().getId(),
                        secureToken.getVoter().getEmail(),
                        secureToken.getVoter().getName(),
                        secureToken.getVoter().getRole().toString()
                    );
                } else {
                    userInfo = new LoginUserInfo(
                        secureToken.getUser().getId(),
                        secureToken.getUser().getEmail(),
                        secureToken.getUser().getName(),
                        secureToken.getUser().getRole().toString()
                    );
                }

                return ResponseEntity.ok(new LoginResponse(
                    "OTP verified successfully",
                    sessionToken,
                    true,
                    "proceed",
                    userInfo
                ));
            }
            return ResponseEntity.badRequest().body(new LoginResponse(
                "Invalid OTP", null, false, "retry", null
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new LoginResponse(e.getMessage(), null, false, "error", null));
        }
    }

    // Logout (requires authentication)
    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication, 
                                         HttpServletResponse response) {
        String email = authentication.getName();

        boolean isVoter = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("VOTER"));
        boolean isUser = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ADMIN") 
                        || a.getAuthority().equals("SUPER_ADMIN"));

        if (isVoter) {
            return voterService.logout(email, response);
        } else if (isUser) {
            return userService.logout(email, response);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body("Unauthorized user type");
    }

    // Forgot password
    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(
            @RequestBody Map<String, String> request) {
        String email = request.get("email");
        return voterService.forgotPassword(email);
    }

    // Get voter info (requires authentication)
    @GetMapping("/voter/info")
    public ResponseEntity<Map<String, Object>> getVoterInfo(
            Authentication authentication) {
        String email = authentication.getName();
        return voterService.getVoterInfoByEmail(email);
    }

    // Update voter info (requires VOTER role)
    @PutMapping("/voter/update-info")
    public ResponseEntity<Map<String, String>> updateVoterInfo(
            @RequestBody Map<String, String> updates,
            Authentication authentication) {
        String email = authentication.getName();
        try {
            voterService.updateVoterInfoByEmail(email, updates);
            return ResponseEntity.ok(Map.of("message", "Updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", e.getMessage()));
        }
    }
}
```

**Key Annotations:**
| Annotation | Purpose |
|------------|---------|
| `@RestController` | RESTful controller that returns JSON |
| `@RequestMapping` | Base URL path |
| `@PostMapping`, `@GetMapping`, `@PutMapping`, `@DeleteMapping` | HTTP methods |
| `@RequestParam` | URL query parameters |
| `@RequestBody` | JSON body parsing |
| `@RequestPart` | Multipart form data |
| `@PathVariable` | URL path variables |
| `Authentication` | Injected authenticated user info |

---

## 9. Security Configuration

### 9.1 SecurityConfig

```java
@Configuration
@EnableWebSecurity  // Enables Spring Security
public class SecurityConfig {
    @Autowired
    private CustomDetailsService customDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Password hashing
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) 
            throws Exception {
        httpSecurity
            .csrf(customizer -> customizer.disable())  // Disable CSRF for APIs
            .authorizeHttpRequests((requests) -> requests
                // Public endpoints - no authentication required
                .requestMatchers(
                    "/",
                    "/api/auth/voter/register",
                    "/api/auth/voter/login",
                    "/api/auth/verify-login-otp",
                    "/api/auth/voter/verify-otp",
                    "/api/auth/forgot-password",
                    "/api/auth/verify-forgot-password-otp",
                    "/api/auth/resent-otp",
                    "/api/auth/user/login",
                    "/api/auth/reset-password",
                    "/api/auth/super-admin/invitation/verify",
                    "/api/auth/super-admin/invitation/register",
                    "/api/auth/hello",
                    "/api/auth/exists/**",
                    "/api/auth/userexists/**"
                ).permitAll()
                
                // VOTER role required
                .requestMatchers(
                    "/api/auth/voter/update-password",
                    "api/auth/voter/update-info"
                ).hasAuthority("VOTER")
                
                // ADMIN role required
                .requestMatchers(
                    "/api/auth/super-user/register",
                    "/api/auth/admin/voters",
                    "/api/auth/admin/voter",
                    "/api/auth/admin/info",
                    "/api/auth/admin/statistics",
                    "/api/auth/admin/update-info"
                ).hasAuthority("ADMIN")
                
                // SUPER_ADMIN role required
                .requestMatchers(
                    "/api/auth/super-admin/info",
                    "/api/auth/super-admin/statistics",
                    "/api/auth/super-admin/invitation",
                    "/api/auth/super-admin/invitation/resent",
                    "/api/auth/super-admin/admins",
                    "/api/auth/super-admin/admin"
                ).hasAuthority("SUPER_ADMIN")
                
                // Multiple roles allowed
                .requestMatchers("/api/auth/user/reset-password")
                    .hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // No sessions
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) 
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        provider.setUserDetailsService(customDetailsService);
        return provider;
    }
}
```

### 9.2 CustomDetailsService

```java
@Service
public class CustomDetailsService implements UserDetailsService {
    @Autowired
    private VoterDAO voterDAO;
    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String email) 
            throws UsernameNotFoundException {
        // Try to find voter first
        Voter voter = voterDAO.getVoterByEmail(email);
        if (voter != null) {
            return new CustomUserDetails(voter);
        }

        // Try to find user
        User user = userDAO.getUserByEmail(email);
        if (user != null) {
            return new CustomUserDetails(user);
        }

        throw new UsernameNotFoundException("User not found: " + email);
    }
}
```

### 9.3 CustomUserDetails

```java
public class CustomUserDetails implements UserDetails {
    private GeneralUser user;

    public CustomUserDetails(GeneralUser user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return user's role as authority
        return Collections.singleton(
            new SimpleGrantedAuthority(user.getRole().name())
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
}
```

---

## 10. JWT Authentication

### 10.1 JwtService

```java
@Service
public class JwtService {
    private String secretKey = "thisismysecret19897donottouctouchit8329373743hhdjssmma89202";

    // Generate JWT token with roles
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        
        // Add roles to token claims
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .claims().add(claims)
                .subject(userDetails.getUsername())  // Email as subject
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 30))  // 30 hours
                .and()
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }
}
```

### 10.2 JwtFilter

```java
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain)
            throws ServletException, IOException {
        
        // Extract Authorization header
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userName = null;

        // Check for Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);  // Remove "Bearer " prefix
            userName = jwtService.extractUserName(token);
        }

        // Validate and set authentication
        if (userName != null && 
            SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Extract roles from JWT
            List<String> roles = jwtService.extractRoles(token);
            List<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // Create authentication token
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(userName, null, authorities);

            authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
            );
            
            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
```

**JWT Flow:**
1. User logs in with credentials
2. Server validates credentials
3. Server generates JWT with user info and roles
4. Client stores JWT (usually in localStorage)
5. Client sends JWT in Authorization header: `Bearer <token>`
6. JwtFilter extracts and validates token
7. If valid, sets authentication in SecurityContext
8. Request proceeds to controller

---

## 11. Email Service

### 11.1 EmailService Interface

```java
public interface EmailService {
    void sendMail(final AbstractEmailContext email) throws MessagingException;
    void sendMFAMail(final AbstractEmailContext email) throws MessagingException;
    void sendForgotPasswordMail(final AbstractEmailContext email) throws MessagingException;
    void sendInvitationMail(final AbstractEmailContext email) throws MessagingException;
}
```

### 11.2 EmailServiceImplementation

```java
@Service
public class EmailServiceImplementation implements EmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendMail(final AbstractEmailContext email) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(
            message, 
            MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
            StandardCharsets.UTF_8.name()
        );

        // Build HTML email content
        String emailContent = "<html><body>"
            + "<h1>Welcome, " + email.getContext().get("name") + " !</h1>"
            + "<p>Thank you for registering.</p>"
            + "<p>Your verification code is: <b>" + email.getOtp() + "</b></p>"
            + "</body></html>";

        messageHelper.setTo(email.getTo());
        messageHelper.setFrom(email.getFrom());
        messageHelper.setSubject(email.getSubject());
        messageHelper.setText(emailContent, true);  // true = HTML

        emailSender.send(message);
    }

    @Override
    public void sendMFAMail(final AbstractEmailContext email) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(
            message, 
            MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
            StandardCharsets.UTF_8.name()
        );

        String emailContent = "<html><body>"
            + "<h1>Multi-Factor Authentication (MFA)</h1>"
            + "<p>Dear " + email.getContext().get("name") + ",</p>"
            + "<p>Your verification code: <b>" + email.getOtp() + "</b></p>"
            + "<p>If you did not request this, please contact support.</p>"
            + "</body></html>";

        messageHelper.setTo(email.getTo());
        messageHelper.setFrom(email.getFrom());
        messageHelper.setSubject(email.getSubject());
        messageHelper.setText(emailContent, true);

        emailSender.send(message);
    }
}
```

### 11.3 Email Context Classes

```java
// Abstract base class
public abstract class AbstractEmailContext {
    protected String from;
    protected String to;
    protected String subject;
    protected int otp;
    private Map<String, Object> context;

    public AbstractEmailContext() {
        this.context = new HashMap<>();
    }

    public <T> void init(T context) {
        // Override in subclasses
    }

    public Object put(String key, Object value) {
        return key == null ? null : this.context.put(key, value.toString());
    }
    
    // Getters and setters...
}

// Account verification email context
public class AccountEmailContext extends AbstractEmailContext {
    private String token;

    @Override
    public <T> void init(T context) {
        GeneralUser user = (GeneralUser) context;
        put("name", user.getName());
        setSubject("Complete Your Registration by Entering the Code");
        setFrom("ryoutamikasa@gmail.com");
        setTo(user.getEmail());
    }

    public void setToken(String token) {
        this.token = token;
        put("token", token);
    }

    public void buildVerificationUrl(final String baseURL, String token) {
        final String url = UriComponentsBuilder.fromUriString(baseURL)
                .path("/register/verify")
                .queryParam("token", token)
                .toUriString();
        put("verificationURL", url);
    }
}
```

### 11.4 Email Configuration (application.properties)

```properties
# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## 12. File Upload with Cloudinary

### 12.1 CloudinaryConfig

```java
@Configuration
public class CloudinaryConfig {
    @Value("${cloudinary.cloud_name}")
    private String cloudName;

    @Value("${cloudinary.api_key}")
    private String apiKey;

    @Value("${cloudinary.api_secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        final Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        return new Cloudinary(config);
    }
}
```

### 12.2 CloudinaryService

```java
@Service
@Data
public class CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;

    @Transactional
    public CidDocument uploadFile(final MultipartFile file, final String fileName) {
        try {
            // Upload to Cloudinary
            final Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                Map.of("public_id", "lgvt/cid/" + fileName)
            );
            
            // Extract URL and ID from response
            final String url = (String) uploadResult.get("url");
            final String publicId = (String) uploadResult.get("public_id");

            return CidDocument.builder()
                    .document_cloudinary_url(url)
                    .document_cloudinary_id(publicId)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image");
        }
    }
}
```

### 12.3 FileUploadUtil

```java
@UtilityClass  // Lombok utility class
public class FileUploadUtil {
    public static final long MAX_FILE_SIZE = 1024 * 1024 * 5; // 5MB
    public static final String IMAGE_PATTERN = ".*\\.(jpg|jpeg|png)$";
    public static final String DATE_FORMAT = "yyyyMMddHHmmss";

    public static boolean isAllowedExtension(String fileName, String pattern) {
        final Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)
            .matcher(fileName);
        return matcher.matches();
    }

    public static void assertAllowed(MultipartFile file, String pattern) {
        final long size = file.getSize();
        if (size > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 5MB limit");
        }

        final String fileName = file.getOriginalFilename();
        if (!isAllowedExtension(fileName, pattern)) {
            throw new IllegalArgumentException("File type not allowed");
        }
    }

    public static String getFileName(final String name) {
        final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        final String date = dateFormat.format(System.currentTimeMillis());
        return String.format("%S_%S", name, date);
    }
}
```

---

## 13. Microservice Communication with OpenFeign

OpenFeign allows you to call other microservices using simple interface declarations.

### 13.1 Enable Feign Clients

```java
@SpringBootApplication
@EnableFeignClients  // Enable Feign in application
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
```

### 13.2 AuditFeign Interface

```java
@FeignClient(name = "AUDIT-LOG-SERVICE")  // Service name in Eureka
public interface AuditFeign {
    @GetMapping("/api/audits")
    public ResponseEntity<List<AuditDto>> getAllAudits();

    @PostMapping("/api/audits")
    public ResponseEntity<AuditDto> createAudit(@RequestBody AuditDto audit);
}
```

### 13.3 Using Feign Client

```java
@Service
public class VoterServiceImpl implements VoterService {
    @Autowired
    private AuditFeign auditFeign;  // Inject Feign client

    @Override
    public String saveVoter(Voter voter, MultipartFile imageFile) {
        // ... save voter logic ...

        // Call audit service via Feign
        AuditDto audit = new AuditDto(
            voter.getEmail(),
            "VOTER_CREATE",
            "Voter registered successfully",
            null,
            "SUCCESS"
        );
        auditFeign.createAudit(audit);  // HTTP POST to audit service

        return token;
    }
}
```

### 13.4 AuditDto

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditDto {
    private String user_email;
    private String action;
    private String description;
    private String ipAddress;
    private String status;
}
```

---

## 14. Exception Handling

### 14.1 Global Exception Handler

```java
@ControllerAdvice  // Global exception handling
public class CustomizedResponseEntityExceptionHandler 
        extends ResponseEntityExceptionHandler {
    
    // Handle all exceptions
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorDetails> handleAllException(
            Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            ex.getMessage(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle validation errors
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            ex.getFieldError().getDefaultMessage(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
```

### 14.2 ErrorDetails

```java
public class ErrorDetails {
    private LocalDateTime timestamp;
    private String message;
    private String details;

    public ErrorDetails(LocalDateTime timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

    // Getters and setters...
}
```

### 14.3 Custom Exception

```java
public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
```

---

## 15. Learning Path

### Step 1: Prerequisites
- Java 17+ basics
- Maven basics
- REST API concepts
- SQL basics

### Step 2: Core Spring Boot Concepts
1. Study Spring Boot annotations (@Component, @Service, @Repository, @Controller)
2. Understand dependency injection (@Autowired)
3. Learn about application.properties configuration

### Step 3: Database Layer
1. Study JPA entities and annotations
2. Understand EntityManager vs Spring Data JPA repositories
3. Learn JPQL query syntax
4. Practice CRUD operations

### Step 4: Service Layer
1. Understand service layer responsibilities
2. Learn transaction management (@Transactional)
3. Study business logic patterns

### Step 5: REST Controllers
1. Study HTTP methods (GET, POST, PUT, DELETE)
2. Learn request/response handling
3. Understand path variables and request parameters
4. Practice building REST endpoints

### Step 6: Security
1. Study Spring Security basics
2. Learn about authentication vs authorization
3. Understand JWT tokens
4. Practice implementing security filters

### Step 7: Advanced Topics
1. Email integration
2. File uploads (Cloudinary)
3. Microservice communication (Feign)
4. Exception handling

### Practice Projects
1. **Simple CRUD API**: Create a basic entity with CRUD operations
2. **User Authentication**: Implement login/register with JWT
3. **Role-Based Access**: Add roles and secure endpoints
4. **Email Verification**: Send OTP emails
5. **File Upload**: Integrate Cloudinary

---

## API Endpoints Reference

### Public Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/voter/register` | Register new voter |
| POST | `/api/auth/voter/login` | Voter login |
| POST | `/api/auth/user/login` | Admin/SuperAdmin login |
| POST | `/api/auth/voter/verify-otp` | Verify registration OTP |
| POST | `/api/auth/verify-login-otp` | Verify MFA OTP |
| POST | `/api/auth/forgot-password` | Request password reset |
| POST | `/api/auth/reset-password` | Reset password |
| POST | `/api/auth/resent-otp` | Resend OTP |
| GET | `/api/auth/exists/{id}` | Check if voter exists |

### Voter Endpoints (Requires VOTER role)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/auth/voter/info` | Get voter profile |
| POST | `/api/auth/voter/update-password` | Update password |
| PUT | `/api/auth/voter/update-info` | Update profile |

### Admin Endpoints (Requires ADMIN role)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/auth/admin/voters` | List voters in jurisdiction |
| DELETE | `/api/auth/admin/voter/{id}` | Delete voter |
| GET | `/api/auth/admin/info` | Get admin profile |
| GET | `/api/auth/admin/statistics` | Get voter statistics |
| PUT | `/api/auth/admin/update-info` | Update admin profile |

### Super Admin Endpoints (Requires SUPER_ADMIN role)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/super-admin/invitation` | Send admin invitation |
| POST | `/api/auth/super-admin/invitation/resent` | Resend invitation |
| GET | `/api/auth/super-admin/admins` | List all admins |
| DELETE | `/api/auth/super-admin/admin` | Delete/disable admin |
| GET | `/api/auth/super-admin/info` | Get super admin profile |
| GET | `/api/auth/super-admin/statistics` | Get system statistics |

---

## Common Patterns in This Project

### 1. Interface-Implementation Pattern
- Define interface (e.g., `VoterService`)
- Create implementation (e.g., `VoterServiceImpl`)
- Benefits: Loose coupling, testability

### 2. DTO Pattern
- Use DTOs for API responses (e.g., `LoginResponse`)
- Separates internal entities from API contracts

### 3. Builder Pattern
- Used with Lombok's `@Builder` (e.g., `CidDocument`)
- Clean object creation

### 4. Repository Pattern
- DAOs abstract database access
- Service layer uses DAOs, not direct DB calls

### 5. Filter Chain Pattern
- JwtFilter in security chain
- Processes requests before controllers

---

## Tips for Students

1. **Start Small**: Begin with simple CRUD before adding security
2. **Use Debugging**: Add `System.out.println` statements to understand flow
3. **Read Logs**: Spring Boot logs show what's happening
4. **Test with Postman**: Use Postman to test APIs
5. **Understand Errors**: Read stack traces carefully
6. **Version Control**: Use Git to track changes
7. **Documentation**: Comment complex logic

---

## Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [JWT.io](https://jwt.io/) - JWT Debugger
- [Baeldung](https://www.baeldung.com/) - Spring Tutorials
- [Spring Initializr](https://start.spring.io/) - Project Generator

---

**Happy Learning! 🚀**
