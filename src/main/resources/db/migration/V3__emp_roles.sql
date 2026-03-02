create table role_groups (
    id int auto_increment primary key,
    name varchar(255) not null,
    description VARCHAR(255),
    status int DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
 );

 create table employee_roles (
    id int auto_increment primary key,
    employee_id int,
    role_group_id int,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    oparetor BIGINT DEFAULT 1,
    status int DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
 );