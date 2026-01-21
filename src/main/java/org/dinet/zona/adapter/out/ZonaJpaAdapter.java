package org.dinet.zona.adapter.out;


import org.dinet.zona.application.port.out.ZonaRepositoryPort;

import org.dinet.zona.domain.model.Zona;
import org.dinet.zona.domain.model.ZonaId;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class ZonaJpaAdapter implements ZonaRepositoryPort {

    private final JdbcTemplate jdbcTemplate;

    public ZonaJpaAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<String, Zona> encontrarZonasPorIds(Set<String> zonaIds) {
        if (zonaIds.isEmpty()) return Map.of();

        String placeholders = String.join(",", zonaIds.stream().map(n -> "?").toList());
        String sql = "SELECT id, soporte_refrigeracion FROM zonas WHERE id IN (" + placeholders + ")";

        return jdbcTemplate.query(sql, zonaIds.toArray(), (rs, rowNum) ->
                new Zona(new ZonaId(rs.getString("id")), rs.getBoolean("soporte_refrigeracion"))
        ).stream().collect(Collectors.toMap(z -> z.id().value(), z -> z));
    }
}
