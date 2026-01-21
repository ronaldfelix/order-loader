package org.dinet.pedido.application.port.out;

import java.util.Optional;

public interface IdempotenciaRepositoryPort {
    boolean existePorKeyYHash(String idempotencyKey, String archivoHash);
    void registrar(String idempotencyKey, String archivoHash);
}
