create table employees (
    id int auto_increment primary key,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    names varchar(255) not null,
    email varchar(255) not null unique,
    position int not null,
    `password` varchar(255) DEFAULT NULL,
    employee_type varchar(255) DEFAULT NULL,
    salary decimal(10, 2) DEFAULT NULL,
    last_login TIMESTAMP not null,
    status int DEFAULT 1,
    can_login BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);