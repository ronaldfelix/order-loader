package org.dinet.pedido.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Pedido {
    private final UUID id;
    private final NumeroPedido numeroPedido;
    private final ClienteId clienteId;
    private final ZonaId zonaId;
    private final LocalDate fechaEntrega;
    private EstadoPedido estado;
    private final boolean requiereRefrigeracion;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Pedido(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.numeroPedido = builder.numeroPedido;
        this.clienteId = builder.clienteId;
        this.zonaId = builder.zonaId;
        this.fechaEntrega = builder.fechaEntrega;
        this.estado = builder.estado;
        this.requiereRefrigeracion = builder.requiereRefrigeracion;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public NumeroPedido getNumeroPedido() { return numeroPedido; }
    public ClienteId getClienteId() { return clienteId; }
    public ZonaId getZonaId() { return zonaId; }
    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public EstadoPedido getEstado() { return estado; }
    public boolean requiereRefrigeracion() { return requiereRefrigeracion; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public static Builder builder() {
        return new Builder();
    }

    //builder personalizado
    public static class Builder {
        private UUID id;
        private NumeroPedido numeroPedido;
        private ClienteId clienteId;
        private ZonaId zonaId;
        private LocalDate fechaEntrega;
        private EstadoPedido estado;
        private boolean requiereRefrigeracion;
        private LocalDateTime createdAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder numeroPedido(NumeroPedido numeroPedido) { this.numeroPedido = numeroPedido; return this; }
        public Builder clienteId(ClienteId clienteId) { this.clienteId = clienteId; return this; }
        public Builder zonaId(ZonaId zonaId) { this.zonaId = zonaId; return this; }
        public Builder fechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; return this; }
        public Builder estado(EstadoPedido estado) { this.estado = estado; return this; }
        public Builder requiereRefrigeracion(boolean requiereRefrigeracion) { this.requiereRefrigeracion = requiereRefrigeracion; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Pedido build() { return new Pedido(this); }
    }
}

