package com.nttdata.bankapp.mscreditservice.model.enums;

/**
 * Estados posibles de un crédito.
 */
public enum CreditStatus {
    ACTIVE, // Crédito activo y al día
    PAID,   // Crédito pagado completamente
    OVERDUE // Crédito con pagos vencidos
}
