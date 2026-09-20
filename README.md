# Kigali Bus Management System - Assignment 4

Spring Boot REST API implementing complete CRUD operations for Bus, Route, and Schedule.

## Requirements
- Java 21
- Maven
- PostgreSQL
- Postman

## Database
Create a PostgreSQL database named `bus_management_db`.

Default local configuration:
- URL: `jdbc:postgresql://localhost:5433/bus_management_db`
- Username: `postgres`
- Password: `postgres`

You can override these with environment variables `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`.

## Run
```bash
mvn spring-boot:run
```

Base URL: `http://localhost:8088`

## REST endpoints

### Bus
- POST `/api/buses`
- GET `/api/buses`
- GET `/api/buses/{id}`
- PUT `/api/buses/{id}`
- DELETE `/api/buses/{id}`

### Route
- POST `/api/routes`
- GET `/api/routes`
- GET `/api/routes/{id}`
- PUT `/api/routes/{id}`
- DELETE `/api/routes/{id}`

### Schedule
- POST `/api/schedules`
- GET `/api/schedules`
- GET `/api/schedules/{id}`
- PUT `/api/schedules/{id}`
- DELETE `/api/schedules/{id}`

## Business logic validation
- Bus plate numbers must follow a Rwanda-style format such as `RAD123B`.
- Bus plate numbers must be unique.
- Bus capacity must be between 10 and 100.
- Route start and end points must be different.
- Route distance must be between 0.5 km and 300 km.
- Estimated route duration must use `HH:mm` format.
- A schedule can only use an existing Bus and Route.
- Only a bus with `ACTIVE` status can be scheduled.
- Schedule arrival time must be after departure time.
- One bus cannot have two schedules at the same departure time on the same day.
- A Bus or Route referenced by a Schedule cannot be deleted.

Import the Postman collection from `postman/KBMS-WEBTECH.postman_collection.json`.
