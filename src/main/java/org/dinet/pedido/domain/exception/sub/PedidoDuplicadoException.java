package org.dinet.pedido.domain.exception.sub;

import org.dinet.pedido.domain.exception.PedidoDomainException;

public class PedidoDuplicadoException extends PedidoDomainException {
    public PedidoDuplicadoException(String numeroPedido) {
        super("Pedido duplicado: " + numeroPedido, "PEDIDO_DUPLICADO");
    }
}
