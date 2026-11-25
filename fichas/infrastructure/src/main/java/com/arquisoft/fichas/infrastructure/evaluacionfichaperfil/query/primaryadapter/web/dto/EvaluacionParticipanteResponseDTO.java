package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.dto;

import com.arquisoft.fichas.infrastructure.estadoevaluacion.query.primaryadapter.web.dto.EstadoEvaluacionParticipanteResponseDTO;

import java.util.UUID;

public record EvaluacionParticipanteResponseDTO(
        UUID id,
        EstadoEvaluacionParticipanteResponseDTO estado
) {
}
