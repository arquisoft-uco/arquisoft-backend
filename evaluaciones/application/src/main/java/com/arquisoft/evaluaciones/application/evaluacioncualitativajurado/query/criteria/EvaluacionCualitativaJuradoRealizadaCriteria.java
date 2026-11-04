package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria;

import java.util.UUID;

public record EvaluacionCualitativaJuradoRealizadaCriteria(
        UUID evaluacionJurado,
        UUID jurado
) {
}
