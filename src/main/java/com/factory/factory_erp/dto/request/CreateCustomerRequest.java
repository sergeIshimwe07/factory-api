package com.factory.factory_erp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerRequest {
    
    @NotBlank(message = "Customer name is required")
    private String name;
    
    @NotNull(message = "Customer type is required")
    private String type;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String phone;
    private String address;
    private String city;
    private BigDecimal creditLimit;
}
