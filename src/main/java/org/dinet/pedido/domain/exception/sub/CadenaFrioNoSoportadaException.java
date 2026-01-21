package org.dinet.pedido.domain.exception.sub;

import org.dinet.pedido.domain.exception.PedidoDomainException;

public class CadenaFrioNoSoportadaException extends PedidoDomainException {
    public CadenaFrioNoSoportadaException(String message) {
        super(message, "CADENA_FRIO_NO_SOPORTADA");
    }
}
