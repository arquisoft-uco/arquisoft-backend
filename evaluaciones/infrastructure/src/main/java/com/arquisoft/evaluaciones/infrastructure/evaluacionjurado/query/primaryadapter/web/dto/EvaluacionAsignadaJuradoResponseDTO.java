package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.dto;

import java.util.UUID;

public record EvaluacionAsignadaJuradoResponseDTO(
        UUID id,
        EvaluacionDTO evaluacion,
        EntregableDTO entregable
) {

    public record EvaluacionDTO(UUID id, EstadoDTO estado) {
    }

    public record EstadoDTO(String id, String nombre) {
    }

    public record EntregableDTO(UUID id, String proyecto, Integer version) {
    }
}
