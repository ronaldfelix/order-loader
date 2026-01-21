package org.dinet.pedido.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class ClienteIdTest {

    @Test
    void deberiaCrearClienteIdValido() {
        ClienteId clienteId = new ClienteId("CLI-123");

        assertNotNull(clienteId);
        assertEquals("CLI-123", clienteId.value());
    }

    @Test
    void deberiaRechazarClienteIdNulo() {
        assertThrows(IllegalArgumentException.class, () ->
            new ClienteId(null)
        );
    }

    @Test
    void deberiaRechazarClienteIdVacio() {
        assertThrows(IllegalArgumentException.class, () ->
            new ClienteId("")
        );
    }

    @Test
    void deberiaRechazarClienteIdBlanco() {
        assertThrows(IllegalArgumentException.class, () ->
            new ClienteId("   ")
        );
    }

    @Test
    void deberiaImplementarEqualsCorrectamente() {
        ClienteId cliente1 = new ClienteId("CLI-123");
        ClienteId cliente2 = new ClienteId("CLI-123");
        ClienteId cliente3 = new ClienteId("CLI-456");

        assertEquals(cliente1, cliente2);
        assertNotEquals(cliente1, cliente3);
    }
}

