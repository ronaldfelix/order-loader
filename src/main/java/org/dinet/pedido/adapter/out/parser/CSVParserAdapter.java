package org.dinet.pedido.adapter.out.parser;

import org.dinet.pedido.application.dto.FilaCSV;
import org.dinet.pedido.application.port.out.ArchivoParserPort;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
public class CSVParserAdapter implements ArchivoParserPort {

    @Override
    public Stream<FilaCSV> parsearStream(InputStream contenido) {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(contenido, StandardCharsets.UTF_8));

        try {
            // omito cabecera csc
            String headerLine = reader.readLine();
            if (headerLine == null) {
                reader.close();
                return Stream.empty();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer cabecera del CSV", e);
        }

        AtomicInteger lineNumber = new AtomicInteger(2); // considera que la cabecera es 1

        Iterator<FilaCSV> iterator = new Iterator<>() {
            private String nextLine;
            private boolean hasReadNext = false;

            @Override
            public boolean hasNext() {
                if (!hasReadNext) {
                    try {
                        nextLine = reader.readLine();
                        hasReadNext = true;
                    } catch (IOException e) {
                        throw new RuntimeException("Error al leer archivo CSV", e);
                    }
                }
                return nextLine != null;
            }

            @Override
            public FilaCSV next() {
                if (!hasReadNext) {
                    hasNext();
                }
                hasReadNext = false;

                String line = nextLine;
                String[] campos = line.split(",", -1);

                if (campos.length != 6) {
                    return null; // omito en caso de que la línea no tenga 6 campos
                }

                return new FilaCSV(
                        lineNumber.getAndIncrement(),
                        campos[0].trim(),
                        campos[1].trim(),
                        campos[2].trim(),
                        campos[3].trim(),
                        campos[4].trim(),
                        campos[5].trim()
                );
            }
        };

        return StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                        false)
                .filter(fila -> fila != null)
                .onClose(() -> {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        throw new RuntimeException("Error al cerrar BufferedReader", e);
                    }
                });
    }
}

