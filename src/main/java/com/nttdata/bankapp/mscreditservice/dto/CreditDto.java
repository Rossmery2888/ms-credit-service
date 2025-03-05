package com.nttdata.bankapp.mscreditservice.dto;

import com.nttdata.bankapp.mscreditservice.model.enums.CreditType;
import com.nttdata.bankapp.mscreditservice.model.enums.CustomerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para la transferencia de datos de créditos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditDto {
    private String id;
    private String creditNumber;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotNull(message = "Customer type is required")
    private CustomerType customerType;

    @NotNull(message = "Credit type is required")
    private CreditType type;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private BigDecimal remainingAmount;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Interest rate must be non-negative")
    private BigDecimal interestRate;

    @NotNull(message = "Term is required")
    @Min(value = 1, message = "Term must be at least 1 month")
    private Integer term;

    private LocalDate startDate;
    private LocalDate dueDate;
}