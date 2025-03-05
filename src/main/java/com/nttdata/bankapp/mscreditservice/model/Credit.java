package com.nttdata.bankapp.mscreditservice.model;
import com.nttdata.bankapp.mscreditservice.model.enums.CreditStatus;
import com.nttdata.bankapp.mscreditservice.model.enums.CreditType;
import com.nttdata.bankapp.mscreditservice.model.enums.CustomerProfile;
import com.nttdata.bankapp.mscreditservice.model.enums.CustomerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modelo actualizado para créditos, incluyendo estado de vencimiento.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "credits")
public class Credit {
    @Id
    private String id;

    @Indexed(unique = true)
    private String creditNumber;
    private String customerId;
    private CustomerType customerType; // PERSONAL, BUSINESS
    private CustomerProfile customerProfile; // REGULAR, VIP, PYME
    private CreditType type; // PERSONAL, BUSINESS
    private BigDecimal amount;
    private BigDecimal remainingAmount;
    private BigDecimal interestRate;
    private Integer term; // en meses
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate nextPaymentDueDate; // Fecha de vencimiento del próximo pago
    private CreditStatus status; // ACTIVE, PAID, OVERDUE
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}