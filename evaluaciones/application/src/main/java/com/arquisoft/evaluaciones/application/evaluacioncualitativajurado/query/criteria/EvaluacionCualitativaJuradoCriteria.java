package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria;

import java.util.UUID;

public record EvaluacionCualitativaJuradoCriteria(
        UUID evaluacionJuradoId,
        UUID estudianteId
) {
}
