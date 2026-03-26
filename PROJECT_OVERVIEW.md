# BOCRA Hackathon Backend - Project Overview

## Project Summary

**Project Name:** BOCRA Hackathon Backend  
**Version:** 0.0.1-SNAPSHOT  
**Organization:** bw.org.bocra (Botswana Communications Regulatory Authority)  
**Description:** Spring Boot API for BOCRA public services and secure admin operations

---

## Technology Stack

| Category | Technology |
|----------|------------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.4 |
| Database | PostgreSQL |
| ORM | Spring Data JPA + Hibernate |
| Migration | Flyway |
| Security | Spring Security + JWT |
| Rate Limiting | Bucket4j |
| Monitoring | Spring Actuator + Prometheus |
| Build Tool | Maven |
| Container | Docker |

---

## Architecture

### Layer Structure

```
src/main/java/bw/org/bocra/backend/
├── HackathonApplication.java       # Main entry point
├── config/                        # Configuration classes
│   ├── AdminUserBootstrap.java    # Initial admin user setup
│   └── SecurityConfig.java        # Security configuration
├── controller/                    # REST API controllers
│   ├── AdminController.java       # Admin operations
│   ├── AuthController.java        # Authentication
│   ├── ComplaintController.java   # Public complaint submission
│   ├── CyberIncidentController.java
│   ├── DocumentController.java
│   ├── InquiryController.java
│   ├── NewsArticleController.java
│   ├── SearchController.java
│   ├── StatController.java
│   └── TenderController.java
├── dto/                           # Data Transfer Objects
│   ├── AdminProfileResponse.java
│   ├── AuthLoginRequest.java
│   └── AuthLoginResponse.java
├── exception/                     # Exception handling
│   └── GlobalExceptionHandler.java
├── model/                         # Entity models
│   ├── AdminUser.java
│   ├── AuditLog.java
│   ├── Complaint.java
│   ├── CyberIncident.java
│   ├── Document.java
│   ├── Inquiry.java
│   ├── NewsArticle.java
│   ├── Stat.java
│   └── Tender.java
├── repository/                    # Data repositories
│   ├── AdminUserRepository.java
│   ├── AuditLogRepository.java
│   ├── ComplaintRepository.java
│   ├── CyberIncidentRepository.java
│   ├── DocumentRepository.java
│   ├── InquiryRepository.java
│   ├── NewsRepository.java
│   ├── StatRepository.java
│   └── TenderRepository.java
├── security/                      # Security components
│   ├── CaptchaVerificationService.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtUtil.java
│   ├── RateLimitFilter.java
│   ├── RequestIpResolver.java
│   └── SensitiveStringEncryptor.java
└── service/                       # Business logic
    ├── AuditLogService.java
    ├── AuthService.java
    ├── ComplaintService.java
    ├── CyberIncidentService.java
    ├── InputSanitizer.java
    ├── InquiryService.java
    ├── NewsService.java
    └── SearchService.java
```

---

## API Endpoints

### Public Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/login` | User authentication |
| GET | `/api/v1/auth/me` | Get current user |
| POST | `/api/v1/auth/logout` | User logout |
| POST | `/api/v1/complaints` | Submit complaint |
| GET | `/api/v1/complaints/{ticketId}` | Track complaint |
| POST | `/api/v1/inquiries` | Submit inquiry |
| POST | `/api/v1/cyber-incidents` | Report cyber incident |
| GET | `/api/v1/cyber-incidents/{incidentId}` | Track incident |
| GET | `/api/v1/news` | List news articles |
| GET | `/api/v1/tenders` | List tenders |
| GET | `/api/v1/documents` | List documents |
| GET | `/api/v1/stats` | Get statistics |
| GET | `api/v1/search` | Search content |

### Admin Endpoints (ROLE_ADMIN)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/admin/complaints` | List all complaints |
| GET | `/api/v1/admin/inquiries` | List all inquiries |
| GET | `/api/v1/admin/cyber-incidents` | List all incidents |
| GET | `/api/v1/admin/audit-logs` | View audit logs |
| POST | `/api/v1/news` | Create news article |
| POST | `/api/v1/tenders` | Create tender |
| POST | `/api/v1/documents` | Upload document |

---

## Data Models

### Core Entities

1. **AdminUser** - System administrators with role-based access
2. **Complaint** - Public complaint submissions with ticket tracking
3. **CyberIncident** - Cyber incident reports
4. **Inquiry** - Public inquiries
5. **NewsArticle** - News content management
6. **Tender** - Procurement tenders
7. **Document** - Document repository
8. **Stat** - Statistics data
9. **AuditLog** - Security audit trail

---

## Security Features

### Authentication & Authorization
- JWT (JSON Web Token) based authentication
- Role-Based Access Control (RBAC)
- Login lockout after failed attempts

### Rate Limiting
- Bucket4j-based rate limiting
- Configurable limits for:
  - Login attempts
  - Public submission endpoints

### Additional Security
- Optional CAPTCHA verification (Cloudflare Turnstile compatible)
- Sensitive field encryption for PII
- Strict security headers (HSTS, CSP, X-Frame-Options)
- CORS allow-list configuration
- CSRF protection enabled

---

## Database Migrations

| Version | Description |
|---------|-------------|
| V1 | Initial schema |
| V2 | Seed reference data |
| V3 | Security hardening + audit log |
| V4 | Seed operational demo data |

---

## Configuration

### Environment Variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `PORT` | No | 8083 | Server port |
| `JDBC_DATABASE_URL` | Yes | - | PostgreSQL URL |
| `JDBC_DATABASE_USERNAME` | No | postgres | DB username |
| `JDBC_DATABASE_PASSWORD` | Yes | - | DB password |
| `APP_JWT_SECRET` | Yes | - | JWT signing secret |
| `APP_JWT_EXPIRATION_MS` | No | 3600000 | Token expiration |
| `APP_CORS_ALLOWED_ORIGINS` | No | localhost:3000 | CORS origins |
| `APP_ADMIN_BOOTSTRAP_USERNAME` | No | admin | Initial admin |
| `APP_ADMIN_BOOTSTRAP_PASSWORD` | Yes | - | Initial admin password |
| `APP_CAPTCHA_ENABLED` | No | false | Enable CAPTCHA |
| `APP_RATE_LIMIT_ENABLED` | No | true | Enable rate limiting |
| `APP_ENCRYPTION_ENABLED` | No | true | Enable encryption |

---

## Deployment

### Local (Docker)
```bash
docker compose up --build
```

### Production (Railway)
- Deploy from GitHub repository
- Requires PostgreSQL database
- Configure environment variables in Railway dashboard

---

## Build & Run

### Prerequisites
- Java 17+
- Maven 3.8+

### Build
```bash
./mvnw clean package -DskipTests
```

### Run
```bash
java -jar target/hackathon-backend-0.0.1-SNAPSHOT.jar
```

---

## Monitoring

### Health Check
```
GET /actuator/health
```

### Metrics
```
GET /actuator/metrics
GET /actuator/prometheus
```

---

## Project Structure

```
.
├── Dockerfile                 # Docker build file
├── docker-compose.yml         # Local development stack
├── pom.xml                    # Maven configuration
├── README.md                  # Project readme
├── RAILWAY_DEPLOYMENT.md      # Railway deployment guide
├── HELP.md                    # Maven help
└── src/
    ├── main/
    │   ├── java/              # Source code
    │   └── resources/
    │       ├── application.yml
    │       └── db/migration/  # Flyway migrations
    └── test/                  # Test files
```
