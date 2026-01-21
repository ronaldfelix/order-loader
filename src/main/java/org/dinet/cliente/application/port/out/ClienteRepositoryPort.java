package org.dinet.cliente.application.port.out;

import java.util.Map;
import java.util.Set;

public interface ClienteRepositoryPort {
    Map<String, Boolean> encontrarClientesActivos(Set<String> clienteIds);
}
