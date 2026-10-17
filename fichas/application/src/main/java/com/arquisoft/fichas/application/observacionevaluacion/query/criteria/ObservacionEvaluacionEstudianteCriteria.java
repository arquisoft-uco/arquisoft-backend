package com.arquisoft.fichas.application.observacionevaluacion.query.criteria;

import java.util.UUID;

public record ObservacionEvaluacionEstudianteCriteria(
        UUID evaluacionFichaPerfil,
        UUID estudiante
) {
}
