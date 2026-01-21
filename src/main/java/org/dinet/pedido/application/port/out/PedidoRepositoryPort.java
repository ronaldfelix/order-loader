package org.dinet.pedido.application.port.out;

import org.dinet.pedido.domain.model.Pedido;

import java.util.List;
import java.util.Set;

public interface PedidoRepositoryPort {
    void guardarLote(List<Pedido> pedidos);
    Set<String> encontrarNumerosExistentes(Set<String> numerosPedido);
}
