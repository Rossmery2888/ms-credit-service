package com.example.mscreditservice.model;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Credit {
    private String id;
    private String creditType; // PERSONAL o EMPRESARIAL
    private String customerId;
    private BigDecimal amount;
    private BigDecimal outstandingBalance;
    private String status; // ACTIVE, PAID, CANCELED
}



