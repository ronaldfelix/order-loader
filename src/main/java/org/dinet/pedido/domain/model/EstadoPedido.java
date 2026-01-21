package org.dinet.pedido.domain.model;

import org.dinet.pedido.domain.exception.sub.EstadoInvalidoException;

public enum EstadoPedido {
    PENDIENTE,
    CONFIRMADO,
    ENTREGADO;

    public static EstadoPedido fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new EstadoInvalidoException("El estado no puede estar vacío");
        }
        try {
            return EstadoPedido.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EstadoInvalidoException("Estado inválido: " + value);
        }
    }
}
