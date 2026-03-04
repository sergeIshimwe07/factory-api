package com.factory.factory_erp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

// create table employees (
//     id int auto_increment primary key,
//     first_name varchar(255) not null,
//     last_name varchar(255) not null,
//     names varchar(255) not null,
//     position int not null,
//     `password` varchar(255) DEFAULT NULL,
//     employee_type varchar(255) DEFAULT NULL,
//     salary decimal(10, 2) DEFAULT NULL,
//     last_login TIMESTAMP not null,
//     status int DEFAULT 1,
//     can_login BOOLEAN DEFAULT FALSE,
//     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
//     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
// );

@Entity
@Table(name = "employees")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(name = "names", nullable = false)
    private String names;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "position", nullable = false)
    private Integer position;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "employee_type")
    private String employeeType;
    
    @Column(name = "salary")
    private java.math.BigDecimal salary;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "status", columnDefinition = "INT DEFAULT 1")
    private Integer status;
    
    @Column(name = "can_login", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean canLogin;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
