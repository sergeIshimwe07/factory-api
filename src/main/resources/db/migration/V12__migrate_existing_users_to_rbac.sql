-- Migrate existing users from old role enum to new RBAC system
-- This migration assumes V11 has already created the RBAC tables

-- First, add a temporary column to store old role values
ALTER TABLE users ADD COLUMN old_role VARCHAR(50) NULL;

-- Copy existing role values to temporary column (if role column exists)
-- Note: This will fail gracefully if the column doesn't exist
UPDATE users SET old_role = role WHERE role IS NOT NULL;

-- Drop the old role column
ALTER TABLE users DROP COLUMN role;

-- Assign roles to existing users based on their old role values
-- Admin users
INSERT INTO user_roles (user_id, role_id, is_active)
SELECT u.id, rg.id, TRUE
FROM users u
CROSS JOIN role_groups rg
WHERE u.old_role = 'admin' AND rg.role_code = 'ADMIN';

-- Sales Agent users
INSERT INTO user_roles (user_id, role_id, is_active)
SELECT u.id, rg.id, TRUE
FROM users u
CROSS JOIN role_groups rg
WHERE u.old_role = 'sales_agent' AND rg.role_code = 'SALES_AGENT';

-- Production Manager users
INSERT INTO user_roles (user_id, role_id, is_active)
SELECT u.id, rg.id, TRUE
FROM users u
CROSS JOIN role_groups rg
WHERE u.old_role = 'production_manager' AND rg.role_code = 'PRODUCTION_MANAGER';

-- Accountant users
INSERT INTO user_roles (user_id, role_id, is_active)
SELECT u.id, rg.id, TRUE
FROM users u
CROSS JOIN role_groups rg
WHERE u.old_role = 'accountant' AND rg.role_code = 'ACCOUNTANT';

-- Store Manager users
INSERT INTO user_roles (user_id, role_id, is_active)
SELECT u.id, rg.id, TRUE
FROM users u
CROSS JOIN role_groups rg
WHERE u.old_role = 'store_manager' AND rg.role_code = 'STORE_MANAGER';

-- Drop the temporary column
ALTER TABLE users DROP COLUMN old_role;
