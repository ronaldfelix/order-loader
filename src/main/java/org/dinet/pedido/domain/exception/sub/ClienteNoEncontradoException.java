package org.dinet.pedido.domain.exception.sub;

import org.dinet.pedido.domain.exception.PedidoDomainException;

public class ClienteNoEncontradoException extends PedidoDomainException {
    public ClienteNoEncontradoException(String clienteId) {
        super("Cliente no encontrado: " + clienteId, "CLIENTE_NO_ENCONTRADO");
    }
}
