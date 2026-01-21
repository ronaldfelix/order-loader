package org.dinet.pedido.domain.service;

import org.dinet.pedido.domain.exception.sub.CadenaFrioNoSoportadaException;
import org.dinet.pedido.domain.exception.sub.FechaEntregaInvalidaException;
import org.dinet.pedido.domain.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ValidadorPedidoServiceTest {

    private final ValidadorPedidoService validador = new ValidadorPedidoService();

    @Test
    void deberiaLanzarExcepcionCuandoFechaEsPasada() {
        LocalDate fechaPasada = LocalDate.now().minusDays(1);

        assertThrows(FechaEntregaInvalidaException.class, () ->
                validador.validarFechaEntrega(fechaPasada)
        );
    }

    @Test
    void deberiaPermitirFechaFutura() {
        LocalDate fechaFutura = LocalDate.now().plusDays(5);

        assertDoesNotThrow(() -> validador.validarFechaEntrega(fechaFutura));
    }

    @Test
    void deberiaLanzarExcepcionCuandoRequiereRefrigeracionYZonaNoSoporta() {
        Pedido pedido = Pedido.builder()
                .numeroPedido(new NumeroPedido("P001"))
                .clienteId(new ClienteId("CLI-123"))
                .zonaId(new ZonaId("ZONA2"))
                .fechaEntrega(LocalDate.now().plusDays(1))
                .estado(EstadoPedido.PENDIENTE)
                .requiereRefrigeracion(true)
                .build();

        assertThrows(CadenaFrioNoSoportadaException.class, () ->
                validador.validarRefrigeracion(pedido, false)
        );
    }

    @Test
    void deberiaPermitirCuandoZonaSoportaRefrigeracion() {
        Pedido pedido = Pedido.builder()
                .numeroPedido(new NumeroPedido("P001"))
                .clienteId(new ClienteId("CLI-123"))
                .zonaId(new ZonaId("ZONA1"))
                .fechaEntrega(LocalDate.now().plusDays(1))
                .estado(EstadoPedido.PENDIENTE)
                .requiereRefrigeracion(true)
                .build();

        assertDoesNotThrow(() -> validador.validarRefrigeracion(pedido, true));
    }
}
