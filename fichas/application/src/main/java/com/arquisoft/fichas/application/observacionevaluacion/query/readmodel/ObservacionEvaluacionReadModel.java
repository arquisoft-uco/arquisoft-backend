package com.arquisoft.fichas.application.observacionevaluacion.query.readmodel;

import java.util.UUID;

public record ObservacionEvaluacionReadModel(
        UUID id,
        UUID evaluacionFichaPerfil,
        String observacion
) {
}
