package com.example.mscreditservice.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "credits")
public class Credit {
    @Id
    private String id;
    private String creditType; // PERSONAL o EMPRESARIAL
    private String customerId;
    private BigDecimal amount;
    private BigDecimal outstandingBalance;
    private String status; // ACTIVE, PAID, CANCELED
    private LocalDate dueDate;
    private boolean overdue;
    private String lastPaymentBy; // ID del cliente que hizo el último pago (para pagos de terceros)
    private LocalDate lastPaymentDate;

    public Credit(String id, String creditType, String customerId, BigDecimal amount,
                  BigDecimal outstandingBalance, String status) {
        this.id = id;
        this.creditType = creditType;
        this.customerId = customerId;
        this.amount = amount;
        this.outstandingBalance = outstandingBalance;
        this.status = status;
    }
}


