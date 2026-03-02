insert into role_groups (name, description) values
('Admin', 'Administrators with full access to the system'),
('Manager', 'Managers with access to managerial functions'),
('Employee', 'Regular employees with limited access');

insert into employee_roles (employee_id, role_group_id, assigned_at, oparetor) values
(1, 1, now(),1);