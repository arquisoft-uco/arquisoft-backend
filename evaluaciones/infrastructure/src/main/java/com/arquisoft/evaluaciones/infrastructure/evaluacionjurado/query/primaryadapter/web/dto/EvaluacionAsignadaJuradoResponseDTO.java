package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.dto;

import java.util.UUID;

public record EvaluacionAsignadaJuradoResponseDTO(
        UUID id,
        EstadoDTO estado,
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
