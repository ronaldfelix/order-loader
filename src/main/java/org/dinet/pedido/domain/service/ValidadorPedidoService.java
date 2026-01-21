package org.dinet.pedido.domain.service;

import org.dinet.pedido.domain.exception.sub.CadenaFrioNoSoportadaException;
import org.dinet.pedido.domain.exception.sub.FechaEntregaInvalidaException;
import org.dinet.pedido.domain.model.Pedido;

import java.time.LocalDate;
import java.time.ZoneId;

public class ValidadorPedidoService {
    private static final ZoneId LIMA_ZONE = ZoneId.of("America/Lima");

    public void validarFechaEntrega(LocalDate fechaEntrega) {
        LocalDate hoy = LocalDate.now(LIMA_ZONE);
        if (fechaEntrega.isBefore(hoy)) {
            throw new FechaEntregaInvalidaException("La fecha de entrega no puede ser pasada: " + fechaEntrega);
        }
    }

    public void validarRefrigeracion(Pedido pedido, boolean zonaSoportaRefrigeracion) {
        if (pedido.requiereRefrigeracion() && !zonaSoportaRefrigeracion) {
            throw new CadenaFrioNoSoportadaException(
                    "La zona " + pedido.getZonaId().value() + " no soporta refrigeración"
            );
        }
    }
}
