package org.dinet.pedido.domain.model;

import org.dinet.pedido.domain.exception.sub.EstadoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstadoPedidoTest {

    @Test
    void deberiaConvertirStringValidoAPENDIENTE() {
        EstadoPedido estado = EstadoPedido.fromString("PENDIENTE");
        assertEquals(EstadoPedido.PENDIENTE, estado);
    }

    @Test
    void deberiaConvertirStringValidoACONFIRMADO() {
        EstadoPedido estado = EstadoPedido.fromString("CONFIRMADO");
        assertEquals(EstadoPedido.CONFIRMADO, estado);
    }

    @Test
    void deberiaConvertirStringValidoAENTREGADO() {
        EstadoPedido estado = EstadoPedido.fromString("ENTREGADO");
        assertEquals(EstadoPedido.ENTREGADO, estado);
    }

    @Test
    void deberiaRechazarEstadoInvalido() {
        assertThrows(EstadoInvalidoException.class, () ->
            EstadoPedido.fromString("INVALIDO")
        );
    }

    @Test
    void deberiaRechazarEstadoNulo() {
        assertThrows(EstadoInvalidoException.class, () ->
            EstadoPedido.fromString(null)
        );
    }

    @Test
    void deberiaRechazarEstadoVacio() {
        assertThrows(EstadoInvalidoException.class, () ->
            EstadoPedido.fromString("")
        );
    }

    @Test
    void deberiaSerCaseInsensitive() {
        assertEquals(EstadoPedido.PENDIENTE, EstadoPedido.fromString("pendiente"));
        assertEquals(EstadoPedido.CONFIRMADO, EstadoPedido.fromString("confirmado"));
        assertEquals(EstadoPedido.ENTREGADO, EstadoPedido.fromString("entregado"));
    }
}

