-- Create supplier_orders table
CREATE TABLE IF NOT EXISTS supplier_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_order_id VARCHAR(50) UNIQUE NOT NULL,
    supplier_id BIGINT NOT NULL,
    delivery_date TIMESTAMP NULL,
    notes TEXT,
    status ENUM('draft', 'sent', 'confirmed', 'partial_received', 'received', 'invoiced', 'paid', 'cancelled') DEFAULT 'draft' NOT NULL,
    proof_of_payment VARCHAR(255),
    invoice VARCHAR(255),
    subtotal DECIMAL(15, 2) DEFAULT 0.00,
    tax DECIMAL(15, 2) DEFAULT 0.00,
    total DECIMAL(15, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_supplier_orders_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE RESTRICT,
    INDEX idx_supplier_order_id (supplier_order_id),
    INDEX idx_supplier_id (supplier_id),
    INDEX idx_status (status),
    INDEX idx_delivery_date (delivery_date),
    INDEX idx_created_at (created_at)
);

-- Create supplier_order_items table
CREATE TABLE IF NOT EXISTS supplier_order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_order_id BIGINT NOT NULL,
    raw_material_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(15, 2) NOT NULL,
    total DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_supplier_order_items_order FOREIGN KEY (supplier_order_id) REFERENCES supplier_orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_supplier_order_items_material FOREIGN KEY (raw_material_id) REFERENCES products(id) ON DELETE RESTRICT,
    INDEX idx_supplier_order_id (supplier_order_id),
    INDEX idx_raw_material_id (raw_material_id)
);
