package org.dinet.shared.exception;

public class IdempotenciaErrorException extends RuntimeException {
    public IdempotenciaErrorException(String message) {
        super(message);
    }
}
