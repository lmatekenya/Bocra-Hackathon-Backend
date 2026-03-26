# BOCRA Hackathon Backend

Spring Boot API for BOCRA public services and secure admin operations.

## Stack
- Java 17
- Spring Boot 3
- Spring Security (JWT + RBAC)
- PostgreSQL + Flyway
- Bucket4j rate limiting
- Actuator + Prometheus metrics endpoint
- Docker deployment

## Core Endpoints
- `POST /api/v1/auth/login`
- `GET /api/v1/auth/me`
- `POST /api/v1/auth/logout`
- `POST /api/v1/complaints`
- `GET /api/v1/complaints/{ticketId}`
- `POST /api/v1/inquiries`
- `POST /api/v1/cyber-incidents`
- `GET /api/v1/cyber-incidents/{incidentId}`
- `GET /api/v1/news`
- `GET /api/v1/tenders`
- `GET /api/v1/documents`
- `GET /api/v1/stats`

## Admin Endpoints (`ROLE_ADMIN`)
- `GET /api/v1/admin/complaints`
- `GET /api/v1/admin/inquiries`
- `GET /api/v1/admin/cyber-incidents`
- `GET /api/v1/admin/audit-logs`
- `POST /api/v1/news`
- `POST /api/v1/tenders`
- `POST /api/v1/documents`

## Security Controls Implemented
- JWT authentication with role-based authorization.
- Login lockout policy after repeated failed attempts.
- Rate limiting on login and public submission endpoints.
- Optional server-side CAPTCHA verification (Cloudflare Turnstile compatible).
- Sensitive-field encryption converter for stored PII.
- Audit trail for auth and admin/public critical actions.
- Strict security headers + HSTS + CORS allow-list.

## Environment Variables
- `PORT`
- `JDBC_DATABASE_URL`
- `JDBC_DATABASE_USERNAME`
- `JDBC_DATABASE_PASSWORD`
- `DATABASE_URL` (optional Railway URL alternative)
- `POSTGRES_URL` (optional Railway URL alternative)
- `PGHOST` (optional Railway alternative to `JDBC_DATABASE_URL`)
- `PGPORT` (optional Railway alternative to `JDBC_DATABASE_URL`)
- `PGDATABASE` (optional Railway alternative to `JDBC_DATABASE_URL`)
- `PGUSER` (optional Railway alternative to `JDBC_DATABASE_USERNAME`)
- `PGPASSWORD` (optional Railway alternative to `JDBC_DATABASE_PASSWORD`)
- `APP_JWT_SECRET`
- `APP_JWT_EXPIRATION_MS`
- `APP_CORS_ALLOWED_ORIGINS`
- `APP_ADMIN_BOOTSTRAP_USERNAME`
- `APP_ADMIN_BOOTSTRAP_PASSWORD`
- `APP_AUTH_MAX_FAILED_ATTEMPTS`
- `APP_AUTH_LOCK_MINUTES`
- `APP_CAPTCHA_ENABLED`
- `APP_CAPTCHA_SECRET`
- `APP_CAPTCHA_VERIFY_URL`
- `APP_RATE_LIMIT_ENABLED`
- `APP_RATE_LIMIT_LOGIN_CAPACITY`
- `APP_RATE_LIMIT_LOGIN_WINDOW_SECONDS`
- `APP_RATE_LIMIT_PUBLIC_CAPACITY`
- `APP_RATE_LIMIT_PUBLIC_WINDOW_SECONDS`
- `APP_ENCRYPTION_ENABLED`
- `APP_ENCRYPTION_SECRET`

## Migrations
- `V1__initial_schema.sql`
- `V2__seed_reference_data.sql`
- `V3__security_hardening_and_audit_log.sql`
- `V4__seed_operational_demo_data.sql`

## Local Run (Docker)
```bash
docker compose up --build
```

## Deployment Automation
Workflow:
- `.github/workflows/railway-deploy.yml`

Required GitHub secret:
- `RAILWAY_DEPLOY_HOOK_URL`
