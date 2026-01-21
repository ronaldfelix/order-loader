CREATE TABLE clientes (
    id VARCHAR(50) PRIMARY KEY,
    activo BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE zonas (
    id VARCHAR(50) PRIMARY KEY,
    soporte_refrigeracion BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE pedidos (
    id UUID PRIMARY KEY,
    numero_pedido VARCHAR(100) NOT NULL UNIQUE,
    cliente_id VARCHAR(50) NOT NULL,
    zona_id VARCHAR(50) NOT NULL,
    fecha_entrega DATE NOT NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('PENDIENTE', 'CONFIRMADO', 'ENTREGADO')),
    requiere_refrigeracion BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_pedidos_estado_fecha ON pedidos(estado, fecha_entrega);

CREATE TABLE cargas_idempotencia (
    id UUID PRIMARY KEY,
    idempotency_key VARCHAR(255) NOT NULL,
    archivo_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_idempotencia UNIQUE (idempotency_key, archivo_hash)
);
