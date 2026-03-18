-- Products Table
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id VARCHAR(50) UNIQUE NOT NULL,
    sku VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(100),
    unit ENUM('piece', 'kg', 'liter', 'meter', 'box', 'set') DEFAULT 'piece',
    base_price DECIMAL(15, 2) NOT NULL,
    cost_price DECIMAL(15, 2) NOT NULL,
    current_stock INT DEFAULT 0,
    minimum_stock INT DEFAULT 0,
    reorder_level INT DEFAULT 0,
    weight DECIMAL(10, 2),
    description TEXT,
    status ENUM('active', 'inactive', 'discontinued') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_product_id (product_id),
    INDEX idx_sku (sku),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_current_stock (current_stock)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Stock Movements Table
CREATE TABLE stock_movements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movement_id VARCHAR(50) UNIQUE NOT NULL,
    product_id BIGINT NOT NULL,
    type ENUM('receipt', 'issue', 'adjustment', 'production', 'return') NOT NULL,
    quantity INT NOT NULL,
    reference VARCHAR(100),
    notes TEXT,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_movement_id (movement_id),
    INDEX idx_product_id (product_id),
    INDEX idx_type (type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bill of Materials (BOM)
CREATE TABLE bill_of_materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bom_id VARCHAR(50) UNIQUE NOT NULL,
    finished_product_id BIGINT NOT NULL,
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (finished_product_id) REFERENCES products(id) ON DELETE RESTRICT,
    INDEX idx_bom_id (bom_id),
    INDEX idx_finished_product_id (finished_product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- BOM Items (Raw Materials)
CREATE TABLE bom_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bom_id BIGINT NOT NULL,
    raw_material_id BIGINT NOT NULL,
    quantity_required DECIMAL(10, 2) NOT NULL,
    unit VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bom_id) REFERENCES bill_of_materials(id) ON DELETE CASCADE,
    FOREIGN KEY (raw_material_id) REFERENCES products(id) ON DELETE RESTRICT,
    INDEX idx_bom_id (bom_id),
    INDEX idx_raw_material_id (raw_material_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
