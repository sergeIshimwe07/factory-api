# Factory ERP API

A comprehensive Enterprise Resource Planning (ERP) system for factory management built with Spring Boot 4.0.3 and Java 21.

## Features

- **Authentication & Authorization**: JWT-based authentication with role-based access control
- **Sales Management**: Complete sales order processing with customer management
- **Inventory Management**: Product tracking, stock movements, and low-stock alerts
- **Production Management**: Bill of Materials (BOM) and production tracking
- **Commission Management**: Automated commission calculation for sales agents
- **Accounting**: Chart of accounts, journal entries, and financial reporting
- **Dashboard**: Real-time KPIs and analytics
- **Reporting**: Comprehensive reports for sales, inventory, production, and finances

## Tech Stack

- **Java 21**
- **Spring Boot 4.0.3**
- **Spring Security** with JWT
- **Spring Data JPA** with Hibernate
- **MySQL 8.0+**
- **Flyway** for database migrations
- **Lombok** for cleaner code
- **OpenAPI 3.0** (Swagger) for API documentation

## Prerequisites

- JDK 21 or higher
- Maven 3.8+
- MySQL 8.0+

## Database Setup

1. Create MySQL database:
```sql
CREATE DATABASE factory_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Update database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/factory_db
spring.datasource.username=root
spring.datasource.password=your_password
```

## Installation & Running

1. Clone the repository
```bash
git clone <repository-url>
cd factory-erp
```

2. Build the project
```bash
mvn clean install
```

3. Run the application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Default Admin Credentials

- **Email**: admin@factory.com
- **Password**: SecurePass123

## API Documentation

Once the application is running, access the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

API documentation is also available at:
```
http://localhost:8080/v3/api-docs
```

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login

### Dashboard
- `GET /api/dashboard/summary` - Get dashboard summary with KPIs

### Sales
- `GET /api/sales` - List all sales orders (paginated)
- `POST /api/sales` - Create new sales order
- `GET /api/sales/{id}` - Get sales order details
- `PATCH /api/sales/{id}` - Update sales order

### Products
- `GET /api/products` - List all products (paginated)
- `POST /api/products` - Create new product
- `GET /api/products/{id}` - Get product details
- `PATCH /api/products/{id}` - Update product

### Customers
- `GET /api/customers` - List all customers (paginated)
- `POST /api/customers` - Create new customer
- `GET /api/customers/{id}` - Get customer details
- `PATCH /api/customers/{id}` - Update customer
- `PATCH /api/customers/{id}/toggle-block` - Block/unblock customer

### Payments
- `GET /api/payments` - List all payments (paginated)
- `POST /api/payments` - Record new payment
- `GET /api/payments/{id}` - Get payment details

### Inventory
- `GET /api/inventory/movements` - List stock movements
- `POST /api/inventory/movements` - Record stock movement

### Production
- `GET /api/production` - List production entries
- `POST /api/production` - Create production entry
- `GET /api/production/bom` - List Bill of Materials
- `POST /api/production/bom` - Create BOM

### Commissions
- `GET /api/commissions` - List commissions
- `POST /api/commissions/{id}/mark-paid` - Mark commission as paid
- `GET /api/commissions/rules` - List commission rules
- `POST /api/commissions/rules` - Create commission rule

### Accounting
- `GET /api/journal` - List journal entries
- `POST /api/journal` - Create journal entry
- `GET /api/accounts` - List chart of accounts

### Reports
- `GET /api/reports/sales` - Sales report
- `GET /api/reports/inventory` - Inventory report
- `GET /api/reports/production` - Production report
- `GET /api/reports/commissions` - Commissions report
- `GET /api/reports/profit-loss` - Profit & Loss statement
- `GET /api/reports/balance-sheet` - Balance sheet

### Users
- `GET /api/users` - List all users (paginated)
- `POST /api/users` - Create new user
- `PATCH /api/users/{id}/toggle-active` - Activate/deactivate user

### Suppliers
- `GET /api/suppliers` - List all suppliers
- `POST /api/suppliers` - Create new supplier
- `PATCH /api/suppliers/{id}` - Update supplier

## API Response Format

### Success Response
```json
{
  "status": "success",
  "data": {...},
  "message": "Optional message",
  "timestamp": "2026-03-17T10:30:00"
}
```

### Error Response
```json
{
  "status": "error",
  "message": "Error description",
  "code": "ERROR_CODE",
  "timestamp": "2026-03-17T10:30:00"
}
```

### Paginated Response
```json
{
  "status": "success",
  "data": {
    "data": [...],
    "pagination": {
      "page": 1,
      "limit": 10,
      "total": 100,
      "pages": 10,
      "hasMore": true
    }
  }
}
```

## Authentication

All protected endpoints require a JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

To obtain a token, use the login endpoint:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@factory.com",
    "password": "SecurePass123"
  }'
```

## Database Schema

The application uses Flyway for database migrations. All migrations are located in `src/main/resources/db/migration/`.

### Main Tables
- `users` - System users with roles
- `customers` - Customer information
- `suppliers` - Supplier information
- `products` - Product catalog
- `sales_orders` - Sales orders
- `sales_order_items` - Sales order line items
- `payments` - Payment records
- `stock_movements` - Inventory movements
- `bill_of_materials` - Production BOMs
- `bom_items` - BOM components
- `production_entries` - Production records
- `commissions` - Commission records
- `commission_rules` - Commission calculation rules
- `accounts` - Chart of accounts
- `journal_entries` - Accounting journal entries
- `journal_entry_lines` - Journal entry lines

## Project Structure

```
src/main/java/com/factory/factory_erp/
├── config/              # Configuration classes
├── controller/          # REST controllers
├── dto/                 # Data Transfer Objects
│   ├── request/        # Request DTOs
│   └── response/       # Response DTOs
├── entity/             # JPA entities
│   └── enums/         # Enums
├── exception/          # Custom exceptions
├── repository/         # Spring Data repositories
├── service/            # Business logic services
└── util/               # Utility classes

src/main/resources/
├── db/
│   └── migration/      # Flyway migration scripts
└── application.properties
```

## Development

### Running Tests
```bash
mvn test
```

### Building for Production
```bash
mvn clean package -DskipTests
java -jar target/factory-erp-0.0.1-SNAPSHOT.jar
```

## Environment Variables

You can override application properties using environment variables:

- `DB_URL` - Database URL
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `JWT_SECRET` - JWT secret key
- `JWT_EXPIRATION` - JWT expiration time in milliseconds

## Security

- Passwords are encrypted using BCrypt
- JWT tokens expire after 24 hours (configurable)
- CORS is enabled for all origins (configure for production)
- All endpoints except `/api/auth/**` require authentication

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

## Support

For support, email support@factory-erp.com or open an issue in the repository.
