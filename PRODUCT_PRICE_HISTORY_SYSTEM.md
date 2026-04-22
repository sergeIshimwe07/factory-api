# Product Price History System - Design Documentation

## Overview
The Product Price system is designed with **historical price tracking** to decouple product prices from products themselves. This ensures that when prices change, all past records and reports retain the accurate historical prices they had at the time of transactions.

## Problem Solved
### Without Decoupling
If prices were tied to the Product entity:
- When you update a product's current price, historical sales and reports would show incorrect prices
- You couldn't accurately report on what price was used for past transactions
- Historical data integrity is lost

### With Decoupling (Our Solution)
By keeping prices separate and maintaining a price history:
- Past transactions retain their exact historical prices
- Reports always show accurate data for their time period
- Price changes don't affect historical records
- You have a complete audit trail of all price changes

## Entity Design

### ProductPrice Entity
```
ProductPrice {
  id: Long (auto-generated)
  productId: Long (not a foreign key - intentionally decoupled)
  startDate: LocalDate (when this price becomes effective)
  endDate: LocalDate (when this price expires, null = ongoing)
  amount: BigDecimal (the price)
  currency: String (e.g., "USD", default: "USD")
  status: PriceStatus (ACTIVE, EXPIRED, SUPERSEDED, ARCHIVED)
  notes: String (reason for price change, etc.)
  operator: User (who created this price)
  createdAt: LocalDateTime (inheritance from BaseEntity)
  updatedAt: LocalDateTime (inheritance from BaseEntity)
}

PriceStatus Enum:
- ACTIVE: Currently active price
- EXPIRED: Price validity has ended
- SUPERSEDED: Price has been replaced by a newer price
- ARCHIVED: Price no longer in use but kept for historical records
```

### Key Features
1. **Decoupled from Product**: Uses `productId` as a Long field, not a foreign key relationship
2. **Time-bounded**: Each price has a `startDate` and optional `endDate`
3. **Status Tracking**: Tracks whether a price is active, expired, or superseded
4. **Audit Trail**: Links to the User who created/modified the price
5. **Notes Field**: Allows explanation for price changes

## Usage Patterns

### Get Current Price
```java
Optional<ProductPrice> currentPrice = productPriceService.getCurrentPrice(productId);
```

### Get Price on Specific Date (for historical reports)
```java
Optional<ProductPrice> price = productPriceService.getActivePriceOn(productId, reportDate);
```

### Create New Price
```java
ProductPrice newPrice = productPriceService.createPrice(
    productId,
    new BigDecimal("99.99"),
    "USD",
    operator,
    LocalDate.now(),
    "New wholesale pricing"
);
// This automatically expires the old price
```

### Get Price History
```java
List<ProductPrice> history = productPriceService.getPriceChangeHistory(productId);
```

### Get Prices for a Period
```java
List<ProductPrice> prices = productPriceService.getPriceHistoryForPeriod(
    productId,
    startDate,
    endDate
);
```

## Database Schema

### product_price Table
```sql
CREATE TABLE product_price (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_id BIGINT NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NULL,
  amount DECIMAL(15,2) NOT NULL,
  currency VARCHAR(3) NOT NULL DEFAULT 'USD',
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  notes TEXT NULL,
  operator BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  
  INDEX idx_product_id_start_date (product_id, start_date),
  INDEX idx_product_id_end_date (product_id, end_date),
  INDEX idx_status (status),
  
  FOREIGN KEY (operator) REFERENCES users(id)
);
```

### Indexes
- `idx_product_id_start_date`: Optimizes queries for active prices
- `idx_product_id_end_date`: Optimizes queries for expired prices
- `idx_status`: Optimizes filtering by price status

## REST API Endpoints

### Get Current Price
```
GET /api/product-prices/product/{productId}/current
```

### Get Price on Specific Date
```
GET /api/product-prices/product/{productId}/on-date?date=2026-04-22
```

### Get Price History (all prices, sorted by date)
```
GET /api/product-prices/product/{productId}/history
```

### Get Prices for Period
```
GET /api/product-prices/product/{productId}/period
  ?startDate=2026-01-01&endDate=2026-04-22
```

### Create New Price
```
POST /api/product-prices
{
  "productId": 1,
  "amount": 99.99,
  "currency": "USD",
  "operatorId": 123,
  "startDate": "2026-05-01",
  "notes": "Quarterly price increase"
}
```

### Get Archived Prices
```
GET /api/product-prices/product/{productId}/archived
```

## Benefits

1. **Data Integrity**: Historical records never become inaccurate
2. **Audit Trail**: Complete history of all price changes with timestamps and operator info
3. **Flexible Reporting**: Can generate reports at any point in time with correct prices
4. **Price Analysis**: Track pricing trends over time
5. **Compliance**: Maintain accurate records for audits and compliance
6. **Flexibility**: Support multiple currencies and status tracking

## Migration Path

Migration `V15__enhance_product_price_for_history_tracking.sql` adds the new columns to existing `product_price` table:
- `end_date`: NULL for all existing prices (treat as ongoing)
- `status`: Set to 'ACTIVE' for all existing prices
- `currency`: Defaults to 'USD'
- `notes`: NULL for all existing prices

No data loss or backward compatibility issues.

## Service Methods

The `ProductPriceService` provides high-level operations:

- `getActivePriceOn(productId, date)`: Get price valid on a date
- `getCurrentPrice(productId)`: Get today's active price
- `getPriceHistoryForPeriod(productId, startDate, endDate)`: Get prices in a range
- `createPrice(...)`: Create new price and expire old one
- `getPriceChangeHistory(productId)`: Get all price changes
- `expirePrice(price, expiryDate)`: Manually expire a price
- `markAsSuperseeded(price)`: Mark price as superseded
- `getArchivedPrices(productId)`: Get archived prices

## Example Scenarios

### Scenario 1: Monthly Price Adjustment
1. Current price: $100 (ACTIVE, no end_date)
2. Customer wants to increase to $110 starting May 1
3. Call `createPrice(..., 110, "2026-05-01", ...)`
4. System automatically:
   - Expires old price ($100) with end_date = 2026-04-30
   - Creates new price ($110) with start_date = 2026-05-01
5. Reports for April show $100, May onwards show $110

### Scenario 2: Historical Accuracy
1. Sales order created on March 1 with price $80
2. Product price changes to $90 on March 15
3. Historical system shows:
   - March 1 had price $80 (correct for that date)
   - March 15+ has price $90
4. Invoice shows correct historical price regardless of current price

### Scenario 3: Audit Trail
1. Track why prices changed: "Supplier cost increased due to new tariffs"
2. See who approved each price change
3. Timestamps show exactly when changes took effect
4. Complete history for compliance audit
