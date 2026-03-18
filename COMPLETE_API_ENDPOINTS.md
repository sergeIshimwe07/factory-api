# Factory ERP API - Complete Endpoint Documentation

## 🎉 IMPLEMENTATION COMPLETE - ALL ENDPOINTS READY

This document provides a complete list of all implemented API endpoints for the Factory ERP system.

---

## Authentication Endpoints

### POST /api/auth/login
**Description**: User login with JWT token generation  
**Request Body**:
```json
{
  "email": "admin@factory.com",
  "password": "SecurePass123"
}
```
**Response**:
```json
{
  "status": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": "USR-001",
      "name": "Admin User",
      "email": "admin@factory.com",
      "role": "admin",
      "avatar": null
    }
  }
}
```

---

## Dashboard Endpoints

### GET /api/dashboard/summary
**Description**: Get dashboard summary with KPIs, sales trends, low stock alerts  
**Query Parameters**:
- `period` (optional): "day", "week", "month" (default: "month")

**Response**:
```json
{
  "status": "success",
  "data": {
    "totalSalesToday": 125000.00,
    "totalSalesMonth": 3500000.00,
    "outstandingCredit": 450000.00,
    "lowStockAlerts": [...],
    "commissionSummary": {...},
    "salesTrend": [...]
  }
}
```

---

## Product Endpoints

### GET /api/products
**Description**: List all products with pagination and filtering  
**Query Parameters**:
- `page` (default: 1)
- `limit` (default: 10)
- `category` (optional)
- `status` (optional): "active", "inactive", "discontinued"

### GET /api/products/{id}
**Description**: Get product details by ID

### POST /api/products
**Description**: Create new product  
**Request Body**:
```json
{
  "name": "Product Name",
  "category": "Electronics",
  "unit": "piece",
  "basePrice": 50000.00,
  "costPrice": 35000.00,
  "minimumStock": 10,
  "reorderLevel": 20,
  "description": "Product description"
}
```

### PATCH /api/products/{id}
**Description**: Update product details

### GET /api/products/search?q={query}
**Description**: Search products by name or SKU

---

## Customer Endpoints

### GET /api/customers
**Description**: List all customers with pagination  
**Query Parameters**:
- `page` (default: 1)
- `limit` (default: 10)

### GET /api/customers/{id}
**Description**: Get customer details by ID

### POST /api/customers
**Description**: Create new customer  
**Request Body**:
```json
{
  "name": "Customer Name",
  "type": "wholesale",
  "email": "customer@example.com",
  "phone": "+250788123456",
  "address": "Kigali, Rwanda",
  "city": "Kigali",
  "creditLimit": 5000000.00
}
```

### PATCH /api/customers/{id}
**Description**: Update customer details

### PATCH /api/customers/{id}/toggle-block
**Description**: Block or unblock a customer

### GET /api/customers/search?q={query}
**Description**: Search customers by name or email

---

## Sales Order Endpoints

### GET /api/sales
**Description**: List all sales orders with pagination and filtering  
**Query Parameters**:
- `page` (default: 1)
- `limit` (default: 10)
- `status` (optional): "pending", "confirmed", "processing", "completed", "cancelled"

### GET /api/sales/{id}
**Description**: Get sales order details by ID

### POST /api/sales
**Description**: Create new sales order  
**Request Body**:
```json
{
  "customerId": "CUST-001",
  "items": [
    {
      "productId": "PROD-001",
      "quantity": 10,
      "unitPrice": 50000.00,
      "discount": 0
    }
  ],
  "discount": 0,
  "notes": "Order notes",
  "dueDate": "2026-04-15"
}
```

### PATCH /api/sales/{id}/status
**Description**: Update order status  
**Request Body**:
```json
{
  "status": "confirmed"
}
```

---

## Payment Endpoints

### GET /api/payments
**Description**: List all payments with pagination  
**Query Parameters**:
- `page` (default: 1)
- `limit` (default: 10)
- `customerId` (optional)

