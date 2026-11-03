package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.criteria;

import java.util.UUID;

public record EvaluacionCuantitativaAsesorCriteria(
        UUID evaluacionAsesor,
        UUID asesor
) {
}
