# Deepali — User CRUD Spring Boot API

**Author:** Deepali  
**Stack:** Java 21, Spring Boot 3.4, Spring Web, Spring Security + JWT, Spring Data JPA/Hibernate, PostgreSQL, OpenAPI/Swagger UI, Resilience4j, JUnit 5, Mockito, Docker, GitHub Actions, Jenkins.

## Features
- Create, read, update and delete users; paginated listing.
- JWT login endpoint and stateless bearer-token security.
- Consistent success response envelope and structured global error responses.
- Request validation, duplicate-email conflict handling and not-found handling.
- OpenAPI 3 documentation for controllers, including JWT bearer security.
- Resilience4j circuit breaker with fallback for an example external profile service.
- Unit tests, GitHub Actions workflow, Jenkins pipeline, Dockerfile and Docker Compose.

## Run with Docker Compose
Prerequisites: Docker with Compose and (for local Maven builds) Java 21 + Maven.

```bash
# Optional: set a non-demo secret before starting
export JWT_SECRET="$(openssl rand -base64 48)"
docker compose up --build
```

The app listens on `http://localhost:8080`. PostgreSQL is available on port 5432.

> Local demo login defaults to `demo` / `demo12345`. Override `DEMO_USERNAME` and `DEMO_PASSWORD`; never use demo credentials or the development JWT secret in a real deployment. The in-memory demo user is for sample authentication only—replace it with a persisted user/account model before production.

## Run locally
Start PostgreSQL and create a database/user matching `application.yml`, or set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`. Then:

```bash
mvn clean verify
mvn spring-boot:run
```

## OpenAPI / Swagger
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Login is public. Swagger's **Authorize** dialog accepts the JWT value; enter the token (the UI sends it as `Authorization: Bearer ...`).

## API endpoints
| Method | Endpoint | Auth | Purpose |
|---|---|---:|---|
| POST | `/api/v1/auth/login` | No | Exchange demo credentials for JWT |
| POST | `/api/v1/users` | Yes | Create a user |
| GET | `/api/v1/users/{id}` | Yes | Get a user |
| GET | `/api/v1/users?page=0&size=20&sort=id,asc` | Yes | Paginated users |
| PUT | `/api/v1/users/{id}` | Yes | Update user |
| DELETE | `/api/v1/users/{id}` | Yes | Delete user |
| GET | `/api/v1/users/{id}/profile-summary` | Yes | Demonstrate circuit breaker + fallback |
| GET | `/actuator/health` | No | Health check |

## Example login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"demo","password":"demo12345"}'
```
Copy `data.accessToken` from the response.

## Example create user
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H 'Authorization: Bearer YOUR_JWT' \
  -H 'Content-Type: application/json' \
  -d '{"name":"Deepali","email":"deepali@example.com","phone":"9876543210"}'
```

Success response:
```json
{
  "success": true,
  "message": "User created successfully",
  "data": {
    "id": 1,
    "name": "Deepali",
    "email": "deepali@example.com",
    "phone": "9876543210",
    "createdAt": "2026-10-09T10:00:00Z",
    "updatedAt": "2026-10-09T10:00:00Z"
  },
  "timestamp": "2026-10-09T10:00:00Z"
}
```
Timestamps are illustrative. Error responses include `timestamp`, `status`, `error`, `message`, `path`, and `validationErrors`.

## Circuit breaker example
The profile summary client calls `PROFILE_SERVICE_URL/profiles/{id}`. If that dependency fails, the endpoint returns a degraded summary with tier `TEMPORARILY_UNAVAILABLE` and source `fallback`. Set `PROFILE_SERVICE_URL` to a compatible service to exercise the successful path. Circuit-breaker thresholds are in `application.yml`.

## Tests
```bash
mvn test
```
Tests cover service success, duplicate email, not-found behavior, paging, delete behavior and fallback response. The Spring context test uses the configured PostgreSQL datasource, so run it with PostgreSQL available; for a fully isolated test setup, add Testcontainers or a dedicated test profile.

## CI/CD
- `.github/workflows/maven.yml`: builds and tests on pushes and pull requests to `main` / `master`.
- `Jenkinsfile`: runs Maven verification, packages the app, builds a Docker image and publishes on `main`. Configure Jenkins tools `jdk21` and `maven3`, Docker access, and a credential named `dockerhub-credentials` before enabling publishing.
- `Dockerfile`: non-root runtime container.
- `docker-compose.yml`: starts PostgreSQL and the app.

## Production hardening checklist
- Replace in-memory demo authentication with persisted accounts and role-based authorization.
- Keep JWT secrets in a secrets manager; rotate secrets and use HTTPS.
- Use Flyway/Liquibase migrations instead of `ddl-auto: update`.
- Add integration tests with Testcontainers and endpoint-level security tests.
- Set database credentials and image registry names through secret-managed environment variables.
