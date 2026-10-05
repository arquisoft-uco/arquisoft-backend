package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.primaryadapter.web.dto;

import java.util.UUID;

public record ObservacionEvaluacionResponseDTO(
        UUID id,
        UUID evaluacionFichaPerfil,
        String observacion
) {
}
