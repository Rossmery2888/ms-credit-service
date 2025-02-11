package com.example.mscreditservice.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CreditResponseDTO {
    private String id;
    private String customerId;
    private String customerType;
    private BigDecimal amount;
    private BigDecimal remainingAmount;
    private Integer term;
    private BigDecimal interestRate;
    private LocalDateTime createdAt;
    private String status;
}