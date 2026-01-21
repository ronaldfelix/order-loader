package org.dinet.pedido.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dinet.pedido.application.dto.ArchivoCSV;
import org.dinet.pedido.application.dto.ResultadoCarga;
import org.dinet.pedido.application.port.in.CargarPedidosUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Pedidos")
@SecurityRequirement(name = "bearer-jwt")
@RequiredArgsConstructor
public class PedidoRestController {

    private final CargarPedidosUseCase cargarPedidosUseCase;

    @Operation(summary = "Cargar pedidos desde CSV", description = "Carga un archivo CSV con pedidos y devuelve el resultado de la carga")
    @PostMapping(value = "/cargar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultadoCarga> cargarPedidos(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Idempotency-Key") String idempotencyKey) throws IOException {

        ArchivoCSV archivoCSV = new ArchivoCSV(
                file.getOriginalFilename(),
                file.getInputStream(),
                file.getSize()
        );

        ResultadoCarga resultado = cargarPedidosUseCase.cargarPedidos(archivoCSV, idempotencyKey);
        return ResponseEntity.ok(resultado);
    }
}
