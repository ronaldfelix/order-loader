package org.dinet.pedido.domain.exception.sub;

import org.dinet.pedido.domain.exception.PedidoDomainException;

public class NumeroPedidoInvalidoException extends PedidoDomainException {
    public NumeroPedidoInvalidoException(String message) {
        super(message, "NUMERO_PEDIDO_INVALIDO");
    }
}
