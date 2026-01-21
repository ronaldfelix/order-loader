package org.dinet.pedido.domain.exception.sub;

import org.dinet.pedido.domain.exception.PedidoDomainException;

public class ZonaInvalidaException extends PedidoDomainException {
    public ZonaInvalidaException(String zonaId) {
        super("Zona inválida: " + zonaId, "ZONA_INVALIDA");
    }
}
