-- Production Entries Table
CREATE TABLE production_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    production_id VARCHAR(50) UNIQUE NOT NULL,
    batch_number VARCHAR(100) UNIQUE NOT NULL,
    bom_id BIGINT NOT NULL,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    quantity_produced INT NOT NULL,
    quantity_defective INT DEFAULT 0,
    supervisor VARCHAR(100),
    status ENUM('planned', 'in_progress', 'completed', 'cancelled') DEFAULT 'planned',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (bom_id) REFERENCES bill_of_materials(id) ON DELETE RESTRICT,
    INDEX idx_production_id (production_id),
    INDEX idx_batch_number (batch_number),
    INDEX idx_bom_id (bom_id),
    INDEX idx_status (status),
    INDEX idx_start_date (start_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
