package org.dinet.pedido.domain.model;

import org.dinet.pedido.domain.exception.sub.NumeroPedidoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumeroPedidoTest {

    @Test
    void deberiaCrearNumeroPedidoValido() {
        NumeroPedido numero = new NumeroPedido("P001");

        assertNotNull(numero);
        assertEquals("P001", numero.value());
    }

    @Test
    void deberiaAceptarNumeroPedidoAlfanumerico() {
        assertDoesNotThrow(() -> new NumeroPedido("ABC123"));
        assertDoesNotThrow(() -> new NumeroPedido("P001"));
        assertDoesNotThrow(() -> new NumeroPedido("ORDER999"));
    }

    @Test
    void deberiaRechazarNumeroPedidoNulo() {
        assertThrows(NumeroPedidoInvalidoException.class, () ->
            new NumeroPedido(null)
        );
    }

    @Test
    void deberiaRechazarNumeroPedidoVacio() {
        assertThrows(NumeroPedidoInvalidoException.class, () ->
            new NumeroPedido("")
        );
    }

    @Test
    void deberiaRechazarNumeroPedidoBlanco() {
        assertThrows(NumeroPedidoInvalidoException.class, () ->
            new NumeroPedido("   ")
        );
    }

    @Test
    void deberiaRechazarNumeroPedidoConGuion() {
        assertThrows(NumeroPedidoInvalidoException.class, () ->
            new NumeroPedido("P-001")
        );
    }


    @Test
    void deberiaRechazarNumeroPedidoConCaracteresEspeciales() {
        assertThrows(NumeroPedidoInvalidoException.class, () ->
            new NumeroPedido("P@001")
        );
        assertThrows(NumeroPedidoInvalidoException.class, () ->
            new NumeroPedido("P#001")
        );
    }

    @Test
    void deberiaImplementarEqualsCorrectamente() {
        NumeroPedido numero1 = new NumeroPedido("P001");
        NumeroPedido numero2 = new NumeroPedido("P001");
        NumeroPedido numero3 = new NumeroPedido("P002");

        assertEquals(numero1, numero2);
        assertNotEquals(numero1, numero3);
    }

    @Test
    void deberiaImplementarHashCodeCorrectamente() {
        NumeroPedido numero1 = new NumeroPedido("P001");
        NumeroPedido numero2 = new NumeroPedido("P001");

        assertEquals(numero1.hashCode(), numero2.hashCode());
    }
}

