package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.criteria;

import java.util.UUID;

public record EvaluacionCuantitativaAsesorEstudianteCriteria(
        UUID evaluacionAsesor,
        UUID estudiante
) {
}
