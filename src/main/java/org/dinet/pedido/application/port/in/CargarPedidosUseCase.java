package org.dinet.pedido.application.port.in;

import org.dinet.pedido.application.dto.ArchivoCSV;
import org.dinet.pedido.application.dto.ResultadoCarga;

public interface CargarPedidosUseCase {
    ResultadoCarga cargarPedidos(ArchivoCSV archivo, String idempotencyKey);
}