### GET /api/payments/{id}
**Description**: Get payment details by ID

### POST /api/payments
**Description**: Record new payment  
**Request Body**:
```json
{
  "saleId": "SALE-001",
  "amount": 500000.00,
  "method": "bank_transfer",
  "reference": "TXN123456",
  "notes": "Payment notes"
}
```

---

## Inventory/Stock Movement Endpoints

### GET /api/inventory/movements
**Description**: List stock movements with pagination and filtering  
**Query Parameters**:
- `page` (default: 1)
- `limit` (default: 10)
- `type` (optional): "receipt", "issue", "adjustment", "production", "return_stock"
- `dateFrom` (optional): "2026-01-01"

### POST /api/inventory/movements
**Description**: Record stock movement  
**Request Body**:
```json
{
  "productId": "PROD-001",
  "type": "receipt",
  "quantity": 100,
  "reference": "PO-12345",
  "notes": "Stock received from supplier"
}
```

---

## Production Endpoints

### GET /api/production
**Description**: List production entries with pagination  
**Query Parameters**:
- `page` (default: 1)
- `limit` (default: 10)
- `status` (optional): "planned", "in_progress", "completed", "cancelled"

### POST /api/production
**Description**: Create production entry  
**Request Body**:
```json
{
  "billOfMaterialsId": "BOM-001",
  "quantityProduced": 100,
  "quantityDefective": 2,
  "supervisor": "John Doe",
  "notes": "Production notes"
}
```

### GET /api/production/bom
**Description**: List all Bill of Materials (BOM)  
**Query Parameters**:
- `page` (default: 1)
- `limit` (default: 10)

---

## User Management Endpoints

### GET /api/users
**Description**: List all users with pagination  
**Query Parameters**:
- `page` (default: 1)
- `limit` (default: 10)

### POST /api/users
**Description**: Create new user  
**Request Body**:
```json
{
  "name": "User Name",
  "email": "user@factory.com",
  "password": "SecurePass123",
  "role": "sales_agent"
}
```

### PATCH /api/users/{id}/toggle-active
**Description**: Activate or deactivate user

### POST /api/users/{id}/reset-password
**Description**: Reset user password  
**Request Body**:
```json
{
  "newPassword": "NewSecurePass123"
}
```

---

## Supplier Endpoints

### GET /api/suppliers
**Description**: List all suppliers with pagination  
**Query Parameters**:
- `page` (default: 1)
- `limit` (default: 10)

### POST /api/suppliers
**Description**: Create new supplier  
**Request Body**:
```json
{
  "name": "Supplier Name",
  "email": "supplier@example.com",
  "phone": "+250788123456",
  "address": "Kigali, Rwanda",
  "contactPerson": "Contact Name"
}
```

### PATCH /api/suppliers/{id}
**Description**: Update supplier details

---

## Commission Endpoints

### GET /api/commissions
**Description**: List all commissions with pagination  
**Query Parameters**:
- `page` (default: 1)
- `limit` (default: 10)
- `status` (optional): "paid", "unpaid"

### POST /api/commissions/{id}/mark-paid
**Description**: Mark commission as paid  
**Request Body**:
```json
{
  "reference": "PAY-12345",
  "paidDate": "2026-03-17"
}
```

### GET /api/commissions/rules
**Description**: List all commission rules

### POST /api/commissions/rules
**Description**: Create commission rule  
**Request Body**:
```json
{
  "productId": "PROD-001",
  "type": "percentage",
  "value": 5.0
}
```

### PATCH /api/commissions/rules/{id}
**Description**: Update commission rule

### DELETE /api/commissions/rules/{id}
**Description**: Delete commission rule (soft delete)

---

## Reporting Endpoints

### GET /api/reports/sales
**Description**: Sales report for a date range  
**Query Parameters**:
- `dateFrom`: "2026-01-01" (required)
- `dateTo`: "2026-03-31" (required)

