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

## API Endpoints

### Products

- `GET /api/products`
- `GET /api/products/low-stock`
- `GET /api/products/{id}`
- `POST /api/products`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`

The low-stock endpoint returns products where `quantityInStock <= minimumStock`.

### Stock movements

- `GET /api/stock-movements`
- `GET /api/stock-movements/{id}`
- `GET /api/stock-movements/products/{productId}`
- `POST /api/stock-movements/products/{productId}/restock`
- `POST /api/stock-movements`
- `PUT /api/stock-movements/{id}`
- `DELETE /api/stock-movements/{id}`

The product history endpoint returns the stock movements for one product ordered by `movementDate` descending.
The restock endpoint creates an inbound stock movement and increases the current stock of the product.
