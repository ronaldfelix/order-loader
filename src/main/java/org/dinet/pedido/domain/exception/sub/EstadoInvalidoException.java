package org.dinet.pedido.domain.exception.sub;

import org.dinet.pedido.domain.exception.PedidoDomainException;

public class EstadoInvalidoException extends PedidoDomainException {
    public EstadoInvalidoException(String message) {
        super(message, "ESTADO_INVALIDO");
    }
}
