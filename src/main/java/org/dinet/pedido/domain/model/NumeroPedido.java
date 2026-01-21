package org.dinet.pedido.domain.model;

import org.dinet.pedido.domain.exception.sub.NumeroPedidoInvalidoException;

public record NumeroPedido(String value) {
    public NumeroPedido {
        if (value == null || value.isBlank()) {
            throw new NumeroPedidoInvalidoException("El número de pedido no puede estar vacío");
        }
        if (!value.matches("^[a-zA-Z0-9]+$")) {
            throw new NumeroPedidoInvalidoException("El número de pedido debe ser alfanumérico: " + value);
        }
    }
}
