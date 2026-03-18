-- Seed Default Admin User (password: SecurePass123)
-- Password is BCrypt hashed
INSERT INTO users (user_id, name, email, password, role, is_active) VALUES
('usr_001', 'Admin User', 'admin@factory.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin', TRUE);

-- Seed Chart of Accounts
INSERT INTO accounts (account_id, code, name, type, balance, status) VALUES
('acc_1000', '1000', 'Cash', 'asset', 500000.00, 'active'),
('acc_1100', '1100', 'Sales Revenue', 'revenue', 2850000.00, 'active'),
('acc_1200', '1200', 'Accounts Receivable', 'asset', 450000.00, 'active'),
('acc_1300', '1300', 'Inventory', 'asset', 2500000.00, 'active'),
('acc_1400', '1400', 'Fixed Assets', 'asset', 5000000.00, 'active'),
('acc_2000', '2000', 'Accounts Payable', 'liability', 250000.00, 'active'),
('acc_2100', '2100', 'Long Term Debt', 'liability', 1000000.00, 'active'),
('acc_3000', '3000', 'Capital Stock', 'equity', 5000000.00, 'active'),
('acc_3100', '3100', 'Retained Earnings', 'equity', 2200000.00, 'active'),
('acc_4000', '4000', 'Cost of Goods Sold', 'expense', 1800000.00, 'active'),
('acc_4100', '4100', 'Operating Expenses', 'expense', 450000.00, 'active'),
('acc_4200', '4200', 'Commission Expense', 'expense', 85000.00, 'active');
