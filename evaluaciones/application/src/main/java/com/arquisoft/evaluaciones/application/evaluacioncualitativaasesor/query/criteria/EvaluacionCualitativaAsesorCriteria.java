package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.criteria;

import java.util.UUID;

public record EvaluacionCualitativaAsesorCriteria(
        UUID evaluacionAsesor,
        UUID asesor
) {
}
