package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.query.primaryadapter.web.dto;

import java.util.UUID;

public record EvaluacionCualitativaJuradoResponseDTO(
        UUID id,
        ItemDTO item,
        CriterioDTO criterio
) {

    public record ItemDTO(UUID id, String nombre, String descripcion) {
    }

    public record CriterioDTO(UUID id, String nombre, String descripcion) {
    }
}
