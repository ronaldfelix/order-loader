package org.dinet.pedido.application.service;

import jakarta.transaction.Transactional;
import org.dinet.cliente.application.port.out.ClienteRepositoryPort;
import org.dinet.pedido.application.dto.ArchivoCSV;
import org.dinet.pedido.application.dto.ErrorDetalle;
import org.dinet.pedido.application.dto.FilaCSV;
import org.dinet.pedido.application.dto.ResultadoCarga;
import org.dinet.pedido.application.port.in.CargarPedidosUseCase;
import org.dinet.pedido.application.port.out.ArchivoParserPort;
import org.dinet.pedido.application.port.out.IdempotenciaRepositoryPort;
import org.dinet.pedido.application.port.out.PedidoRepositoryPort;
import org.dinet.pedido.domain.exception.sub.ClienteNoEncontradoException;
import org.dinet.pedido.domain.exception.PedidoDomainException;
import org.dinet.pedido.domain.exception.sub.PedidoDuplicadoException;
import org.dinet.pedido.domain.exception.sub.ZonaInvalidaException;
import org.dinet.pedido.domain.model.*;
import org.dinet.pedido.domain.service.ValidadorPedidoService;
import org.dinet.shared.exception.IdempotenciaErrorException;
import org.dinet.zona.application.port.out.ZonaRepositoryPort;
import org.dinet.zona.domain.model.Zona;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class CargarPedidosService implements CargarPedidosUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CargarPedidosService.class);
    private static final int CHUNK_SIZE = 10_000; // Tamaño del chunk para precess por stream

    @Value("${batch.size:500}")
    private int batchSize;

    private final PedidoRepositoryPort pedidoRepository;
    private final ClienteRepositoryPort clienteRepository;
    private final ZonaRepositoryPort zonaRepository;
    private final IdempotenciaRepositoryPort idempotenciaRepository;
    private final ArchivoParserPort archivoParser;
    private final ValidadorPedidoService validadorPedido = new ValidadorPedidoService();

    public CargarPedidosService(
            PedidoRepositoryPort pedidoRepository,
            ClienteRepositoryPort clienteRepository,
            ZonaRepositoryPort zonaRepository,
            IdempotenciaRepositoryPort idempotenciaRepository,
            ArchivoParserPort archivoParser) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.zonaRepository = zonaRepository;
        this.idempotenciaRepository = idempotenciaRepository;
        this.archivoParser = archivoParser;
    }

    @Override
    @Transactional
    public ResultadoCarga cargarPedidos(ArchivoCSV archivo, String idempotencyKey) {
        logger.info("Iniciando carga de archivo: {} ({}  bytes) con idempotency key: {}",
                archivo.nombreArchivo(), archivo.tamanoBytes(), idempotencyKey);

        byte[] contenidoBytes;

        try (var inputStream = archivo.contenido()) {
            contenidoBytes = inputStream.readAllBytes();
            logger.debug("Archivo leído exitosamente: {} bytes", contenidoBytes.length);
        } catch (IOException e) {
            logger.error("Error al leer el archivo: {}", archivo.nombreArchivo(), e);
            throw new RuntimeException("Error al leer el archivo", e);
        }

        String archivoHash = calcularHashSHA256(contenidoBytes);
        logger.debug("Hash del archivo calculado: {}", archivoHash);

        if (idempotenciaRepository.existePorKeyYHash(idempotencyKey, archivoHash)) {
            logger.warn("Intento de procesar archivo duplicado - idempotencyKey: {}, hash: {}",
                    idempotencyKey, archivoHash);
            throw new IdempotenciaErrorException("Archivo ya procesado con esta key");
        }

        logger.debug("Parseando archivo CSV en modo streaming...");
        ResultadoCarga resultado;

        try (var stream = archivoParser.parsearStream(new ByteArrayInputStream(contenidoBytes))) {
            resultado = procesarPedidosEnChunks(stream);
        }

        idempotenciaRepository.registrar(idempotencyKey, archivoHash);
        logger.info("Carga completada - Total: {}, Guardados: {}, Con error: {}",
                resultado.totalProcesados(), resultado.guardados(), resultado.conError());

        return resultado;
    }

    private ResultadoCarga procesarPedidosEnChunks(Stream<FilaCSV> filasStream) {
        List<ErrorDetalle> erroresGlobales = new ArrayList<>();
        int totalProcesados = 0;
        int totalGuardados = 0;
        Set<String> numerosPedidoGlobales = new HashSet<>();

        Iterator<FilaCSV> iterator = filasStream.iterator();

        while (iterator.hasNext()) {
            // Leer chunk
            List<FilaCSV> chunk = new ArrayList<>(CHUNK_SIZE);
            for (int i = 0; i < CHUNK_SIZE && iterator.hasNext(); i++) {
                chunk.add(iterator.next());
            }

            if (chunk.isEmpty()) {
                break;
            }

            logger.info("Procesando chunk de {} filas (total acumulado: {})", chunk.size(), totalProcesados + chunk.size());

            Set<String> clienteIds = chunk.stream()
                    .map(FilaCSV::clienteId)
                    .collect(Collectors.toSet());
            Set<String> zonaIds = chunk.stream()
                    .map(FilaCSV::zonaEntrega)
                    .collect(Collectors.toSet());
            Set<String> numerosPedido = chunk.stream()
                    .map(FilaCSV::numeroPedido)
                    .collect(Collectors.toSet());

            Map<String, Boolean> clientesActivos = clienteRepository.encontrarClientesActivos(clienteIds);
            Map<String, Zona> zonas = zonaRepository.encontrarZonasPorIds(zonaIds);
            Set<String> pedidosExistentes = pedidoRepository.encontrarNumerosExistentes(numerosPedido);

            List<ErrorDetalle> erroresChunk = new ArrayList<>();
            List<Pedido> pedidosValidos = new ArrayList<>();

            for (FilaCSV fila : chunk) {
                try {
                    if (!numerosPedidoGlobales.add(fila.numeroPedido())) {
                        throw new PedidoDuplicadoException(fila.numeroPedido());
                    }
                    validarYAgregar(fila, clientesActivos, zonas, pedidosExistentes, pedidosValidos);
                } catch (PedidoDomainException e) {
                    erroresChunk.add(new ErrorDetalle(fila.linea(), e.getMessage(), e.getErrorCode()));
                }
            }

            if (!pedidosValidos.isEmpty()) {
                guardarEnLotes(pedidosValidos);
                totalGuardados += pedidosValidos.size();
            }

            erroresGlobales.addAll(erroresChunk);
            totalProcesados += chunk.size();

            logger.debug("Chunk procesado: {} válidos, {} errores", pedidosValidos.size(), erroresChunk.size());
        }

        Map<String, Integer> erroresAgrupados = erroresGlobales.stream()
                .collect(Collectors.groupingBy(
                        ErrorDetalle::codigo,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        return new ResultadoCarga(
                totalProcesados,
                totalGuardados,
                erroresGlobales.size(),
                erroresGlobales,
                erroresAgrupados
        );
    }

    private void validarYAgregar(
            FilaCSV fila,
            Map<String, Boolean> clientesActivos,
            Map<String, Zona> zonas,
            Set<String> pedidosExistentes,
            List<Pedido> pedidosValidos) {

        if (pedidosExistentes.contains(fila.numeroPedido())) {
            throw new PedidoDuplicadoException(fila.numeroPedido());
        }

        Boolean clienteActivo = clientesActivos.get(fila.clienteId());
        if (clienteActivo == null || !clienteActivo) {
            throw new ClienteNoEncontradoException(fila.clienteId());
        }

        Zona zona = zonas.get(fila.zonaEntrega());
        if (zona == null) {
            throw new ZonaInvalidaException(fila.zonaEntrega());
        }

        NumeroPedido numeroPedido = new NumeroPedido(fila.numeroPedido());
        ClienteId clienteId = new ClienteId(fila.clienteId());
        ZonaId zonaId = new ZonaId(fila.zonaEntrega());
        LocalDate fechaEntrega = LocalDate.parse(fila.fechaEntrega());
        EstadoPedido estado = EstadoPedido.fromString(fila.estado());
        boolean requiereRefrigeracion = Boolean.parseBoolean(fila.requiereRefrigeracion());

        Pedido pedido = Pedido.builder()
                .numeroPedido(numeroPedido)
                .clienteId(clienteId)
                .zonaId(zonaId)
                .fechaEntrega(fechaEntrega)
                .estado(estado)
                .requiereRefrigeracion(requiereRefrigeracion)
                .build();

        validadorPedido.validarFechaEntrega(fechaEntrega);
        validadorPedido.validarRefrigeracion(pedido, zona.soportaRefrigeracion());

        pedidosValidos.add(pedido);
    }

    private void guardarEnLotes(List<Pedido> pedidos) {
        for (int i = 0; i < pedidos.size(); i += batchSize) {
            int end = Math.min(i + batchSize, pedidos.size());
            List<Pedido> lote = pedidos.subList(i, end);
            pedidoRepository.guardarLote(lote);
        }
    }

    private String calcularHashSHA256(byte[] contenidoBytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(contenidoBytes);
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error al calcular hash del archivo", e);
        }
    }
}
