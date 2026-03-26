# Deploying Bocra Backend to Railway with PostgreSQL

This guide provides step-by-step instructions to deploy the Spring Boot backend application to Railway with a PostgreSQL database.

---

## Prerequisites

- A [Railway](https://railway.app/) account
- Git installed locally
- Your project code pushed to a GitHub repository

---

## Step 1: Prepare Your Repository

1. Ensure your code is pushed to a GitHub repository
2. The repository must contain:
   - `Dockerfile` (already present)
   - `pom.xml` (already present)
   - `src/` directory (already present)

---

## Step 2: Create a Railway Project

1. Log in to [Railway](https://railway.app/)
2. Click **"New Project"**
3. Select **"Deploy from GitHub repo"**
4. Choose your repository containing the Bocra backend code
5. Click **"Deploy Now"**

---

## Step 3: Add PostgreSQL Database

1. In your Railway project dashboard, click **"New"** → **"Database"** → **"PostgreSQL"**
2. Wait for the database to provision
3. Once ready, click on the PostgreSQL service
4. Go to the **"Variables"** tab
5. Copy the database connection variables from the PostgreSQL service.
   Railway may expose these as `PG*` variables (recommended) and/or a URL variable:
   - `PGHOST`
   - `PGPORT`
   - `PGDATABASE`
   - `PGUSER`
   - `PGPASSWORD`
   - Optional URL variable such as `DATABASE_URL` or `POSTGRES_URL`

---

## Step 4: Configure Environment Variables

Go to your backend service's **"Variables"** tab and add the following:

| Variable | Value | Description |
|----------|-------|-------------|
| `PORT` | `8083` | Application port |
| `JDBC_DATABASE_URL` | `jdbc:postgresql://<host>:<port>/<db>` | PostgreSQL JDBC URL |
| `JDBC_DATABASE_USERNAME` | `<db_user>` | Database username |
| `JDBC_DATABASE_PASSWORD` | `<db_password>` | Database password |
| `APP_JWT_SECRET` | `<generate_strong_secret>` | JWT signing secret (min 32 characters) |
| `APP_JWT_EXPIRATION_MS` | `3600000` | JWT token expiration (1 hour) |
| `APP_CORS_ALLOWED_ORIGINS` | `*` | Allowed CORS origins (or your frontend URL) |
| `APP_ADMIN_BOOTSTRAP_USERNAME` | `admin` | Default admin username |
| `APP_ADMIN_BOOTSTRAP_PASSWORD` | `<set_strong_password>` | Default admin password |
| `APP_CAPTCHA_ENABLED` | `false` | Disable captcha for deployment |
| `APP_RATE_LIMIT_ENABLED` | `true` | Enable rate limiting |
| `APP_ENCRYPTION_ENABLED` | `true` | Enable encryption |

If your backend service is linked to the Railway PostgreSQL service and receives
`PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, and `PGPASSWORD`, the application can
use those directly even without `JDBC_DATABASE_*`.

### Getting JDBC_DATABASE_URL

1. Go to your PostgreSQL service in Railway
2. Copy `PGHOST`, `PGPORT`, and `PGDATABASE`
3. Build the value as:
   `jdbc:postgresql://<PGHOST>:<PGPORT>/<PGDATABASE>`
4. Use `PGUSER` for `JDBC_DATABASE_USERNAME` and `PGPASSWORD` for `JDBC_DATABASE_PASSWORD`

---

## Step 5: Configure Dockerfile for Railway

The existing Dockerfile works, but ensure it exposes the correct port. The current Dockerfile exposes port 8083, which matches the `PORT` variable in `application.yml`.

No changes needed to the Dockerfile for Railway deployment.

---

## Step 6: Deploy the Application

1. In Railway dashboard, go to your backend service
2. Click **"Deploy"** button (or push to GitHub to trigger auto-deploy)
3. Watch the deployment logs for any errors
4. Wait for the build to complete (may take 3-5 minutes on first deploy)

---

## Step 7: Verify Deployment

1. Once deployed, click on the **"Deployments"** tab
2. Click on the latest deployment
3. Copy the **"URL"** (e.g., `https://your-app.up.railway.app`)
4. Test the API health endpoint:
   ```
   https://your-app.up.railway.app/actuator/health
   ```
5. You should receive a JSON response with `"status":"UP"`

---

## Step 8: Test Authentication

The application creates an admin user on first startup based on `APP_ADMIN_BOOTSTRAP_USERNAME` and `APP_ADMIN_BOOTSTRAP_PASSWORD`.

Test login:
```bash
curl -X POST https://your-app.up.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"YourAdminPassword"}'
```

---

## Troubleshooting

### Database Connection Errors
- Verify `JDBC_DATABASE_URL` is correctly formatted as `jdbc:postgresql://...`
- Ensure PostgreSQL service is running
- Check that database credentials match
- If logs show `Connection to localhost:5432 refused`, Railway DB variables were not injected into the backend service

### Build Failures
- Check Maven dependencies in `pom.xml`
- Ensure Java 17 is specified (already configured in Dockerfile)
- Review build logs for specific errors

### Application Won't Start
- Check environment variables are properly set
- Verify Flyway migrations run successfully (check logs)
- Ensure `APP_JWT_SECRET` is at least 32 characters

### CORS Errors
- Update `APP_CORS_ALLOWED_ORIGINS` to include your frontend domain
- For development, you can set it to `*` (not recommended for production)

---

## Environment Variables Reference

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `PORT` | No | 8083 | Server port |
| `JDBC_DATABASE_URL` | No* | - | PostgreSQL JDBC URL (`jdbc:postgresql://...`) |
| `JDBC_DATABASE_USERNAME` | No* | postgres | Database username |
| `JDBC_DATABASE_PASSWORD` | No* | - | Database password |
| `PGHOST` | No* | - | Railway PostgreSQL host |
| `PGPORT` | No* | - | Railway PostgreSQL port |
| `PGDATABASE` | No* | - | Railway PostgreSQL database name |
| `PGUSER` | No* | - | Railway PostgreSQL username |
| `PGPASSWORD` | No* | - | Railway PostgreSQL password |
| `APP_JWT_SECRET` | Yes | - | JWT secret (min 32 chars) |
| `APP_JWT_EXPIRATION_MS` | No | 3600000 | JWT expiration in ms |
| `APP_CORS_ALLOWED_ORIGINS` | No | http://localhost:3000 | Allowed CORS origins |
| `APP_ADMIN_BOOTSTRAP_USERNAME` | No | admin | Initial admin username |
| `APP_ADMIN_BOOTSTRAP_PASSWORD` | No | - | Initial admin password |
| `APP_CAPTCHA_ENABLED` | No | false | Enable Cloudflare Turnstile |
| `APP_RATE_LIMIT_ENABLED` | No | true | Enable rate limiting |
| `APP_ENCRYPTION_ENABLED` | No | true | Enable data encryption |

`*` Provide either `JDBC_DATABASE_*` variables or the `PG*` database variables.

---

## Security Notes for Production

1. **Change JWT Secret**: Generate a strong random secret
2. **Change Admin Password**: Update `APP_ADMIN_BOOTSTRAP_PASSWORD`
3. **Configure CORS**: Set specific origins instead of `*`
4. **Enable Captcha**: Set `APP_CAPTCHA_ENABLED=true` and configure `APP_CAPTCHA_SECRET`
5. **Encryption Secret**: Set `APP_ENCRYPTION_SECRET` to a strong value

---

## Additional Resources

- [Railway Documentation](https://docs.railway.app/)
- [Railway PostgreSQL Guide](https://docs.railway.app/databases/postgresql)
- [Railway Deployments](https://docs.railway.app/deploy/deployments)
