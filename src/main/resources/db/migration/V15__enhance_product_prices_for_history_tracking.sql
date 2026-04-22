-- V15__enhance_product_price_for_history_tracking.sql
-- Enhancement: Decouple product prices from products to maintain historical accuracy
-- When a product price changes, past records and reports retain accurate historical data

-- Add new columns to product_price table for proper price history tracking
ALTER TABLE product_price 
ADD COLUMN end_date DATE NULL AFTER start_date,
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' AFTER amount,
ADD COLUMN currency VARCHAR(3) NOT NULL DEFAULT 'USD' AFTER status,
ADD COLUMN notes TEXT NULL AFTER currency;

-- Create indexes for efficient historical price queries
CREATE INDEX idx_product_id_start_date ON product_price(product_id, start_date);
CREATE INDEX idx_product_id_end_date ON product_price(product_id, end_date);
CREATE INDEX idx_status ON product_price(status);

-- Set end_date for existing records to NULL (ongoing prices)
-- This ensures all existing prices are treated as ACTIVE with no expiration date
UPDATE product_price SET end_date = NULL WHERE end_date IS NULL;

-- Add comment to explain the design
ALTER TABLE product_price COMMENT = 'Stores price history decoupled from products. When prices change, historical records and reports retain accurate past data.';