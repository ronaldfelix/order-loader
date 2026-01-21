package org.dinet.pedido.application.dto;

import java.io.InputStream;

public record ArchivoCSV(
        String nombreArchivo,
        InputStream contenido,
        long tamanoBytes
) {
    public ArchivoCSV {
        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            throw new IllegalArgumentException("El nombre del archivo no puede estar vacío");
        }
        if (contenido == null) {
            throw new IllegalArgumentException("El contenido del archivo no puede ser nulo");
        }
        if (tamanoBytes <= 0) {
            throw new IllegalArgumentException("El tamaño del archivo debe ser mayor a 0");
        }
    }
}

