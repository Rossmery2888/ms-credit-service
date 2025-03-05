package com.nttdata.bankapp.mscreditservice.exception;

/**
 * Excepción personalizada para crédito no encontrado.
 */
public class CreditNotFoundException extends RuntimeException {
    public CreditNotFoundException(String message) {
        super(message);
    }
}
