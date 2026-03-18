-- Commission Rules Table
CREATE TABLE commission_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_id VARCHAR(50) UNIQUE NOT NULL,
    product_id BIGINT NOT NULL,
    type ENUM('percentage', 'fixed') NOT NULL,
    value DECIMAL(10, 2) NOT NULL,
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
    INDEX idx_rule_id (rule_id),
    INDEX idx_product_id (product_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Commissions Table
CREATE TABLE commissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    commission_id VARCHAR(50) UNIQUE NOT NULL,
    agent_id BIGINT NOT NULL,
    period VARCHAR(7) NOT NULL,
    sale_amount DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    rate DECIMAL(5, 2) NOT NULL,
    commission DECIMAL(15, 2) NOT NULL,
    status ENUM('unpaid', 'paid') DEFAULT 'unpaid',
    reference VARCHAR(100),
    paid_date DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (agent_id) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_commission_id (commission_id),
    INDEX idx_agent_id (agent_id),
    INDEX idx_period (period),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
