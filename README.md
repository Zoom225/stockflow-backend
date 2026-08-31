# StockFlow Backend

Backend REST API for the StockFlow inventory management application.

## Technologies

- Java 21
- Spring Boot 4.1.1
- Maven
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- Bean Validation
- Lombok

## Project

StockFlow is an inventory management application for small and medium-sized businesses.

Main features will include:

- Product management
- Category management
- Supplier management
- Stock entries and exits
- Stock movement history
- Low-stock alerts
- Authentication and authorization
- Dashboard

## Run Locally

### 1. Start PostgreSQL

```bash
docker compose up -d
```

The default database credentials are:

- Database: `stockflow`
- User: `stockflow`
- Password: `stockflow`

### 2. Run the application

```bash
./mvnw spring-boot:run
```

On startup, Flyway applies the SQL migrations from `src/main/resources/db/migration` and Hibernate validates that the entity mappings match the schema.

### 3. Optional environment variables

You can override the default connection with:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
