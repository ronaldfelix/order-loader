package org.dinet.pedido.domain.model;

public record ClienteId(String value) {
    public ClienteId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ClienteId no puede estar vacío");
        }
    }
}