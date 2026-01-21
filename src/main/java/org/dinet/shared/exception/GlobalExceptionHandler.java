package org.dinet.shared.exception;

import org.dinet.pedido.domain.exception.PedidoDomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IdempotenciaErrorException.class)
    public ResponseEntity<ErrorResponse> handleIdempotenciaErrada(IdempotenciaErrorException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("IDEMPOTENCIA_ERRADA", ex.getMessage(), List.of(), getCorrelationId()));
    }

    @ExceptionHandler(PedidoDomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(PedidoDomainException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getErrorCode(), ex.getMessage(), List.of(), getCorrelationId()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("RECURSO_NO_ENCONTRADO", "Recurso no encontrado", List.of(), getCorrelationId()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Error interno del servidor - Correlation ID: {}", getCorrelationId(), ex);
        String errorDetails = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getName();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("ERROR_INTERNO", "Error interno del servidor: " + errorDetails, List.of(), getCorrelationId()));
    }

    private String getCorrelationId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            Object correlationId = attributes.getRequest().getAttribute("correlationId");
            if (correlationId != null) return correlationId.toString();
        }
        return UUID.randomUUID().toString();
    }

    public record ErrorResponse(String code, String message, List<String> details, String correlationId) {}
}
