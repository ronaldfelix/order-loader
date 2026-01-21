package org.dinet.pedido.application.dto;

public record FilaCSV(
        int linea,
        String numeroPedido,
        String clienteId,
        String fechaEntrega,
        String estado,
        String zonaEntrega,
        String requiereRefrigeracion
) {
}

