package org.dinet.pedido.adapter.out.persistence;

import org.dinet.pedido.application.port.out.IdempotenciaRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class IdempotenciaJpaAdapter implements IdempotenciaRepositoryPort {

    private final JdbcTemplate jdbcTemplate;

    public IdempotenciaJpaAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean existePorKeyYHash(String idempotencyKey, String archivoHash) {
        String sql = "SELECT COUNT(*) FROM cargas_idempotencia WHERE idempotency_key = ? AND archivo_hash = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idempotencyKey, archivoHash);
        return count != null && count > 0;
    }

    @Override
    public void registrar(String idempotencyKey, String archivoHash) {
        String sql = "INSERT INTO cargas_idempotencia (id, idempotency_key, archivo_hash, created_at) VALUES (?, ?, ?, NOW())";
        jdbcTemplate.update(sql, UUID.randomUUID(), idempotencyKey, archivoHash);
    }
}
