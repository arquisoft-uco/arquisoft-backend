package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.dto;

import java.time.Instant;
import java.util.UUID;

public record EvaluacionFichaPerfilResponseDTO(
        UUID id,
        UUID fichaPerfilId,
        Instant fechaCreacion,
        String estadoEvaluacion,
        String estadoEvaluacionNombre
) {
}
