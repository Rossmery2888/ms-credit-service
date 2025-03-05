package com.nttdata.bankapp.mscreditservice.exception;

/**
 * Excepción personalizada para tipo de crédito inválido.
 */
public class InvalidCreditTypeException extends RuntimeException {
    public InvalidCreditTypeException(String message) {
        super(message);
    }
}