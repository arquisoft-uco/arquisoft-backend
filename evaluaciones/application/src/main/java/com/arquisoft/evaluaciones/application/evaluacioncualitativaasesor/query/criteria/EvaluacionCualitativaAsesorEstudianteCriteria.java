package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.criteria;

import java.util.UUID;

public record EvaluacionCualitativaAsesorEstudianteCriteria(
        UUID evaluacionAsesor,
        UUID estudiante
) {
}
