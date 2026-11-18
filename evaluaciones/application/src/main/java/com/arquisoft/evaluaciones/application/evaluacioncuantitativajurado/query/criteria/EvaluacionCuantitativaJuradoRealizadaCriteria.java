package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria;

import java.util.UUID;

public record EvaluacionCuantitativaJuradoRealizadaCriteria(
        UUID evaluacionJurado,
        UUID jurado
) {
}
