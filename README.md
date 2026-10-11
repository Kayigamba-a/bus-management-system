# KBMS — Kigali Bus Management System

A full-stack Web Technology academic project. Backend: Java 21 / Spring Boot 3.5.6. Frontend: React / Vite. Data: PostgreSQL (transactional records) and MongoDB (audit documents). RabbitMQ queues notification events. Session authentication + Spring Security RBAC (ADMIN / OPERATOR / PASSENGER). Optional Google OAuth2 login.

## Quick start (recommended — VS Code + Docker Desktop)

1. Install **VS Code**, **Git**, **Docker Desktop**, and (optionally) Java 21 plus the VS Code Extension Pack for Java and Spring Boot Extension Pack. Open the **KBMS folder** in VS Code.
2. Start Docker Desktop and wait until the engine is running.
3. In the VS Code terminal, run `docker compose up --build -d`. The first startup downloads Docker images/dependencies.
4. Open **http://localhost:5173**. Backend health check: **http://localhost:8080/api/health**. RabbitMQ management console: **http://localhost:15672** (user `guest`, password `guest`).
5. Sign in with the *development-only seed account* `admin@kbms.rw` / `ChangeMe123!`. **Change this password before any public deployment**; configure `ADMIN_EMAIL` and `ADMIN_PASSWORD` for newly initialized databases.
6. To stop: `docker compose down`. To inspect logs: `docker compose logs -f backend`. To reset all development data: `docker compose down -v` (destructive).

### Local development without backend Docker

Run `docker compose up -d postgres mongodb rabbitmq` first. With JDK **21** and Maven installed, `cd backend && mvn spring-boot:run`. Then `cd frontend && npm install && npm run dev`. Running `docker compose up --build -d` is easier because Docker packages Maven/JDK and Node automatically. Java 27 needn't be your project target; choose JDK 21 for compatibility.

## Roles / functionality

- **Public**: routes and upcoming schedules.
- **Passenger**: register, log in, book one seat per trip, view bookings.
- **Operator**: add buses/routes, create and update schedules, mark trips delayed/cancelled/completed.
- **Admin**: all operator privileges, promote/demote users, view MongoDB audit events.
- PostgreSQL stores users, buses, routes, trips and bookings. MongoDB stores audit events. RabbitMQ is a durable broker; its consumer logs notification payloads as a **development simulation**, it does **not** send real SMS/email.

## Optional Google OAuth2 login

Create a Google OAuth web client with callback `http://localhost:8080/login/oauth2/code/google` and allowed origin `http://localhost:5173`. Create `.env` from `.env.example`, populate `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET`, set `SPRING_PROFILES_ACTIVE=oauth`, then restart with `docker compose up --build -d`. Click **Continue with Google** on the sign-in page. Without credentials, Google login is not functional; local email/password works.

## API endpoints

| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/health`, `/api/routes`, `/api/trips` | public |
| GET | `/api/auth/csrf` | public |
| POST | `/api/auth/register`, `/api/auth/login` | public |
| GET | `/api/auth/me` | signed in |
| POST | `/api/auth/logout` | signed in |
| GET | `/api/buses` | signed in |
| POST,PUT,DELETE | `/api/buses`, `/api/buses/{id}` | operator/admin |
| POST,PUT,DELETE | `/api/routes`, `/api/routes/{id}` | operator/admin |
| POST,PUT,DELETE | `/api/trips`, `/api/trips/{id}` | operator/admin |
| GET | `/api/bookings/mine` | signed in |
| POST | `/api/bookings/{tripId}` | signed in |
| GET | `/api/admin/users`, `/api/admin/audit` | admin |
| PUT | `/api/admin/users/{id}/role` | admin |

The client fetches `/api/auth/csrf` first, includes session cookies and sends the returned token in `X-XSRF-TOKEN` for state changes.

## Development limits / delivery checklist

- This is a functional **project source package** and demo environment, not a production-hardened public transit platform. No real bus GPS/live data, online payment, SMS gateway, email provider, seat map or formal operator assignment exists.
- Google OAuth2 needs credentials and deployment-specific redirects; RabbitMQ notification consumption is simulated, not actual SMS/email delivery.
- Set non-default passwords and secure your OAuth secrets before deployment; restrict origin, use HTTPS/secure cookies, add data migrations and operational monitoring.
- Deployment and integration tests **must be run on your computer**: this packaging environment does not have Docker, Maven or network dependency resolution available.
- Git requirement: initialize a repository with `git init`, stage and commit each meaningful milestone, push to GitHub, and open a pull request to `main`. A ZIP cannot manufacture legitimate historical commit timestamps.

See `docs/PROJECT_REPORT.md` for the requirements, ER diagram, use cases, test plan, architecture and demo walkthrough.


## Booking update (October 2026)

- Existing Spring Security CORS and HTTP-session login fixes are included.
- Passengers self-book with a phone number and can cancel their active reservations.
- Administrators can book seats for registered passengers (select user + phone) or walk-ins (name + phone), and can confirm payment/cancel reservations. Admin identity is **never** used as the booked passenger.
- PENDING_PAYMENT reservations occupy capacity until confirmed, canceled, or expired. The payment deadline is 30 minutes before departure; unpaid reservations expire automatically (background sweep every minute plus checks on booking/payment).
- A trip-level database lock serializes reservations so capacity cannot be oversold. Delayed trips use their updated departure time.
- Old booking rows remain in PostgreSQL. The obsolete unique passenger/trip constraint is removed at startup without clearing volume data. Previous CONFIRMED bookings still occupy seats.
- Phone numbers are collected for bookings, but notifications are routed through the existing **simulated** RabbitMQ consumer; no actual SMS is sent.
- **Back up data before replacing code**: `docker compose exec -T postgres pg_dump -U kbms kbms > kbms_backup.sql` (PowerShell: use `cmd /c` if redirected output encoding causes problems). Do not use `docker compose down -v`.
- Rebuild with `docker compose up -d --build`; test booking on a FUTURE trip at least 30 minutes away.