**Response**:
```json
{
  "status": "success",
  "data": {
    "period": { "from": "2026-01-01", "to": "2026-03-31" },
    "summary": {
      "totalSales": 10500000.00,
      "totalOrders": 245,
      "averageOrderValue": 42857.14
    }
  }
}
```

### GET /api/reports/inventory
**Description**: Inventory report  
**Query Parameters**:
- `category` (optional)

### GET /api/reports/production
**Description**: Production report for a date range  
**Query Parameters**:
- `dateFrom`: "2026-01-01" (required)
- `dateTo`: "2026-03-31" (required)

### GET /api/reports/commissions
**Description**: Commissions report for a month  
**Query Parameters**:
- `month`: "2026-03" (required)

### GET /api/reports/profit-loss
**Description**: Profit & Loss statement  
**Query Parameters**:
- `dateFrom`: "2026-01-01" (required)
- `dateTo`: "2026-03-31" (required)

**Response**:
```json
{
  "status": "success",
  "data": {
    "period": { "from": "2026-01-01", "to": "2026-03-31" },
    "revenue": {
      "sales": 3500000.00,
      "otherIncome": 50000.00,
      "totalRevenue": 3550000.00
    },
    "expenses": {
      "costOfGoodsSold": 1800000.00,
      "operatingExpenses": 450000.00,
      "commissions": 85000.00,
      "totalExpenses": 2335000.00
    },
    "netProfit": 1215000.00,
    "profitMargin": 34.23
  }
}
```

### GET /api/reports/balance-sheet
**Description**: Balance sheet as of a specific date  
**Query Parameters**:
- `asOfDate`: "2026-03-31" (required)

### GET /api/reports/customers
**Description**: Customer analytics report  
**Query Parameters**:
- `dateFrom`: "2026-01-01" (required)
- `dateTo`: "2026-03-31" (required)

---

## API Response Format

### Success Response
```json
{
  "status": "success",
  "data": { ... },
  "message": "Optional success message",
  "timestamp": "2026-03-17T15:30:00"
}
```

### Error Response
```json
{
  "status": "error",
  "message": "Error description",
  "code": "ERROR_CODE",
  "timestamp": "2026-03-17T15:30:00"
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

---

## Authentication

All endpoints except `/api/auth/login` require JWT authentication.

**Header Format**:
```
Authorization: Bearer <your-jwt-token>
```

**Example**:
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## Total Endpoints Implemented

- **Authentication**: 1 endpoint
- **Dashboard**: 1 endpoint
- **Products**: 5 endpoints
- **Customers**: 6 endpoints
- **Sales Orders**: 4 endpoints
- **Payments**: 3 endpoints
- **Inventory**: 2 endpoints
- **Production**: 3 endpoints
- **Users**: 4 endpoints
- **Suppliers**: 3 endpoints
- **Commissions**: 6 endpoints
- **Reports**: 7 endpoints

**TOTAL: 45 API Endpoints** ✅

---

## Quick Start

1. **Start the application**:
   ```bash
   mvn spring-boot:run
   ```

2. **Login to get JWT token**:
   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"admin@factory.com","password":"SecurePass123"}'
   ```

3. **Use the token for subsequent requests**:
   ```bash
   curl -X GET http://localhost:8080/api/dashboard/summary \
     -H "Authorization: Bearer YOUR_TOKEN_HERE"
   ```

4. **Access Swagger Documentation**:
   ```
   http://localhost:8080/swagger-ui.html
   ```

---

## Status: ✅ PRODUCTION READY

All endpoints are fully implemented with:
- ✅ JWT Authentication
- ✅ Input Validation
- ✅ Error Handling
- ✅ Pagination Support
- ✅ Transaction Management
- ✅ Business Logic
- ✅ Database Integration
- ✅ Security Configuration
