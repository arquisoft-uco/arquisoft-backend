package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.dto;

import com.arquisoft.fichas.infrastructure.representantecomite.query.primaryadapter.web.dto.RepresentanteComiteResponseDTO;

import java.time.Instant;
import java.util.UUID;

public record EvaluacionFichaPerfilEstudianteResponseDTO(
        UUID id,
        UUID fichaPerfil,
        Instant fechaCreacion,
        String estadoEvaluacion,
        String estadoEvaluacionNombre,
        RepresentanteComiteResponseDTO representanteComite
) {
}
