-- Features Table (All system features)
CREATE TABLE features (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    feature_code VARCHAR(50) UNIQUE NOT NULL,
    feature_name VARCHAR(100) NOT NULL,
    description TEXT,
    module VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_feature_code (feature_code),
    INDEX idx_module (module)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Role Groups Table
CREATE TABLE role_groups (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code VARCHAR(50) UNIQUE NOT NULL,
    role_name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Role Permissions Table (Links roles to features with specific permissions)
CREATE TABLE role_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    feature_id BIGINT NOT NULL,
    can_read BOOLEAN DEFAULT FALSE,
    can_edit BOOLEAN DEFAULT FALSE,
    can_delete BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES role_groups(id) ON DELETE CASCADE,
    FOREIGN KEY (feature_id) REFERENCES features(id) ON DELETE CASCADE,
    UNIQUE KEY unique_role_feature (role_id, feature_id),
    INDEX idx_role_id (role_id),
    INDEX idx_feature_id (feature_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User Roles Table (Many-to-Many relationship between users and role groups)
CREATE TABLE user_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by BIGINT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role_groups(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_by) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE KEY unique_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed Features
INSERT INTO features (feature_code, feature_name, description, module) VALUES
-- User Management
('USER_MANAGEMENT', 'User Management', 'Manage system users and their accounts', 'ADMINISTRATION'),
('ROLE_MANAGEMENT', 'Role Management', 'Manage roles and permissions', 'ADMINISTRATION'),

-- Customer & Supplier Management
('CUSTOMER_MANAGEMENT', 'Customer Management', 'Manage customer records', 'CRM'),
('SUPPLIER_MANAGEMENT', 'Supplier Management', 'Manage supplier records', 'PROCUREMENT'),

-- Product & Inventory
('PRODUCT_MANAGEMENT', 'Product Management', 'Manage product catalog', 'INVENTORY'),
('INVENTORY_MANAGEMENT', 'Inventory Management', 'Manage stock and inventory', 'INVENTORY'),
('BOM_MANAGEMENT', 'Bill of Materials', 'Manage product BOMs', 'PRODUCTION'),

-- Sales & Orders
('SALES_ORDER_MANAGEMENT', 'Sales Orders', 'Manage sales orders', 'SALES'),
('QUOTATION_MANAGEMENT', 'Quotations', 'Manage sales quotations', 'SALES'),
('INVOICE_MANAGEMENT', 'Invoices', 'Manage invoices', 'SALES'),

-- Purchase Orders
('PURCHASE_ORDER_MANAGEMENT', 'Purchase Orders', 'Manage purchase orders', 'PROCUREMENT'),

-- Production
('PRODUCTION_MANAGEMENT', 'Production Management', 'Manage production entries', 'PRODUCTION'),

-- Commissions
('COMMISSION_MANAGEMENT', 'Commission Management', 'Manage sales commissions', 'SALES'),

-- Accounting
('ACCOUNT_MANAGEMENT', 'Chart of Accounts', 'Manage accounting accounts', 'ACCOUNTING'),
('JOURNAL_ENTRY_MANAGEMENT', 'Journal Entries', 'Manage journal entries', 'ACCOUNTING'),
('FINANCIAL_REPORTS', 'Financial Reports', 'View financial reports', 'ACCOUNTING'),

-- Reports & Analytics
('SALES_REPORTS', 'Sales Reports', 'View sales reports and analytics', 'REPORTS'),
('INVENTORY_REPORTS', 'Inventory Reports', 'View inventory reports', 'REPORTS'),
('PRODUCTION_REPORTS', 'Production Reports', 'View production reports', 'REPORTS');

-- Seed Role Groups
INSERT INTO role_groups (role_code, role_name, description) VALUES
('ADMIN', 'Administrator', 'Full system access with all permissions'),
('SALES_AGENT', 'Sales Agent', 'Sales and customer management access'),
('PRODUCTION_MANAGER', 'Production Manager', 'Production and inventory management access'),
('ACCOUNTANT', 'Accountant', 'Accounting and financial management access'),
('STORE_MANAGER', 'Store Manager', 'Inventory and warehouse management access'),
('PROCUREMENT_OFFICER', 'Procurement Officer', 'Purchase and supplier management access');

-- Seed Role Permissions
-- ADMIN - Full access to everything
INSERT INTO role_permissions (role_id, feature_id, can_read, can_edit, can_delete)
SELECT r.id, f.id, TRUE, TRUE, TRUE
FROM role_groups r
CROSS JOIN features f
WHERE r.role_code = 'ADMIN';

-- SALES_AGENT Permissions
INSERT INTO role_permissions (role_id, feature_id, can_read, can_edit, can_delete)
SELECT r.id, f.id, TRUE, TRUE, TRUE
FROM role_groups r
CROSS JOIN features f
WHERE r.role_code = 'SALES_AGENT'
AND f.feature_code IN ('CUSTOMER_MANAGEMENT', 'SALES_ORDER_MANAGEMENT', 'QUOTATION_MANAGEMENT', 'INVOICE_MANAGEMENT', 'COMMISSION_MANAGEMENT');

INSERT INTO role_permissions (role_id, feature_id, can_read, can_edit, can_delete)
SELECT r.id, f.id, TRUE, FALSE, FALSE
FROM role_groups r
CROSS JOIN features f
WHERE r.role_code = 'SALES_AGENT'
AND f.feature_code IN ('PRODUCT_MANAGEMENT', 'INVENTORY_MANAGEMENT', 'SALES_REPORTS');

-- PRODUCTION_MANAGER Permissions
INSERT INTO role_permissions (role_id, feature_id, can_read, can_edit, can_delete)
SELECT r.id, f.id, TRUE, TRUE, TRUE
FROM role_groups r
CROSS JOIN features f
WHERE r.role_code = 'PRODUCTION_MANAGER'
AND f.feature_code IN ('PRODUCTION_MANAGEMENT', 'BOM_MANAGEMENT', 'INVENTORY_MANAGEMENT');

INSERT INTO role_permissions (role_id, feature_id, can_read, can_edit, can_delete)
SELECT r.id, f.id, TRUE, FALSE, FALSE
FROM role_groups r
CROSS JOIN features f
WHERE r.role_code = 'PRODUCTION_MANAGER'
AND f.feature_code IN ('PRODUCT_MANAGEMENT', 'PRODUCTION_REPORTS', 'INVENTORY_REPORTS');

-- ACCOUNTANT Permissions
INSERT INTO role_permissions (role_id, feature_id, can_read, can_edit, can_delete)
SELECT r.id, f.id, TRUE, TRUE, TRUE
FROM role_groups r
CROSS JOIN features f
WHERE r.role_code = 'ACCOUNTANT'
AND f.feature_code IN ('ACCOUNT_MANAGEMENT', 'JOURNAL_ENTRY_MANAGEMENT', 'INVOICE_MANAGEMENT');

INSERT INTO role_permissions (role_id, feature_id, can_read, can_edit, can_delete)
SELECT r.id, f.id, TRUE, FALSE, FALSE
FROM role_groups r
CROSS JOIN features f
WHERE r.role_code = 'ACCOUNTANT'
AND f.feature_code IN ('FINANCIAL_REPORTS', 'SALES_REPORTS', 'SALES_ORDER_MANAGEMENT', 'PURCHASE_ORDER_MANAGEMENT');

-- STORE_MANAGER Permissions
INSERT INTO role_permissions (role_id, feature_id, can_read, can_edit, can_delete)
SELECT r.id, f.id, TRUE, TRUE, TRUE
FROM role_groups r
CROSS JOIN features f
WHERE r.role_code = 'STORE_MANAGER'
AND f.feature_code IN ('INVENTORY_MANAGEMENT', 'PRODUCT_MANAGEMENT');

INSERT INTO role_permissions (role_id, feature_id, can_read, can_edit, can_delete)
SELECT r.id, f.id, TRUE, FALSE, FALSE
FROM role_groups r
CROSS JOIN features f
WHERE r.role_code = 'STORE_MANAGER'
AND f.feature_code IN ('INVENTORY_REPORTS', 'PURCHASE_ORDER_MANAGEMENT', 'SALES_ORDER_MANAGEMENT', 'PRODUCTION_MANAGEMENT');

-- PROCUREMENT_OFFICER Permissions
INSERT INTO role_permissions (role_id, feature_id, can_read, can_edit, can_delete)
SELECT r.id, f.id, TRUE, TRUE, TRUE
FROM role_groups r
CROSS JOIN features f
WHERE r.role_code = 'PROCUREMENT_OFFICER'
AND f.feature_code IN ('SUPPLIER_MANAGEMENT', 'PURCHASE_ORDER_MANAGEMENT');

INSERT INTO role_permissions (role_id, feature_id, can_read, can_edit, can_delete)
SELECT r.id, f.id, TRUE, FALSE, FALSE
FROM role_groups r
CROSS JOIN features f
WHERE r.role_code = 'PROCUREMENT_OFFICER'
AND f.feature_code IN ('PRODUCT_MANAGEMENT', 'INVENTORY_MANAGEMENT', 'INVENTORY_REPORTS');
