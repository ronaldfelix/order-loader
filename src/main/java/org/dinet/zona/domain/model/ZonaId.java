package org.dinet.zona.domain.model;

public record ZonaId(String value) {
    public ZonaId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ZonaId no puede estar vacío");
        }
    }
}