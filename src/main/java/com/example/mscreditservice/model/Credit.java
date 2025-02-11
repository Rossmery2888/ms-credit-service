package com.example.mscreditservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "credits")
public class Credit {
    @Id
    private String id;
    private String customerId;
    private String customerType; // PERSONAL or BUSINESS
    private BigDecimal amount;
    private BigDecimal remainingAmount;
    private Integer term; // months
    private BigDecimal interestRate;
    private LocalDateTime createdAt;
    private String status; // APPROVED, PENDING, REJECTED
}