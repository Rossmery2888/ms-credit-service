package com.example.mscreditservice.dto;

import com.example.mscreditservice.model.enums.CreditType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class CreditDTO {
    private String id;
    @NotBlank
    private String customerId;
    @NotNull
    private CreditType creditType;
    @NotNull
    @Positive
    private BigDecimal amount;
    @NotNull
    @Positive
    private BigDecimal monthlyPayment;
    @NotNull
    private Integer numberOfPayments;
}