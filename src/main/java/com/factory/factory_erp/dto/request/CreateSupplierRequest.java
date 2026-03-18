package com.factory.factory_erp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSupplierRequest {
    
    @NotBlank(message = "Supplier name is required")
    private String name;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String phone;
    private String address;
    private String contactPerson;
}
