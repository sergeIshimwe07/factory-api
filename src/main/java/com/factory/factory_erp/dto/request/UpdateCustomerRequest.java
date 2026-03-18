package com.factory.factory_erp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerRequest {
    
    private String name;
    private String type;
    private String email;
    private String phone;
    private String address;
    private String city;
    private BigDecimal creditLimit;
}
