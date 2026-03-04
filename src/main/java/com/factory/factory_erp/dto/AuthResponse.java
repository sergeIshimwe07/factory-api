package com.factory.factory_erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    
    @Builder.Default
    private String type = "Bearer";
    
    private Long id;
    private String firstName;
    private String lastName;
    private String names;
    private String email;
    private Integer position;
    private String employeeType;
    private BigDecimal salary;
    private LocalDateTime lastLogin;
    private Integer status;
    private Boolean canLogin;
}
