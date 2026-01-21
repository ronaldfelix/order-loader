package org.dinet.pedido.application.dto;

public record ErrorDetalle(
        int linea,
        String motivo,
        String codigo
) {}