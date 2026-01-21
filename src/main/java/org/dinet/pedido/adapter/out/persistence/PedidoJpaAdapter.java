package org.dinet.pedido.adapter.out.persistence;

import org.dinet.pedido.application.port.out.PedidoRepositoryPort;
import org.dinet.pedido.domain.model.Pedido;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class PedidoJpaAdapter implements PedidoRepositoryPort {

    private final JdbcTemplate jdbcTemplate;

    public PedidoJpaAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void guardarLote(List<Pedido> pedidos) {
        String sql = """
            INSERT INTO pedidos (id, numero_pedido, cliente_id, zona_id, fecha_entrega, 
                                 estado, requiere_refrigeracion, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        jdbcTemplate.batchUpdate(sql, pedidos, pedidos.size(), (ps, pedido) -> {
            ps.setObject(1, pedido.getId());
            ps.setString(2, pedido.getNumeroPedido().value());
            ps.setString(3, pedido.getClienteId().value());
            ps.setString(4, pedido.getZonaId().value());
            ps.setDate(5, Date.valueOf(pedido.getFechaEntrega()));
            ps.setString(6, pedido.getEstado().name());
            ps.setBoolean(7, pedido.requiereRefrigeracion());
            ps.setTimestamp(8, Timestamp.valueOf(pedido.getCreatedAt()));
            ps.setTimestamp(9, Timestamp.valueOf(pedido.getUpdatedAt()));
        });
    }

    @Override
    public Set<String> encontrarNumerosExistentes(Set<String> numerosPedido) {
        if (numerosPedido.isEmpty()) return Set.of();

        String placeholders = String.join(",", numerosPedido.stream()
                .map(n -> "?").toList());
        String sql = "SELECT numero_pedido FROM pedidos WHERE numero_pedido IN (" + placeholders + ")";

        return jdbcTemplate.query(sql, numerosPedido.toArray(), (rs, rowNum) ->
                rs.getString("numero_pedido")
        ).stream().collect(Collectors.toSet());
    }
}
