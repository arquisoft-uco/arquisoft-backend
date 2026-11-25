package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.primaryadapter.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record EvaluacionResponseDTO(
        UUID id,
        EntregableDTO entregable,
        EstadoDTO estado,
        NotasDTO notas,
        ResultadoDTO resultado
) {

    public record EntregableDTO(UUID id, String proyecto, Integer version) {}

    public record EstadoDTO(String id, String nombre) {}

    public record NotasDTO(BigDecimal notaAsesor, BigDecimal notaJurados, BigDecimal notaFinal) {}

    public record ResultadoDTO(String id, String nombre) {}
}
