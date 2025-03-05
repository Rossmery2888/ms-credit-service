package com.nttdata.bankapp.mscreditservice.exception;

/**
 * Excepción personalizada para cliente no encontrado.
 */
public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}