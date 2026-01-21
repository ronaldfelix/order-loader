package org.dinet.pedido.application.dto;

import java.util.List;
import java.util.Map;

public record ResultadoCarga(
        int totalProcesados,
        int guardados,
        int conError,
        List<ErrorDetalle> erroresDetalle,
        Map<String, Integer> erroresAgrupados
) {}

