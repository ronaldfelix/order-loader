package org.dinet.cliente.adapter.out;

import lombok.RequiredArgsConstructor;
import org.dinet.cliente.application.port.out.ClienteRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class ClienteJpaAdapter implements ClienteRepositoryPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Boolean> encontrarClientesActivos(Set<String> clienteIds) {
        if (clienteIds.isEmpty()) return new HashMap<>();

        String placeholders = String.join(",", Collections.nCopies(clienteIds.size(), "?"));
        String sql = "SELECT id, activo FROM clientes WHERE id IN (" + placeholders + ")";

        Map<String, Boolean> resultado = new HashMap<>();

        jdbcTemplate.query(sql, clienteIds.toArray(), (rs) -> {
            resultado.put(
                    rs.getString("id"),
                    rs.getBoolean("activo")
            );
        });

        return resultado;
    }

}
