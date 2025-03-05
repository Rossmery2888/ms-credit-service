package com.nttdata.bankapp.mscreditservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para la información de saldo de crédito.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditBalanceDto {
    private String creditId;
    private String creditNumber;
    private BigDecimal totalAmount;
    private BigDecimal remainingAmount;
    private BigDecimal paidAmount;
}
