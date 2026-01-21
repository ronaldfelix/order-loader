package org.dinet.pedido.domain.exception;

public abstract class PedidoDomainException extends RuntimeException {
    private final String errorCode;

    protected PedidoDomainException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() { return errorCode; }
}

