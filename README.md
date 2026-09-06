# Kigali Bus Management System

A JSF + Hibernate CRUD web application for managing Kigali buses and routes.

## Stack

- Java source compatibility: 11
- Apache Tomcat 9
- JSF 2.3 (Mojarra)
- CDI / Weld 3
- Hibernate ORM 5.6
- PostgreSQL JDBC
- PostgreSQL database: `bus_management_db`
- Maven WAR project

## Database configuration

The application expects:

- Host: `localhost`
- Port: `5432`
- Database: `bus_management_db`
- User: `postgres`

The database password is intentionally not stored in this repository.
Supply it locally using either:

- Tomcat/JVM option: `-Ddb.password=YOUR_PASSWORD`
- Environment variable: `BUS_DB_PASSWORD`

Do not commit a real password to GitHub.

## Functionalities

### Dashboard
- PostgreSQL connection status
- Total buses
- Active / maintenance / inactive bus counts
- Total routes

### Bus management
- Create, read, update and delete buses
- Search buses
- Unique plate-number protection
- Status management: ACTIVE, MAINTENANCE, INACTIVE
- Rwanda-style plate validation such as `RAD123B`

### Route management
- Create, read, update and delete routes
- Search routes
- Distance validation
- Duration validation in `HH:mm`
- Prevents the same start and end point

## Validation

The forms demonstrate three levels of input validation:

1. Client-side JavaScript validation
2. JSF standard validators
3. Bean Validation annotations on entity classes

The UI also demonstrates external, internal and inline CSS.

## IntelliJ + Tomcat setup

1. Open the project as a Maven project.
2. Select a JDK that can compile Java 11 source (for example JDK 21 with Maven release 11).
3. Reload Maven dependencies.
4. Create a Tomcat 9 Local run configuration.
5. Add the artifact `bus-management-system:war exploded` under Deployment.
6. Configure the database password locally in the Tomcat run configuration.
7. Start PostgreSQL and then run Tomcat.

Hibernate will automatically create/update the `bus` and `route` tables in the `public` schema because `hibernate.hbm2ddl.auto=update` is enabled for this development project.

## Main pages

- Dashboard: `index.xhtml`
- Buses: `bus-list.xhtml` / `bus-form.xhtml`
- Routes: `route-list.xhtml` / `route-form.xhtml`
