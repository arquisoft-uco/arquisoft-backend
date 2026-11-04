package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.primaryadapter.web.dto;

import java.util.UUID;

public record EvaluacionResponseDTO(
        UUID id,
        EntregableDTO entregable,
        EstadoDTO estado
) {

    public record EntregableDTO(UUID id, String proyecto, Integer version) {}

    public record EstadoDTO(String id, String nombre) {}
}
