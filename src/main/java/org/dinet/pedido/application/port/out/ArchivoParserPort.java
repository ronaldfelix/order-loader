package org.dinet.pedido.application.port.out;

import org.dinet.pedido.application.dto.FilaCSV;

import java.io.InputStream;
import java.util.stream.Stream;

public interface ArchivoParserPort {
    Stream<FilaCSV> parsearStream(InputStream contenido);
}

