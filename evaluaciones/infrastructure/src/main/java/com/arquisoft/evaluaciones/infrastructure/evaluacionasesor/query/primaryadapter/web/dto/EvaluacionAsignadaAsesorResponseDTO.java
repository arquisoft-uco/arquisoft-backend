package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.dto;

import java.util.UUID;

public record EvaluacionAsignadaAsesorResponseDTO(
        UUID id,
        EvaluacionDTO evaluacion,
        EntregableDTO entregable,
        boolean tieneEvaluacionesCualitativas,
        boolean tieneEvaluacionesCuantitativas
) {

    public record EvaluacionDTO(UUID id, EstadoDTO estado) {
    }

    public record EstadoDTO(String id, String nombre) {
    }

    public record EntregableDTO(UUID id, String proyecto, Integer version) {
    }
}
