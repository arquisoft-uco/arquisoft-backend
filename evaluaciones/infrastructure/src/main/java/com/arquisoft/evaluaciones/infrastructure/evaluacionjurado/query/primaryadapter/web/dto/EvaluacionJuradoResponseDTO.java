package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.dto;

import java.util.UUID;

public record EvaluacionJuradoResponseDTO(
        UUID id,
        JuradoDTO jurado
) {

    public record JuradoDTO(UUID id, String nombre, String email) {}
}
