package com.example.mscreditservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditRequest {
    private String customerId;
    private BigDecimal amount;
    private BigDecimal outstandingBalance;
}