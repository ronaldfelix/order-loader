package org.dinet.pedido.domain.exception.sub;

import org.dinet.pedido.domain.exception.PedidoDomainException;

public class FechaEntregaInvalidaException extends PedidoDomainException {
    public FechaEntregaInvalidaException(String message) {
        super(message, "FECHA_INVALIDA");
    }
}
