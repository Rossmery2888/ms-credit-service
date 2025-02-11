package com.example.mscreditservice.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreditRequestDTO {
    private String customerId;
    private String customerType;
    private BigDecimal amount;
    private Integer term;
}